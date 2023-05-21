package chessapi4j.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import chessapi4j.Generator;
import chessapi4j.Move;
import chessapi4j.Piece;
import chessapi4j.Position;


/**
 * 
 * @author lunalobos
 *
 */
class MoveGenerator extends AbstractGenerator {
	
	private long[] bits;

	private int checkCount;

	private List<BitPosition> children;

	private long friends, enemys, checkMask, nextWhiteMove, inCheck, inCheckMask;
	private BitPosition position;

	public MoveGenerator(BitPosition position) {
		this.position = position;
		bits = position.getBits();
		int aux = (int) (6L & (position.getWhiteMoveNumeric() << 1 | position.getWhiteMoveNumeric() << 2));
		friends = bits[Piece.BP.ordinal() - aux - 1] | bits[Piece.BN.ordinal() - aux - 1]
				| bits[Piece.BB.ordinal() - aux - 1] | bits[Piece.BR.ordinal() - aux - 1]
				| bits[Piece.BQ.ordinal() - aux - 1] | bits[Piece.BK.ordinal() - aux - 1];
		enemys = bits[Piece.WP.ordinal() + aux - 1] | bits[Piece.WN.ordinal() + aux - 1]
				| bits[Piece.WB.ordinal() + aux - 1] | bits[Piece.WR.ordinal() + aux - 1]
				| bits[Piece.WQ.ordinal() + aux - 1] | bits[Piece.WK.ordinal() + aux - 1];

		nextWhiteMove = (~position.getWhiteMoveNumeric()) & 1L;
		inCheckMask = 0L;
		checkCount = 0;

	}

	private void applyCastleRules(BitPosition position) {
		int whiteMove = (int) position.getWhiteMoveNumeric();

		long[] scMask = CASTLE_MASK[whiteMove][0];
		long[] lcMask = CASTLE_MASK[whiteMove][1];
		int[] scSquares = CASTLE_SQUARES[whiteMove][0];
		int[] lcSquares = CASTLE_SQUARES[whiteMove][1];
		int rookPiece = ROOK_PIECES[whiteMove];
		int kingPiece = KING_PIECES[whiteMove];
		long rookBits = position.getBits()[rookPiece - 1];
		long kingBits = position.getBits()[kingPiece - 1];
		long scRookBitsMasked = (rookBits & scMask[1]) >>> scSquares[1];
		long lcRookBitsMasked = (rookBits & lcMask[1]) >>> lcSquares[1];
		long scKingBitsMasked = (kingBits & scMask[0]) >>> scSquares[0];
		long lcKingBitsMasked = (kingBits & lcMask[0]) >>> lcSquares[0];
		long scBitsMasked = scRookBitsMasked & scKingBitsMasked;
		long lcBitsMasked = lcRookBitsMasked & lcKingBitsMasked;
		long[] scwToSet = new long[] { position.getShortCastleWhiteNumeric(),
				scBitsMasked & position.getShortCastleWhiteNumeric() };
		long[] lcwToSet = new long[] { position.getLongCastleWhiteNumeric(),
				lcBitsMasked & position.getLongCastleWhiteNumeric() };
		long[] scbToSet = new long[] { position.getShortCastleBlackNumeric() & scBitsMasked,
				position.getShortCastleBlackNumeric() };
		long[] lcbToSet = new long[] { position.getLongCastleBlackNumeric() & lcBitsMasked,
				position.getLongCastleBlackNumeric() };
		position.setShortCastleWhiteNumeric(scwToSet[whiteMove]);
		position.setLongCastleWhiteNumeric(lcwToSet[whiteMove]);
		position.setShortCastleBlackNumeric(scbToSet[whiteMove]);
		position.setLongCastleBlackNumeric(lcbToSet[whiteMove]);
	}

	private void applyHalfMoveRule(BitPosition p, long move) {
		int aux = (int) (6L & (position.getWhiteMoveNumeric() << 1 | position.getWhiteMoveNumeric() << 2));
		int friendPawnPiece = Piece.BP.ordinal() - aux;
		long enemyOperation = enemys & move;
		long isEnemyCapture = enemyOperation >>> squaresMap(enemyOperation);
		long pawnOperation = move & p.getBits()[friendPawnPiece - 1];
		long isPawnMove = pawnOperation >>> squaresMap(pawnOperation);
		int[] choice = new int[] { p.getHalfMovesCounter() + 1, 0 };
		p.setHalfMovesCounter(choice[(int) (isPawnMove | isEnemyCapture)]);
	}

	private List<BitPosition> bishopMoves(long br, int square, int pieceType, int[] pawnsDirections, int kingSquare) {
		long defense = defenseDirection(kingSquare, square);
		long pseudoLegalMoves = visibleSquares(position, BISHOP_DIRECTIONS, square);
		long[] pin = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
		long isPin = pin[(int) ((br & checkMask) >>> squaresMap(br & checkMask))];
		long legalMoves = pseudoLegalMoves & isPin & inCheckMask;

		return generatePositions(legalMoves, pieceType, square, pawnsDirections);
	}

	private void createCheckMask(int kingSquare) {
		long empty = ~(enemys | friends);
		int[] enemyRookChoice = new int[] { Piece.WR.ordinal(), Piece.BR.ordinal() };
		int[] enemyQueenChoice = new int[] { Piece.WQ.ordinal(), Piece.BQ.ordinal() };
		int[] enemyBishopChoice = new int[] { Piece.WB.ordinal(), Piece.BB.ordinal() };
		int enemyRook = enemyRookChoice[(int) position.getWhiteMoveNumeric()];
		int enemyQueen = enemyQueenChoice[(int) position.getWhiteMoveNumeric()];
		int enemyBishop = enemyBishopChoice[(int) position.getWhiteMoveNumeric()];
		checkMask = 0L;
		for (int j = 0; j < 4; j++) {
			long visibleEmptyOrFriendsRD = visibleSquares(position.getBits(), new int[] { ROOK_DIRECTIONS[j] },
					kingSquare, nextWhiteMove);
			long friendsRD = visibleEmptyOrFriendsRD & ~empty;
			long[] testBitsRD = bits.clone();
			for (int i = 0; i < 12; i++) {
				testBitsRD[i] = testBitsRD[i] & ~friendsRD;
			}
			long visibleEmptyOrEnemyRD = visibleSquares(testBitsRD, new int[] { ROOK_DIRECTIONS[j] }, kingSquare,
					position.getWhiteMoveNumeric());
			long enemysThreadsRD = visibleEmptyOrEnemyRD & (bits[enemyRook - 1] | bits[enemyQueen - 1]);
			long[] choice = new long[] { 0L, friendsRD | visibleEmptyOrEnemyRD };
			checkMask = checkMask | choice[(int) (enemysThreadsRD >>> squaresMap(enemysThreadsRD))];
		}
		for (int j = 0; j < 4; j++) {
			long visibleEmptyOrFriendsBD = visibleSquares(position.getBits(), new int[] { BISHOP_DIRECTIONS[j] },
					kingSquare, nextWhiteMove);
			long friendsBD = visibleEmptyOrFriendsBD & ~empty;
			long[] testBitsBD = bits.clone();
			for (int i = 0; i < 12; i++) {
				testBitsBD[i] = testBitsBD[i] & ~friendsBD;
			}
			long visibleEmptyOrEnemyBD = visibleSquares(testBitsBD, new int[] { BISHOP_DIRECTIONS[j] }, kingSquare,
					position.getWhiteMoveNumeric());
			long enemysThreadsBD = visibleEmptyOrEnemyBD & (bits[enemyBishop - 1] | bits[enemyQueen - 1]);
			long[] choice = new long[] { 0L, friendsBD | visibleEmptyOrEnemyBD };
			checkMask = checkMask | choice[(int) (enemysThreadsBD >>> squaresMap(enemysThreadsBD))];
		}
		
	}
	
	

	private long defenseDirection(int kingSquare, int pieceSquare) {
		int[][] matrix = QUEEN_MEGAMATRIX[kingSquare];
		long result = 0L;
		int d = 0;
		int[] choice = new int[] { 0, -1 };
		for (int i = 1; i < 9; i++) {
			for (int square : matrix[i - 1]) {
				long operation1 = (1L << pieceSquare) & (1L << square);
				long operation2 = operation1 >>> squaresMap(operation1);
				d = d | (choice[(int) operation2] & i);
			}
		}
		int[][] matrix2 = new int[][] { {}, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5], matrix[6],
				matrix[7] };
		for (int square : matrix2[d]) {
			result = result | (1L << square);
		}
		return result;
	}

	private List<BitPosition> generateCastlePositions(long castleMoves, int kingPiece, int square) {
		List<Long> list = longToList(castleMoves);
		return longToCastlePos(list, kingPiece, square);
	}

	private List<BitPosition> generateCoronations(long legalCoronationMoves, int pieceType, int square,
			int[] pawnsDirections) {
		List<Long> list = longToList(legalCoronationMoves);
		return longToCoronationPos(list, pieceType, square);
	}

	private List<BitPosition> generateEnPassantCaptures(long pseudoLegalMoves, int pieceType, int originSquare,
			int[] pawnsDirections, int[] captureArray) {
		List<Long> list = longToList(pseudoLegalMoves);
		List<BitPosition> pseudoList = longToEnPassantCapture(list, pieceType, originSquare);
		long legalMoves = 0L;
		for (int i = 0; i < list.size(); i++) {
			BitPosition testPosition = (BitPosition) pseudoList.get(i).makeClone();
			testPosition.setWhiteMoveNumeric(position.getWhiteMoveNumeric());
			long check = isInCheck(testPosition, pawnsDirections);
			check = (~check) & (1L);
			legalMoves = legalMoves | (check * list.get(i));
		}
		List<Long> list2 = longToList(legalMoves);
		return longToEnPassantCapture(list2, pieceType, originSquare);
	}

	@Override
	public void generateLegalMoves() {
		inCheckMask = 0L;
		checkCount = 0;
		int whiteMove = (int) position.getWhiteMoveNumeric();
		int pawnPiece = PAWNS[whiteMove];
		int knightPiece = KNIGHTS[whiteMove];
		int bishopPiece = BISHOPS[whiteMove];
		int rookPiece = ROOKS[whiteMove];
		int queenPiece = QUEENS[whiteMove];
		int kingPiece = KINGS[whiteMove];
		int[][] matrix1 = PAWN_MATRIX1[whiteMove];
		int[][] matrix2 = PAWN_MATRIX2[whiteMove];
		int kingSquare = squaresMap(bits[kingPiece - 1]);
		int[][] pawnsDirectionChoice = new int[][] { BLACK_PAWN_MATRIX_2[kingSquare], WHITE_PAWN_MATRIX_2[kingSquare] };
		int[] pawnsDirections = pawnsDirectionChoice[(int) position.getWhiteMoveNumeric()];
		inCheck = isInCheckWhithMask(kingPiece, position.getBits(), position.getWhiteMoveNumeric(), pawnsDirections);

		createCheckMask(kingSquare);
		long[] choice = new long[] { -1L, inCheckMask, 0L, 0L, 0L, 0L };
		inCheckMask = choice[checkCount];
		children = new LinkedList<>();
		// Pawn Moves
		List<Long> pawnList = longToList(bits[pawnPiece - 1]);
		pawnList.stream().forEach(br -> children
				.addAll(pawnMoves(br, squaresMap(br), pawnsDirections, pawnPiece, matrix1, matrix2, kingSquare)));

		// Knight Moves
		List<Long> knightList = longToList(bits[knightPiece - 1]);
		knightList.stream()
				.forEach(br -> children.addAll(knightMoves(br, squaresMap(br), knightPiece, pawnsDirections)));

		// Bishop Moves
		List<Long> bishopList = longToList(bits[bishopPiece - 1]);
		bishopList.stream().forEach(
				br -> children.addAll(bishopMoves(br, squaresMap(br), bishopPiece, pawnsDirections, kingSquare)));

		// Rook Moves
		List<Long> rookList = longToList(bits[rookPiece - 1]);
		rookList.stream().forEach(br -> children.addAll(rookMoves(br, squaresMap(br), rookPiece, pawnsDirections, kingSquare)));

		// Queen Moves
		List<Long> queenList = longToList(bits[queenPiece - 1]);
		queenList.stream().forEach(
				br -> children.addAll(queenMoves(br, squaresMap(br), queenPiece, pawnsDirections, kingSquare)));

		// King Moves
		List<Long> kingList = longToList(bits[kingPiece - 1]);
		kingList.stream().forEach(br -> children.addAll(kingMoves(squaresMap(br), kingPiece)));

	}

	private List<BitPosition> generatePositions(long legalMoves, int pieceType, int square, int[] pawnsDirections) {
		List<Long> list = longToList(legalMoves);
		return longToPos(list, pieceType, square);
	}

	private List<BitPosition> generatePositions(long legalMoves, int pieceType, int square, int[] pawnsDirections,
			int[][] referenceMatrix) {
		List<Long> list = longToList(legalMoves);
		return longToPos(list, pieceType, square);
	}

	private List<BitPosition> generatePositions(long pseudoLegalMoves, long internalCheckMask, int pieceType,
			int originSquare) {
		List<Long> list = longToList(pseudoLegalMoves);
		List<BitPosition> pseudoList = longToPos(list, pieceType, originSquare);
		long legalMoves = 0L;
		for (int i = 0; i < list.size() * (int) internalCheckMask; i++) {
			BitPosition testPosition = (BitPosition) pseudoList.get(i).makeClone();
			testPosition.setWhiteMoveNumeric(position.getWhiteMoveNumeric());
			long check = isInCheck(testPosition);
			check = (~check) & (1L);
			legalMoves = legalMoves | (check * list.get(i));
		}
		long[] arrayMoves = new long[] { pseudoLegalMoves, legalMoves };
		legalMoves = arrayMoves[(int) internalCheckMask];
		List<Long> list2 = longToList(legalMoves);
		return longToPos(list2, pieceType, originSquare);
	}

	private List<BitPosition> generatePositionsWithEnPassant(long legalAdvanceEnPassantMoves, int pieceType, int square,
			int[] pawnsDirections) {
		List<Long> list = longToList(legalAdvanceEnPassantMoves);
		return longToPosEnPassant(list, pieceType, square);
	}

	@Override
	public List<BitPosition> getChildren() {
		return children;
	}

	@Override
	public List<Generator> getChildrenGenerators() {
		List<Generator> gList = new ArrayList<>(children.size());
		for (BitPosition child : children) {
			gList.add(new MoveGenerator(child));
		}
		return gList;
	}

	@Override
	public BitPosition getPosition() {
		return position;
	}

	private long isInCheckWhithMask(int kingPiece, long[] bits, long whiteMoveNumeric, int[] pawnsDirections) {

		int kingSquare = squaresMap(bits[kingPiece - 1]);
		long isInCheck = 0L;
		int aux = (int) (6L & (whiteMoveNumeric << 1 | whiteMoveNumeric << 2));
		int[] enemys = { Piece.WP.ordinal() + aux, Piece.WN.ordinal() + aux, Piece.WB.ordinal() + aux,
				Piece.WR.ordinal() + aux, Piece.WQ.ordinal() + aux, Piece.WK.ordinal() + aux };
		// pawns directions
		int enemyPawn = enemys[0];
		long enemyPawns = bits[enemyPawn - 1];
		for (int pawnDirection : pawnsDirections) {

			long enemyPawnDangerLocation = 1L << pawnDirection;
			long operation = ((enemyPawns & enemyPawnDangerLocation) >>> pawnDirection);
			isInCheck = isInCheck | operation;
			long[] choice = new long[] { 0L, enemyPawnDangerLocation };
			inCheckMask = inCheckMask | choice[(int) operation];
			checkCount += (int) operation;
		}
		// kings directions (only used in test cases)
		int[] kingDirections = KING_MATRIX[kingSquare];
		long kingDirectionsBits = 0L;
		int enemyKing = enemys[5];
		for (int square : kingDirections) {
			kingDirectionsBits = kingDirectionsBits | (1L << square);
		}
		long operation = kingDirectionsBits & bits[enemyKing - 1];
		long operation3 = (operation >>> squaresMap(operation));
		isInCheck = isInCheck | operation3;
		checkCount += (int) operation3;
		// knight directions
		int enemyKnight = enemys[1];
		long enemyKnights = bits[enemyKnight - 1];
		int[] knightDirections = KNIGHT_MATRIX[kingSquare];
		long knightDirectionsBits;
		for (int square : knightDirections) {
			knightDirectionsBits = 1L << square;
			long[] choice = new long[] { 0L, knightDirectionsBits };
			long operation2 = ((knightDirectionsBits & enemyKnights) >>> square);
			isInCheck = isInCheck | operation2;
			inCheckMask = inCheckMask | choice[(int) operation2];
			checkCount += (int) operation2;
		}
		// bishops directions
		long enemyBishopsAndQuens = bits[enemys[2] - 1] | bits[enemys[4] - 1];

		for (int i = 0; i < 4; i++) {
			long visible = visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);
			long[] choice = new long[] { 0L, visible };
			long operation2 = ((enemyBishopsAndQuens & visible) >>> squaresMap(enemyBishopsAndQuens & visible));
			isInCheck = isInCheck | operation2;
			inCheckMask = inCheckMask | choice[(int) operation2];
			checkCount += (int) operation2;
		}
		// rooks directions
		long enemyRooksAndQuens = bits[enemys[3] - 1] | bits[enemys[4] - 1];
		for (int i = 4; i < 8; i++) {
			long visible = visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);
			long[] choice = new long[] { 0L, visible };
			long operation2 = ((enemyRooksAndQuens & visible) >>> squaresMap(enemyRooksAndQuens & visible));
			isInCheck = isInCheck | operation2;
			inCheckMask = inCheckMask | choice[(int) operation2];
			checkCount += (int) operation2;
		}
		return isInCheck;
	}

	private long isLongCastleBlackEnable(int kingSquare) {

		long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
		long piecesInterruption1 = ((1L << 58) & (enemys | friends)) >>> 58;
		long piecesInterruption2 = ((1L << 59) & (enemys | friends)) >>> 59;
		long piecesInterruption3 = ((1L << 57) & (enemys | friends)) >>> 57;
		long castleEnable = position.getLongCastleBlackNumeric();
		long[] testBits1 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 59);
			testBits1[index] = testBits1[index] & ~(1L << 60);
		}
		testBits1[Piece.BK.ordinal() - 1] = 1L << 59;
		long check1 = isInCheck(Piece.BK.ordinal(), testBits1, position.getWhiteMoveNumeric(),
				BLACK_PAWN_MATRIX_2[59]);
		long[] testBits2 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 58);
			testBits2[index] = testBits2[index] & ~(1L << 60);
		}
		testBits2[Piece.BK.ordinal() - 1] = 1L << 58;
		long check2 = isInCheck(Piece.BK.ordinal(), testBits2, position.getWhiteMoveNumeric(),
				BLACK_PAWN_MATRIX_2[58]);

		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & castleEnable
				& ~check1 & ~check2 & ~inCheck;
	}

	private long isLongCastleWhiteEnable(int kingSquare) {
		long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
		long piecesInterruption1 = ((1L << 2) & (enemys | friends)) >>> 2;
		long piecesInterruption2 = ((1L << 3) & (enemys | friends)) >>> 3;
		long piecesInterruption3 = ((1L << 1) & (enemys | friends)) >>> 1;
		long castleEnable = position.getLongCastleWhiteNumeric();
		long[] testBits1 = position.getBits().clone();
		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 3);
			testBits1[index] = testBits1[index] & ~(1L << 4);
		}
		testBits1[Piece.WK.ordinal() - 1] = 1L << 3;
		long check1 = isInCheck(Piece.WK.ordinal(), testBits1, position.getWhiteMoveNumeric(), WHITE_PAWN_MATRIX_2[3]);
		long[] testBits2 = position.getBits().clone();
		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 2);
			testBits2[index] = testBits2[index] & ~(1L << 4);
		}
		testBits2[Piece.WK.ordinal() - 1] = 1L << 2;
		long check2 = isInCheck(Piece.WK.ordinal(), testBits2, position.getWhiteMoveNumeric(), WHITE_PAWN_MATRIX_2[2]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & castleEnable
				& ~check1 & ~check2 & ~inCheck;
	}

	private long isShortCastleBlackEnable(int kingSquare) {
		long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
		long piecesInterruption1 = ((1L << 61) & (enemys | friends)) >>> 61;
		long piecesInterruption2 = ((1L << 62) & (enemys | friends)) >>> 62;
		long castleEnable = position.getShortCastleBlackNumeric();
		long[] testBits1 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 61);
			testBits1[index] = testBits1[index] & ~(1L << 60);
		}
		testBits1[Piece.BK.ordinal() - 1] = 1L << 61;
		long check1 = isInCheck(Piece.BK.ordinal(), testBits1, position.getWhiteMoveNumeric(),
				BLACK_PAWN_MATRIX_2[61]);
		long[] testBits2 = position.getBits().clone();
		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 62);
			testBits2[index] = testBits2[index] & ~(1L << 60);
		}
		testBits2[Piece.BK.ordinal() - 1] = 1L << 62;
		long check2 = isInCheck(Piece.BK.ordinal(), testBits2, position.getWhiteMoveNumeric(),
				BLACK_PAWN_MATRIX_2[62]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & castleEnable & ~check1 & ~check2 & ~inCheck;
	}

	private long isShortCastleWhiteEnable(int kingSquare) {

		long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
		long piecesInterruption1 = ((1L << 5) & (enemys | friends)) >>> 5;
		long piecesInterruption2 = ((1L << 6) & (enemys | friends)) >>> 6;
		long castleEnable = position.getShortCastleWhiteNumeric();
		long[] testBits1 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 5);
			testBits1[index] = testBits1[index] & ~(1L << 4);
		}
		testBits1[Piece.WK.ordinal() - 1] = 1L << 5;
		long check1 = isInCheck(Piece.WK.ordinal(), testBits1, position.getWhiteMoveNumeric(), WHITE_PAWN_MATRIX_2[5]);
		long[] testBits2 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 6);
			testBits2[index] = testBits2[index] & ~(1L << 4);
		}
		testBits2[Piece.WK.ordinal() - 1] = 1L << 6;
		long check2 = isInCheck(Piece.WK.ordinal(), testBits2, position.getWhiteMoveNumeric(), WHITE_PAWN_MATRIX_2[6]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & castleEnable & ~check1 & ~check2 & ~inCheck;
	}

	private List<BitPosition> kingMoves(int square, int pieceType) {
		int[] kingDirections = KING_MATRIX[square];
		long emptySquares = ~(enemys | friends);
		long emptyOrEnemy = emptySquares | enemys;
		long moves = 0L;
		for (int move : kingDirections) {
			moves = moves | (1L << move);
		}
		long pseudoLegalMoves = moves & emptyOrEnemy;
		long internalCheckMask = 1L;
		List<BitPosition> positions = generatePositions(pseudoLegalMoves, internalCheckMask, pieceType, square);
		for (BitPosition position : positions) {
			position.changeColorToMove();
			applyCastleRules(position);
			position.changeColorToMove();
		}
		long castleMoves = 0L;
		castleMoves = castleMoves | (isShortCastleWhiteEnable(square) << 6);
		castleMoves = castleMoves | (isLongCastleWhiteEnable(square) << 2);
		castleMoves = castleMoves | (isShortCastleBlackEnable(square) << 62);
		castleMoves = castleMoves | (isLongCastleBlackEnable(square) << 58);
		List<BitPosition> castlePositions = generateCastlePositions(castleMoves, pieceType, square);
		positions.addAll(castlePositions);
		return positions;
	}

	private List<BitPosition> knightMoves(long br, int square, int pieceType, int[] pawnsDirections) {
		int[] knightDirections = KNIGHT_MATRIX[square];
		long emptySquares = ~(enemys | friends);
		long emptyOrEnemy = emptySquares | enemys;
		long moves = 0L;
		for (int move : knightDirections) {
			moves = moves | (1L << move);
		}
		long[] pin = new long[] { -1L, 0L };
		long pinMask = pin[(int) ((br & checkMask) >>> squaresMap(br & checkMask))];
		long legalMoves = moves & emptyOrEnemy & pinMask & inCheckMask;

		return generatePositions(legalMoves, pieceType, square, pawnsDirections, new int[][] { knightDirections });
	}

	private List<BitPosition> longToCastlePos(List<Long> moves, int kingPiece, int originSquare) {
		List<BitPosition> pseudoList = new LinkedList<>();
		for (Long move : moves) {
			BitPosition newPosition = (BitPosition) position.makeClone();
			makeCastle(newPosition, move, kingPiece, originSquare);
			pseudoList.add(newPosition);
		}
		return pseudoList;
	}

	private List<BitPosition> longToCoronationPos(List<Long> list, int pieceType, int originSquare) {
		List<BitPosition> pseudoList = new LinkedList<>();
		int[] coronationPieces = new int[] { pieceType + 1, pieceType + 2, pieceType + 3, pieceType + 4 };
		for (Long move : list) {
			for (int coronationPiece : coronationPieces) {
				BitPosition newPosition = (BitPosition) position.makeClone();
				makeCoronation(newPosition, move, pieceType, coronationPiece, originSquare);
				pseudoList.add(newPosition);
			}
		}
		return pseudoList;
	}

	private List<BitPosition> longToEnPassantCapture(List<Long> list, int pieceType, int originSquare) {
		List<BitPosition> pseudoList = new LinkedList<>();
		int[] choice = new int[] { 8, -8 };
		for (Long move : list) {
			long capture = 1L << (squaresMap(move) + choice[(int) position.getWhiteMoveNumeric()]);
			BitPosition newPosition = (BitPosition) position.makeClone();
			makeEnPassantCapture(newPosition, capture, move, pieceType, originSquare);
			pseudoList.add(newPosition);
		}
		return pseudoList;
	}

	private List<BitPosition> longToPos(List<Long> list, int pieceType, int originSquare) {
		List<BitPosition> pseudoList = new LinkedList<>();
		for (Long move : list) {
			BitPosition newPosition = (BitPosition) position.makeClone();
			makeMove(newPosition, move, pieceType, originSquare);
			pseudoList.add(newPosition);
		}
		return pseudoList;
	}

	private List<BitPosition> longToPosEnPassant(List<Long> list, int pieceType, int originSquare) {
		List<BitPosition> pseudoList = new LinkedList<>();
		for (Long move : list) {
			BitPosition newPosition = (BitPosition) position.makeClone();
			makeMove(newPosition, move, pieceType, originSquare);
			int enPassant = squaresMap(move);
			newPosition.setEnPassant(enPassant);
			pseudoList.add(newPosition);
		}
		return pseudoList;
	}

	private void makeCastle(BitPosition position, long move, int pieceType, int originSquare) {
		long[] bits = position.getBits();

		for (int index : INDEXES) {
			bits[index] = bits[index] & (~move);
		}
		bits[pieceType - 1] = (bits[pieceType - 1] & (~(1L << originSquare))) | move;

		long rookMove = 0L;
		rookMove = rookMove | (((1L << 6) & (move)) >> 1);
		rookMove = rookMove | (((1L << 2) & (move)) << 1);
		rookMove = rookMove | (((1L << 62) & (move)) >> 1);
		rookMove = rookMove | (((1L << 58) & (move)) << 1);

		long rookOrigin = 0L;
		rookOrigin = rookOrigin | (((1L << 6) & (move)) << 1);
		rookOrigin = rookOrigin | (((1L << 2) & (move)) >> 2);
		rookOrigin = rookOrigin | (((1L << 62) & (move)) << 1);
		rookOrigin = rookOrigin | (((1L << 58) & (move)) >> 2);

		int rookType = pieceType - 2;
		for (long bit : bits) {
			bit = bit & (~rookMove);
		}
		bits[rookType - 1] = (bits[rookType - 1] & (~rookOrigin)) | rookMove;
		position.setBits(bits);

		position.changeColorToMove();
		applyCastleRules(position);
		position.setHalfMovesCounter(position.getHalfMovesCounter() + 1);
		position.increaseMovesCounter();
		position.setEnPassant(-1);
	}

	private void makeCoronation(BitPosition position, long move, int pieceType, int pieceToCrown, int originSquare) {
		long[] bits = position.getBits();
		for (int index : INDEXES) {
			bits[index] = bits[index] & (~move);
		}
		bits[pieceType - 1] = (bits[pieceType - 1] & (~(1L << originSquare)));
		bits[pieceToCrown - 1] = bits[pieceToCrown - 1] | move;
		position.setBits(bits);
		position.changeColorToMove();
		applyCastleRules(position);
		position.setHalfMovesCounter(0);
		position.increaseMovesCounter();
		position.setEnPassant(-1);
	}

	private void makeEnPassantCapture(BitPosition position, long capture, long move, int pieceType, int originSquare) {
		long[] bits = position.getBits();
		for (int index : INDEXES) {
			bits[index] = bits[index] & (~capture);
		}
		bits[pieceType - 1] = (bits[pieceType - 1] & (~(1L << originSquare))) | move;
		position.setBits(bits);
		position.changeColorToMove();
		position.setHalfMovesCounter(0);
		position.increaseMovesCounter();
		position.setEnPassant(-1);
	}

	private void makeMove(BitPosition position, long move, int pieceType, int originSquare) {
		long[] bits = position.getBits();
		for (int index : INDEXES) {
			bits[index] = bits[index] & (~move);
		}
		bits[pieceType - 1] = (bits[pieceType - 1] & (~(1L << originSquare))) | move;
		position.setBits(bits);
		position.changeColorToMove();
		applyCastleRules(position);
		applyHalfMoveRule(position, move);
		position.increaseMovesCounter();
		position.setEnPassant(-1);
	}

	private List<BitPosition> pawnMoves(long br, int square, int[] pawnsDirections, int pieceType, int[][] matrix1,
			int[][] matrix2, int kingSquare) {
		int[] captureArray = matrix2[square];
		long captureMoves = 0L;
		long captureCoronationMoves = 0L;
		for (int squareToCapture : captureArray) {
			captureCoronationMoves = captureCoronationMoves | ((1L & isCoronation(squareToCapture)) << squareToCapture);
			captureMoves = captureMoves | ((1L & ~isCoronation(squareToCapture)) << squareToCapture);
		}
		int normalizedEnPassant = transformEnPassant(position.getEnPassant(), nextWhiteMove);
		long possibleEnPassant = (1L << normalizedEnPassant) & captureMoves;
		captureMoves = captureMoves & enemys;
		captureCoronationMoves = captureCoronationMoves & enemys;
		int[] advanceMatrix = matrix1[square];
		long advanceMoves = 0L;
		long advanceCoronationMoves = 0L;
		long advanceEnPassantMoves = 0L;
		for (int squareToOccupy : advanceMatrix) {
			advanceCoronationMoves = advanceCoronationMoves | ((1L & isCoronation(squareToOccupy)) << squareToOccupy);
			advanceEnPassantMoves = advanceEnPassantMoves
					| ((1L & isEnPassant(square, squareToOccupy, position.getWhiteMoveNumeric())) << squareToOccupy);
			advanceMoves = advanceMoves | ((1L & ~isCoronation(squareToOccupy)
					& ~isEnPassant(square, squareToOccupy, position.getWhiteMoveNumeric())) << squareToOccupy);
		}
		long visible = visibleSquares(position, new int[] { 4, 5, 6, 7 }, square);
		advanceMoves = advanceMoves & ~(friends | enemys | ~visible);
		advanceCoronationMoves = advanceCoronationMoves & ~(friends | enemys | ~visible);
		advanceEnPassantMoves = advanceEnPassantMoves & ~(friends | enemys | ~visible);
		long pseudoCoronationMoves = advanceCoronationMoves | captureCoronationMoves;
		long pseudoLegalMoves = advanceMoves | captureMoves;
		long operation = (br & checkMask) >>> squaresMap(br);
		long defense = defenseDirection(kingSquare, square);
		long[] pin1 = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
		long pinMask1 = pin1[(int) operation];
		long[] pin2 = new long[] { -1L, pseudoCoronationMoves & checkMask & defense };
		long pinMask2 = pin2[(int) operation];
		long[] pin3 = new long[] { -1L, advanceEnPassantMoves & checkMask & defense};
		long pinMask3 = pin3[(int) operation];
		long legalMoves = pseudoLegalMoves & pinMask1 & inCheckMask;
		long legalCoronationMoves = pseudoCoronationMoves & pinMask2 & inCheckMask;
		long legalAdvanceEnPassantMoves = advanceEnPassantMoves & pinMask3 & inCheckMask;
		List<BitPosition> positions = generatePositions(legalMoves, pieceType, square, pawnsDirections);
		List<BitPosition> coronations = generateCoronations(legalCoronationMoves, pieceType, square, pawnsDirections);
		List<BitPosition> positionsEnPassant = generatePositionsWithEnPassant(legalAdvanceEnPassantMoves, pieceType,
				square, pawnsDirections);
		List<BitPosition> enPassantCaptures = generateEnPassantCaptures(possibleEnPassant, pieceType, square,
				pawnsDirections, captureArray);
		positions.addAll(coronations);
		positions.addAll(positionsEnPassant);
		positions.addAll(enPassantCaptures);
		return positions;
	}

	private List<BitPosition> queenMoves(long br, int square, int pieceType, int[] pawnsDirections, int kingSquare) {
		long defense = defenseDirection(kingSquare, square);
		long pseudoLegalMoves = visibleSquares(position, QUEEN_DIRECTIONS, square);
		long[] pin = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
		long pinMask = pin[(int) ((br & checkMask) >>> squaresMap(br & checkMask))];
		long legalMoves = pseudoLegalMoves & pinMask & inCheckMask;

		return generatePositions(legalMoves, pieceType, square, pawnsDirections);
	}

	private List<BitPosition> rookMoves(long br, int square, int pieceType, int[] pawnsDirections, int kingSquare) {
		long defense = defenseDirection(kingSquare, square);
		long pseudoLegalMoves = visibleSquares(position, ROOK_DIRECTIONS, square);
		long[] pin = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
		long pinMask = pin[(int) ((br & checkMask) >>> squaresMap(br))];
		long legalMoves = pseudoLegalMoves & pinMask & inCheckMask;

		List<BitPosition> positions = generatePositions(legalMoves, pieceType, square, pawnsDirections);
		for (BitPosition newPosition : positions) {
			newPosition.changeColorToMove();
			applyCastleRules(newPosition);
			newPosition.changeColorToMove();
		}
		return positions;
	}

	@Override
	public void setChildren(List<Position> childs) {
		List<BitPosition> aux = new ArrayList<>(childs.size());
		for (Position child : childs) {
			if (child instanceof BitPosition)
				aux.add((BitPosition) child);
			else {
				aux.add(new BitPosition(child.toFen()));
			}
		}
		this.children = aux;
	}

	@Override
	public void setPosition(Position position) {
		if (position instanceof BitPosition)
			this.position = (BitPosition) position;
		else {
			this.position = new BitPosition(position.toFen());
		}

	}

	@Override
	public List<Move> getLegalMoves() {
		if (children != null) {
			List<Move> legalMoves = new ArrayList<>(children.size());
			for (BitPosition child : children) {
				MoveDetector d = new MoveDetector(this, child);
				legalMoves.add(d.getUnsafeMove());
			}
			return legalMoves;
		} else {
			generateLegalMoves();
			return getLegalMoves();
		}

	}

	@Override
	public String toString() {
		return "FastMoveGenerator []";
	}

}