package chessapi4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class CheckInfo {
	private long inCheck;
	private long inCheckMask;
	private int checkCount;
	public CheckInfo(long inCheck, long inCheckMask, int checkCount) {
		super();
		this.inCheck = inCheck;
		this.inCheckMask = inCheckMask;
		this.checkCount = checkCount;
	}
	public long getInCheck() {
		return inCheck;
	}
	public void setInCheck(long inCheck) {
		this.inCheck = inCheck;
	}
	public long getInCheckMask() {
		return inCheckMask;
	}
	public void setInCheckMask(long inCheckMask) {
		this.inCheckMask = inCheckMask;
	}
	public int getCheckCount() {
		return checkCount;
	}
	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}

}
/**
 * This class is intended to generate all possible moves
 * from an original position.
 *
 * @author lunalobos
 *
 */
public final class Generator {
	protected static final int[] BISHOP_DIRECTIONS = new int[] { 0, 1, 2, 3 };
	private static final int[] BISHOPS = new int[] { Piece.BB.ordinal(), Piece.WB.ordinal() };
	private static final int[][] BLACK_PAWN_MATRIX_1 = new int[][] { {}, {}, {}, {}, {}, {}, {}, {}, { 0 }, { 1 },
			{ 2 }, { 3 }, { 4 }, { 5 }, { 6 }, { 7 }, { 8 }, { 9 }, { 10 }, { 11 }, { 12 }, { 13 }, { 14 }, { 15 },
			{ 16 }, { 17 }, { 18 }, { 19 }, { 20 }, { 21 }, { 22 }, { 23 }, { 24 }, { 25 }, { 26 }, { 27 }, { 28 },
			{ 29 }, { 30 }, { 31 }, { 32 }, { 33 }, { 34 }, { 35 }, { 36 }, { 37 }, { 38 }, { 39 }, { 40, 32 },
			{ 41, 33 }, { 42, 34 }, { 43, 35 }, { 44, 36 }, { 45, 37 }, { 46, 38 }, { 47, 39 }, { 48 }, { 49 }, { 50 },
			{ 51 }, { 52 }, { 53 }, { 54 }, { 55 } };
	protected static final int[][] BLACK_PAWN_MATRIX_2 = new int[][] { {}, {}, {}, {}, {}, {}, {}, {}, { 1 }, { 0, 2 },
			{ 1, 3 }, { 2, 4 }, { 3, 5 }, { 4, 6 }, { 5, 7 }, { 6 }, { 9 }, { 8, 10 }, { 9, 11 }, { 10, 12 },
			{ 11, 13 }, { 12, 14 }, { 13, 15 }, { 14 }, { 17 }, { 16, 18 }, { 17, 19 }, { 18, 20 }, { 19, 21 },
			{ 20, 22 }, { 21, 23 }, { 22 }, { 25 }, { 24, 26 }, { 25, 27 }, { 26, 28 }, { 27, 29 }, { 28, 30 },
			{ 29, 31 }, { 30 }, { 33 }, { 32, 34 }, { 33, 35 }, { 34, 36 }, { 35, 37 }, { 36, 38 }, { 37, 39 }, { 38 },
			{ 41 }, { 40, 42 }, { 41, 43 }, { 42, 44 }, { 43, 45 }, { 44, 46 }, { 45, 47 }, { 46 }, { 49 }, { 48, 50 },
			{ 49, 51 }, { 50, 52 }, { 51, 53 }, { 52, 54 }, { 53, 55 }, { 54 } };

	private static final long[] SCB_MASK = new long[] { 1L << 60, 1L << 63 };
	private static final int[] SCB_SQUARES = new int[] { 60, 63 };
	private static final long[] SCW_MASK = new long[] { 1L << 4, 1 << 7 };
	private static final int[] SCW_SQUARES = new int[] { 4, 7 };
	private static final long[] LCB_MASK = new long[] { 1L << 60, 1L << 56 };
	private static final int[] LCB_SQUARES = new int[] { 60, 56 };
	private static final long[] LCW_MASK = new long[] { 1L << 4, 1 << 0 };
	private static final int[] LCW_SQUARES = new int[] { 4, 0 };

	private static final long[][][] CASTLE_MASK = new long[][][] { { SCB_MASK, LCB_MASK }, { SCW_MASK, LCW_MASK } };

	private static final int[][][] CASTLE_SQUARES = new int[][][] { { SCB_SQUARES, LCB_SQUARES },
			{ SCW_SQUARES, LCW_SQUARES } };

	private static final int[] INDEXES = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

	private static final int[][] KING_MATRIX = new int[][] { { 9, 8, 1 }, { 10, 8, 9, 2, 0 }, { 11, 9, 10, 3, 1 },
			{ 12, 10, 11, 4, 2 }, { 13, 11, 12, 5, 3 }, { 14, 12, 13, 6, 4 }, { 15, 13, 14, 7, 5 }, { 14, 15, 6 },
			{ 17, 1, 16, 0, 9 }, { 18, 16, 0, 2, 17, 1, 10, 8 }, { 19, 17, 1, 3, 18, 2, 11, 9 },
			{ 20, 18, 2, 4, 19, 3, 12, 10 }, { 21, 19, 3, 5, 20, 4, 13, 11 }, { 22, 20, 4, 6, 21, 5, 14, 12 },
			{ 23, 21, 5, 7, 22, 6, 15, 13 }, { 22, 6, 23, 7, 14 }, { 25, 9, 24, 8, 17 },
			{ 26, 24, 8, 10, 25, 9, 18, 16 }, { 27, 25, 9, 11, 26, 10, 19, 17 }, { 28, 26, 10, 12, 27, 11, 20, 18 },
			{ 29, 27, 11, 13, 28, 12, 21, 19 }, { 30, 28, 12, 14, 29, 13, 22, 20 }, { 31, 29, 13, 15, 30, 14, 23, 21 },
			{ 30, 14, 31, 15, 22 }, { 33, 17, 32, 16, 25 }, { 34, 32, 16, 18, 33, 17, 26, 24 },
			{ 35, 33, 17, 19, 34, 18, 27, 25 }, { 36, 34, 18, 20, 35, 19, 28, 26 }, { 37, 35, 19, 21, 36, 20, 29, 27 },
			{ 38, 36, 20, 22, 37, 21, 30, 28 }, { 39, 37, 21, 23, 38, 22, 31, 29 }, { 38, 22, 39, 23, 30 },
			{ 41, 25, 40, 24, 33 }, { 42, 40, 24, 26, 41, 25, 34, 32 }, { 43, 41, 25, 27, 42, 26, 35, 33 },
			{ 44, 42, 26, 28, 43, 27, 36, 34 }, { 45, 43, 27, 29, 44, 28, 37, 35 }, { 46, 44, 28, 30, 45, 29, 38, 36 },
			{ 47, 45, 29, 31, 46, 30, 39, 37 }, { 46, 30, 47, 31, 38 }, { 49, 33, 48, 32, 41 },
			{ 50, 48, 32, 34, 49, 33, 42, 40 }, { 51, 49, 33, 35, 50, 34, 43, 41 }, { 52, 50, 34, 36, 51, 35, 44, 42 },
			{ 53, 51, 35, 37, 52, 36, 45, 43 }, { 54, 52, 36, 38, 53, 37, 46, 44 }, { 55, 53, 37, 39, 54, 38, 47, 45 },
			{ 54, 38, 55, 39, 46 }, { 57, 41, 56, 40, 49 }, { 58, 56, 40, 42, 57, 41, 50, 48 },
			{ 59, 57, 41, 43, 58, 42, 51, 49 }, { 60, 58, 42, 44, 59, 43, 52, 50 }, { 61, 59, 43, 45, 60, 44, 53, 51 },
			{ 62, 60, 44, 46, 61, 45, 54, 52 }, { 63, 61, 45, 47, 62, 46, 55, 53 }, { 62, 46, 63, 47, 54 },
			{ 49, 48, 57 }, { 48, 50, 49, 58, 56 }, { 49, 51, 50, 59, 57 }, { 50, 52, 51, 60, 58 },
			{ 51, 53, 52, 61, 59 }, { 52, 54, 53, 62, 60 }, { 53, 55, 54, 63, 61 }, { 54, 55, 62 } };

	private static final int[] KING_PIECES = new int[] { Piece.BK.ordinal(), Piece.WK.ordinal() };

	private static final int[] KINGS = new int[] { Piece.BK.ordinal(), Piece.WK.ordinal() };

	protected static final int[][] KNIGHT_MATRIX = new int[][] { { 17, 10 }, { 18, 16, 11 }, { 19, 17, 12, 8 },
			{ 20, 18, 13, 9 }, { 21, 19, 14, 10 }, { 22, 20, 15, 11 }, { 23, 21, 12 }, { 22, 13 }, { 25, 18, 2 },
			{ 26, 24, 19, 3 }, { 27, 25, 20, 16, 4, 0 }, { 28, 26, 21, 17, 5, 1 }, { 29, 27, 22, 18, 6, 2 },
			{ 30, 28, 23, 19, 7, 3 }, { 31, 29, 20, 4 }, { 30, 21, 5 }, { 33, 26, 1, 10 }, { 34, 32, 27, 2, 0, 11 },
			{ 35, 33, 28, 24, 3, 1, 12, 8 }, { 36, 34, 29, 25, 4, 2, 13, 9 }, { 37, 35, 30, 26, 5, 3, 14, 10 },
			{ 38, 36, 31, 27, 6, 4, 15, 11 }, { 39, 37, 28, 7, 5, 12 }, { 38, 29, 6, 13 }, { 41, 34, 9, 18 },
			{ 42, 40, 35, 10, 8, 19 }, { 43, 41, 36, 32, 11, 9, 20, 16 }, { 44, 42, 37, 33, 12, 10, 21, 17 },
			{ 45, 43, 38, 34, 13, 11, 22, 18 }, { 46, 44, 39, 35, 14, 12, 23, 19 }, { 47, 45, 36, 15, 13, 20 },
			{ 46, 37, 14, 21 }, { 49, 42, 17, 26 }, { 50, 48, 43, 18, 16, 27 }, { 51, 49, 44, 40, 19, 17, 28, 24 },
			{ 52, 50, 45, 41, 20, 18, 29, 25 }, { 53, 51, 46, 42, 21, 19, 30, 26 }, { 54, 52, 47, 43, 22, 20, 31, 27 },
			{ 55, 53, 44, 23, 21, 28 }, { 54, 45, 22, 29 }, { 57, 50, 25, 34 }, { 58, 56, 51, 26, 24, 35 },
			{ 59, 57, 52, 48, 27, 25, 36, 32 }, { 60, 58, 53, 49, 28, 26, 37, 33 }, { 61, 59, 54, 50, 29, 27, 38, 34 },
			{ 62, 60, 55, 51, 30, 28, 39, 35 }, { 63, 61, 52, 31, 29, 36 }, { 62, 53, 30, 37 }, { 58, 33, 42 },
			{ 59, 34, 32, 43 }, { 60, 56, 35, 33, 44, 40 }, { 61, 57, 36, 34, 45, 41 }, { 62, 58, 37, 35, 46, 42 },
			{ 63, 59, 38, 36, 47, 43 }, { 60, 39, 37, 44 }, { 61, 38, 45 }, { 41, 50 }, { 42, 40, 51 },
			{ 43, 41, 52, 48 }, { 44, 42, 53, 49 }, { 45, 43, 54, 50 }, { 46, 44, 55, 51 }, { 47, 45, 52 },
			{ 46, 53 } };
	private static final int[] KNIGHTS = new int[] { Piece.BN.ordinal(), Piece.WN.ordinal() };

	private static final int[][] WHITE_PAWN_MATRIX_1 = new int[][] { { 8 }, { 9 }, { 10 }, { 11 }, { 12 }, { 13 },
			{ 14 }, { 15 }, { 16, 24 }, { 17, 25 }, { 18, 26 }, { 19, 27 }, { 20, 28 }, { 21, 29 }, { 22, 30 },
			{ 23, 31 }, { 24 }, { 25 }, { 26 }, { 27 }, { 28 }, { 29 }, { 30 }, { 31 }, { 32 }, { 33 }, { 34 }, { 35 },
			{ 36 }, { 37 }, { 38 }, { 39 }, { 40 }, { 41 }, { 42 }, { 43 }, { 44 }, { 45 }, { 46 }, { 47 }, { 48 },
			{ 49 }, { 50 }, { 51 }, { 52 }, { 53 }, { 54 }, { 55 }, { 56 }, { 57 }, { 58 }, { 59 }, { 60 }, { 61 },
			{ 62 }, { 63 }, {}, {}, {}, {}, {}, {}, {}, {} };
	protected static final int[][] WHITE_PAWN_MATRIX_2 = new int[][] { { 9 }, { 8, 10 }, { 9, 11 }, { 10, 12 },
			{ 11, 13 }, { 12, 14 }, { 13, 15 }, { 14 }, { 17 }, { 16, 18 }, { 17, 19 }, { 18, 20 }, { 19, 21 },
			{ 20, 22 }, { 21, 23 }, { 22 }, { 25 }, { 24, 26 }, { 25, 27 }, { 26, 28 }, { 27, 29 }, { 28, 30 },
			{ 29, 31 }, { 30 }, { 33 }, { 32, 34 }, { 33, 35 }, { 34, 36 }, { 35, 37 }, { 36, 38 }, { 37, 39 }, { 38 },
			{ 41 }, { 40, 42 }, { 41, 43 }, { 42, 44 }, { 43, 45 }, { 44, 46 }, { 45, 47 }, { 46 }, { 49 }, { 48, 50 },
			{ 49, 51 }, { 50, 52 }, { 51, 53 }, { 52, 54 }, { 53, 55 }, { 54 }, { 57 }, { 56, 58 }, { 57, 59 },
			{ 58, 60 }, { 59, 61 }, { 60, 62 }, { 61, 63 }, { 62 }, {}, {}, {}, {}, {}, {}, {}, {} };
	private static final int[][][] PAWN_MATRIX1 = new int[][][] { BLACK_PAWN_MATRIX_1, WHITE_PAWN_MATRIX_1 };
	private static final int[][][] PAWN_MATRIX2 = new int[][][] { BLACK_PAWN_MATRIX_2, WHITE_PAWN_MATRIX_2 };
	private static final int[] PAWNS = new int[] { Piece.BP.ordinal(), Piece.WP.ordinal() };
	protected static final int[] QUEEN_DIRECTIONS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
	protected static final int[][][] QUEEN_MEGAMATRIX = Util.QUEEN_MEGAMATRIX;
	private static final int[] QUEENS = new int[] { Piece.BQ.ordinal(), Piece.WQ.ordinal() };
	protected static final int[] ROOK_DIRECTIONS = new int[] { 4, 5, 6, 7 };
	private static final int[] ROOK_PIECES = new int[] { Piece.BR.ordinal(), Piece.WR.ordinal() };
	private static final int[] ROOKS = new int[] { Piece.BR.ordinal(), Piece.WR.ordinal() };
//	private static final long[] OPTIONS = new long[] { 0L, 1L, 0b11L, 0b111L, 0b1111L, 0b11111L, 0b111111L,
//			0b1111111L };
	private static final int[] EP_CHOICE = new int[] { 8, -8 };
//	private static final long[] CORONATION_REF = new long[] { 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 0L, 0L, 0L, 0L, 0L, 0L,
//			0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
//			0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L };
	private static final VisibleMetrics VISIBLE_METRICS = new VisibleMetrics();

	protected Generator() {

	}

	private void applyCastleRules(Position position) {
		final int whiteMove = (int) position.wm();

		final long[] scMask = CASTLE_MASK[whiteMove][0];
		final long[] lcMask = CASTLE_MASK[whiteMove][1];
		final int[] scSquares = CASTLE_SQUARES[whiteMove][0];
		final int[] lcSquares = CASTLE_SQUARES[whiteMove][1];

		final long rookBits = position.getBits()[ROOK_PIECES[whiteMove] - 1];
		final long kingBits = position.getBits()[KING_PIECES[whiteMove] - 1];

		final long scBitsMasked = ((rookBits & scMask[1]) >>> scSquares[1]) & ((kingBits & scMask[0]) >>> scSquares[0]);
		final long lcBitsMasked = ((rookBits & lcMask[1]) >>> lcSquares[1]) & ((kingBits & lcMask[0]) >>> lcSquares[0]);

		position.setWK(new long[] { position.wk(), scBitsMasked & position.wk() }[whiteMove]);
		position.setWQ(new long[] { position.wq(), lcBitsMasked & position.wq() }[whiteMove]);
		position.setBK(new long[] { position.bk() & scBitsMasked, position.bk() }[whiteMove]);
		position.setBQ(new long[] { position.bq() & lcBitsMasked, position.bq() }[whiteMove]);
	}

	private void applyHalfMoveRule(Position p, long move, long enemys, Position position) {
		final int aux = (int) (6L & (position.wm() << 1 | position.wm() << 2));

		final long enemyOperation = enemys & move;

		final long pawnOperation = move & p.getBits()[Piece.BP.ordinal() - aux - 1];

		p.setHalfMovesCounter(
				new int[] { p.getHalfMovesCounter() + 1, 0 }[(int) ((pawnOperation >>> squaresMap(pawnOperation))
						| (enemyOperation >>> squaresMap(enemyOperation)))]);
	}

	private void bishopMoves(long br, int square, int pieceType, int kingSquare, long enemies, long friends,
			Position position, long checkMask, long inCheckMask, List<Position> children) {

		final long pseudoLegalMoves = visibleSquaresFast(BISHOP_DIRECTIONS, square, friends, enemies);
		final long[] pin = new long[] { -1L, pseudoLegalMoves & checkMask & defenseDirection(kingSquare, square) };
		final long isPin = pin[(int) ((br & checkMask) >>> squaresMap(br & checkMask))];
		long legalMoves = pseudoLegalMoves & isPin & inCheckMask;

		while (legalMoves != 0L) {
			final long move = legalMoves & -legalMoves;
			final Position newPosition = position.makeClone();
			makeMove(newPosition, move, pieceType, square, enemies, position);
			children.add(newPosition);
			legalMoves = legalMoves & ~move;
		}

	}

	private long createCheckMask(int kingSquare, long enemys, long friends, Position position, long nextWhiteMove,
			long[] bits) {
		final long empty = ~(enemys | friends);
		final int[] enemyRookChoice = new int[] { Piece.WR.ordinal(), Piece.BR.ordinal() };
		final int[] enemyQueenChoice = new int[] { Piece.WQ.ordinal(), Piece.BQ.ordinal() };
		final int[] enemyBishopChoice = new int[] { Piece.WB.ordinal(), Piece.BB.ordinal() };
		final int enemyRook = enemyRookChoice[(int) position.wm()];
		final int enemyQueen = enemyQueenChoice[(int) position.wm()];
		final int enemyBishop = enemyBishopChoice[(int) position.wm()];
		long checkMask = 0L;
		for (int j = 0; j < 4; j++) {
			final long visibleEmptyOrFriendsRD = visibleSquares(position.getBits(), new int[] { ROOK_DIRECTIONS[j] },
					kingSquare, nextWhiteMove);
			final long friendsRD = visibleEmptyOrFriendsRD & ~empty;
			final long[] testBitsRD = bits.clone();
			for (int i = 0; i < 12; i++) {
				testBitsRD[i] = testBitsRD[i] & ~friendsRD;
			}
			final long visibleEmptyOrEnemyRD = visibleSquares(testBitsRD, new int[] { ROOK_DIRECTIONS[j] }, kingSquare,
					position.wm());
			final long enemysThreadsRD = visibleEmptyOrEnemyRD & (bits[enemyRook - 1] | bits[enemyQueen - 1]);
			final long[] choice = new long[] { 0L, friendsRD | visibleEmptyOrEnemyRD };
			checkMask = checkMask | choice[(int) (enemysThreadsRD >>> squaresMap(enemysThreadsRD))];
		}
		for (int j = 0; j < 4; j++) {
			final long visibleEmptyOrFriendsBD = visibleSquares(position.getBits(), new int[] { BISHOP_DIRECTIONS[j] },
					kingSquare, nextWhiteMove);
			final long friendsBD = visibleEmptyOrFriendsBD & ~empty;
			final long[] testBitsBD = bits.clone();
			for (int i = 0; i < 12; i++) {
				testBitsBD[i] = testBitsBD[i] & ~friendsBD;
			}
			final long visibleEmptyOrEnemyBD = visibleSquares(testBitsBD, new int[] { BISHOP_DIRECTIONS[j] },
					kingSquare, position.wm());
			final long enemysThreadsBD = visibleEmptyOrEnemyBD & (bits[enemyBishop - 1] | bits[enemyQueen - 1]);
			final long[] choice = new long[] { 0L, friendsBD | visibleEmptyOrEnemyBD };
			checkMask = checkMask | choice[(int) (enemysThreadsBD >>> squaresMap(enemysThreadsBD))];
		}

		return checkMask;
	}

	private static long defenseDirection(int kingSquare, int pieceSquare) {
		final int[][] matrix = QUEEN_MEGAMATRIX[kingSquare];
		long result = 0L;
		int d = 0;

		for (int i = 1; i < 9; i++) {
			for (int square : matrix[i - 1]) {
				long operation1 = (1L << pieceSquare) & (1L << square);

				d = d | (new int[] { 0, -1 }[(int) (operation1 >>> squaresMap(operation1))] & i);
			}
		}
		final int[][] matrix2 = new int[][] { {}, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5],
				matrix[6], matrix[7] };
		for (int square : matrix2[d]) {
			result = result | (1L << square);
		}
		return result;
	}

	private void fillChildrenList(List<Position> children, long[] bits, long friends, long enemies, Position position,
			long checkMask, long inCheckMask, long nextWhiteMove, long inCheck, int pawnPiece, int kingSquare,
			int knightPiece, int bishopPiece, int rookPiece, int queenPiece, int kingPiece, int[] pawnsDirections,
			int[][] matrix1, int[][] matrix2) {
		long lb;
		long j;

		// Pawn Moves
		j = bits[pawnPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			pawnMoves(lb, squaresMap(lb), pawnsDirections, pawnPiece, matrix1, matrix2, kingSquare, enemies, friends,
					position, checkMask, inCheckMask, nextWhiteMove, children);
			j = j & ~lb;
		}
		// Knight Moves
		j = bits[knightPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			knightMoves(lb, squaresMap(lb), knightPiece, enemies, friends, position, checkMask, inCheckMask, children);
			j = j & ~lb;
		}
		// Bishop Moves
		j = bits[bishopPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			bishopMoves(lb, squaresMap(lb), bishopPiece, kingSquare, enemies, friends, position, checkMask, inCheckMask,
					children);
			j = j & ~lb;
		}
		// Rook Moves
		j = bits[rookPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			rookMoves(lb, squaresMap(lb), rookPiece, pawnsDirections, kingSquare, enemies, friends, position, checkMask,
					inCheckMask, children);
			j = j & ~lb;
		}
		// Queen Moves
		j = bits[queenPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			queenMoves(lb, squaresMap(lb), queenPiece, kingSquare, friends, enemies, position, checkMask, inCheckMask,
					children);
			j = j & ~lb;
		}
		// King Moves
		j = bits[kingPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			kingMoves(squaresMap(lb), kingPiece, enemies, friends, position, inCheck, children);
			j = j & ~lb;
		}
	}

	private void generateCastlePositions(long moves, int kingPiece, int square, Position position,
			List<Position> children) {
		while (moves != 0L) {
			final long move = moves & -moves;
			final Position newPosition = position.makeClone();
			makeCastle(newPosition, move, kingPiece, square);
			children.add(newPosition);
			moves = moves & ~move;
		}
	}


	/**
	 * Generates the list of legal positions that arise from this particular position.
	 * @param position
	 * @return the list of legal positions that arise from this particular position
	 */
	public final List<Position> generateChildren(Position position) {
		List<Position> children = new LinkedList<>();
		final long[] bits = position.getBits();
		final int aux = (int) (6L & (position.wm() << 1 | position.wm() << 2));
		final long friends = bits[Piece.BP.ordinal() - aux - 1] | bits[Piece.BN.ordinal() - aux - 1]
				| bits[Piece.BB.ordinal() - aux - 1] | bits[Piece.BR.ordinal() - aux - 1]
				| bits[Piece.BQ.ordinal() - aux - 1] | bits[Piece.BK.ordinal() - aux - 1];
		final long enemies = bits[Piece.WP.ordinal() + aux - 1] | bits[Piece.WN.ordinal() + aux - 1]
				| bits[Piece.WB.ordinal() + aux - 1] | bits[Piece.WR.ordinal() + aux - 1]
				| bits[Piece.WQ.ordinal() + aux - 1] | bits[Piece.WK.ordinal() + aux - 1];

		final long nextWhiteMove = (~position.wm()) & 1L;
		final int whiteMove = (int) position.wm();

		final int kingPiece = KINGS[whiteMove];

		final int kingSquare = squaresMap(bits[kingPiece - 1]);

		final int[] pawnsDirections = new int[][] { BLACK_PAWN_MATRIX_2[kingSquare],
				WHITE_PAWN_MATRIX_2[kingSquare] }[(int) position.wm()];
		final CheckInfo info = isInCheckWhithMask(kingPiece, position.getBits(), position.wm(), pawnsDirections);

		long inCheckMask = info.getInCheckMask();

		final long checkMask = createCheckMask(kingSquare, enemies, friends, position, nextWhiteMove, bits);
		final long[] choice = new long[] { -1L, inCheckMask, 0L, 0L, 0L, 0L };
		inCheckMask = choice[info.getCheckCount()];

		fillChildrenList(children, bits, friends, enemies, position, checkMask, inCheckMask, nextWhiteMove,
				info.getInCheck(), PAWNS[whiteMove], kingSquare, KNIGHTS[whiteMove], BISHOPS[whiteMove],
				ROOKS[whiteMove], QUEENS[whiteMove], kingPiece, pawnsDirections, PAWN_MATRIX1[whiteMove],
				PAWN_MATRIX2[whiteMove]);
		return children;
	}

	private void generateCoronations(long moves, int pieceType, int square, int[] pawnsDirections, Position position,
			List<Position> children) {
		final int[] coronationPieces = new int[] { pieceType + 1, pieceType + 2, pieceType + 3, pieceType + 4 };
		while (moves != 0L) {
			final long move = moves & -moves;
			for (int coronationPiece : coronationPieces) {
				final Position newPosition = position.makeClone();
				makeCoronation(newPosition, move, pieceType, coronationPiece, square);
				children.add(newPosition);
			}
			moves = moves & ~move;
		}
	}

	private void generateEnPassantCaptures(long moves, int pieceType, int originSquare, int[] pawnsDirections,
			int[] captureArray, Position position, List<Position> children) {
		while (moves != 0L) {
			final long move = moves & -moves;
			final long capture = 1L << (squaresMap(move) + EP_CHOICE[(int) position.wm()]);
			Position newPosition = position.makeClone();
			makeEnPassantCapture(newPosition, capture, move, pieceType, originSquare);
			Position testPosition = newPosition.makeClone();
			testPosition.setWM(position.wm());
			if (isInCheckD(testPosition, pawnsDirections) != 1L)
				children.add(newPosition);
			moves = moves & ~move;
		}
	}

	private void generateKingPositions(long moves, int pieceType, int originSquare, long enemys, Position position,
			List<Position> children) {
		while (moves != 0L) {
			final long move = moves & -moves;
			final Position newPosition = position.makeClone();
			makeMove(newPosition, move, pieceType, originSquare, enemys, position);
			final Position testPosition = newPosition.makeClone();
			testPosition.setWM(position.wm());
			if (isInCheck(testPosition) != 1L) {
				newPosition.changeColorToMove();
				applyCastleRules(newPosition);
				newPosition.changeColorToMove();
				children.add(newPosition);
			}
			moves = moves & ~move;
		}
	}

	/**
	 * Generates a list with the Move objects for the given children in the same order.
	 * @param parent
	 * @param children
	 * @return a list with the Move objects for the given children in the same order
	 */
	public final List<Move> generateMoves(Position parent, List<Position> children) {
		List<Move> legalMoves = new ArrayList<>(children.size());
		for (Position child : children) {
			final MoveDetector d = new MoveDetector(parent, child);
			legalMoves.add(d.getUnsafeMove());
		}
		return legalMoves;
	}

	private void generatePositions(long moves, int pieceType, int square, int[] pawnsDirections, long enemys,
			Position position, List<Position> children) {
		while (moves != 0L) {
			final long move = moves & -moves;
			final Position newPosition = position.makeClone();
			makeMove(newPosition, move, pieceType, square, enemys, position);
			children.add(newPosition);
			moves = moves & ~move;
		}
	}

	private void generatePositionsWithEnPassant(long moves, int pieceType, int square, int[] pawnsDirections,
			long enemies, Position position, List<Position> children) {
		while (moves != 0L) {
			final long move = moves & -moves;
			final Position newPosition = position.makeClone();
			makeMove(newPosition, move, pieceType, square, enemies, position);
			newPosition.setEnPassant(squaresMap(move));
			children.add(newPosition);
			moves = moves & ~move;
		}
	}

	protected final long inCheck(int kingPiece, long[] bits, long whiteMoveNumeric, int[] pawnsDirections) {

		final int kingSquare = squaresMap(bits[kingPiece - 1]);
		long isInCheck = 0L;
		final int aux = (int) (6L & (whiteMoveNumeric << 1 | whiteMoveNumeric << 2));
		final int[] enemies = { Piece.WP.ordinal() + aux, Piece.WN.ordinal() + aux, Piece.WB.ordinal() + aux,
				Piece.WR.ordinal() + aux, Piece.WQ.ordinal() + aux, Piece.WK.ordinal() + aux };
		// pawns directions
		final int enemyPawn = enemies[0];
		final long enemyPawns = bits[enemyPawn - 1];
		if (enemyPawns != 0L) {
			for (int pawnDirection : pawnsDirections) {
				long enemyPawnDangerLocation = 1L << pawnDirection;
				isInCheck = isInCheck | (((enemyPawns & enemyPawnDangerLocation) >>> pawnDirection));
			}
		}

		// kings directions
		final int[] kingDirections = KING_MATRIX[kingSquare];
		long kingDirectionsBits = 0L;
		final int enemyKing = enemies[5];
		for (int square : kingDirections) {
			kingDirectionsBits = kingDirectionsBits | (1L << square);
		}
		final long operation = kingDirectionsBits & bits[enemyKing - 1];
		isInCheck = isInCheck | (operation >>> squaresMap(operation));
		// knight directions
		final int enemyKnight = enemies[1];
		final long enemyKnights = bits[enemyKnight - 1];
		if (enemyKnights != 0L) {
			final int[] knightDirections = KNIGHT_MATRIX[kingSquare];
			long knightDirectionsBits;
			for (int square : knightDirections) {
				knightDirectionsBits = 1L << square;
				isInCheck = isInCheck | ((knightDirectionsBits & enemyKnights) >>> square);
			}
		}

		// bishops directions
		final long enemyBishopsAndQuens = bits[enemies[2] - 1] | bits[enemies[4] - 1];

		if (enemyBishopsAndQuens != 0L) {
			for (int i = 0; i < 4; i++) {
				final long visible = visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);
				isInCheck = isInCheck
						| ((enemyBishopsAndQuens & visible) >>> squaresMap(enemyBishopsAndQuens & visible));
			}
		}
		// rooks directions
		final long enemyRooksAndQuens = bits[enemies[3] - 1] | bits[enemies[4] - 1];
		if (enemyRooksAndQuens != 0L) {
			for (int i = 4; i < 8; i++) {
				final long visible = visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);
				isInCheck = isInCheck | ((enemyRooksAndQuens & visible) >>> squaresMap(enemyRooksAndQuens & visible));
			}
		}
		return isInCheck;
	}

	protected static final long isPromotion(int finalSquare) {
//		return CORONATION_REF[finalSquare];
		return ((((finalSquare >>> 3) & 7L) >>> 2) & ((((finalSquare >>> 3) & 7L) >>> 1) & 1L)
				& (((finalSquare >>> 3) & 7L) & 1L))
				| (((((63 - finalSquare) >>> 3) & 7L) >>> 2) & (((((63 - finalSquare) >>> 3) & 7L) >>> 1) & 1L)
						& ((((63 - finalSquare) >>> 3) & 7L) & 1L));
	}

	private long isEnPassant(int originSquare, int finalSquare, long whiteMoveNumeric) {
		final long difference = finalSquare - originSquare;
		final long choice[] = new long[] { ~difference + 1, difference };
		final long maskedDifference = 16L & choice[(int) whiteMoveNumeric];
		return maskedDifference >>> 4;
	}

	protected long isInCheck(Position position) {
		final int kingPiece = -Piece.WK.ordinal() * (int) position.wm() + Piece.BK.ordinal();
		final int kingSquare = squaresMap(position.getBits()[kingPiece - 1]);
		final int[][] pawnsDirectionChoice = new int[][] { BLACK_PAWN_MATRIX_2[kingSquare],
				WHITE_PAWN_MATRIX_2[kingSquare] };
		final int[] pawnsDirections = pawnsDirectionChoice[(int) position.wm()];
		return inCheck(kingPiece, position.getBits(), position.wm(), pawnsDirections);
	}

	private long isInCheckD(Position position, int[] pawnsDirections) {
		final int kingPiece = -Piece.WK.ordinal() * (int) position.wm() + Piece.BK.ordinal();
		return inCheck(kingPiece, position.getBits(), position.wm(), pawnsDirections);
	}

	private CheckInfo isInCheckWhithMask(int kingPiece, long[] bits, long whiteMoveNumeric, int[] pawnsDirections) {
		long inCheckMask = 0L;
		int checkCount = 0;
		final int kingSquare = squaresMap(bits[kingPiece - 1]);
		long isInCheck = 0L;
		final int aux = (int) (6L & (whiteMoveNumeric << 1 | whiteMoveNumeric << 2));
		final int[] enemys = { Piece.WP.ordinal() + aux, Piece.WN.ordinal() + aux, Piece.WB.ordinal() + aux,
				Piece.WR.ordinal() + aux, Piece.WQ.ordinal() + aux, Piece.WK.ordinal() + aux };
		// pawns directions

		for (int pawnDirection : pawnsDirections) {

			final long enemyPawnDangerLocation = 1L << pawnDirection;
			final long operation = ((bits[enemys[0] - 1] & enemyPawnDangerLocation) >>> pawnDirection);
			isInCheck = isInCheck | operation;

			inCheckMask = inCheckMask | new long[] { 0L, enemyPawnDangerLocation }[(int) operation];
			checkCount += (int) operation;
		}
		// kings directions (only used in test cases)

		long kingDirectionsBits = 0L;

		for (int square : KING_MATRIX[kingSquare]) {
			kingDirectionsBits = kingDirectionsBits | (1L << square);
		}
		final long operation = kingDirectionsBits & bits[enemys[5] - 1];
		final long operation3 = (operation >>> squaresMap(operation));
		isInCheck = isInCheck | operation3;
		checkCount += (int) operation3;
		// knight directions

		long knightDirectionsBits;
		for (int square : KNIGHT_MATRIX[kingSquare]) {
			knightDirectionsBits = 1L << square;

			final long operation2 = ((knightDirectionsBits & bits[enemys[1] - 1]) >>> square);
			isInCheck = isInCheck | operation2;
			inCheckMask = inCheckMask | new long[] { 0L, knightDirectionsBits }[(int) operation2];
			checkCount += (int) operation2;
		}
		// bishops directions
		final long enemyBishopsAndQuens = bits[enemys[2] - 1] | bits[enemys[4] - 1];

		for (int i = 0; i < 4; i++) {
			final long visible = visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);

			final long operation2 = ((enemyBishopsAndQuens & visible) >>> squaresMap(enemyBishopsAndQuens & visible));
			isInCheck = isInCheck | operation2;
			inCheckMask = inCheckMask | new long[] { 0L, visible }[(int) operation2];
			checkCount += (int) operation2;
		}
		// rooks directions
		final long enemyRooksAndQuens = bits[enemys[3] - 1] | bits[enemys[4] - 1];
		for (int i = 4; i < 8; i++) {
			final long visible = visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);

			final long operation2 = ((enemyRooksAndQuens & visible) >>> squaresMap(enemyRooksAndQuens & visible));
			isInCheck = isInCheck | operation2;
			inCheckMask = inCheckMask | new long[] { 0L, visible }[(int) operation2];
			checkCount += (int) operation2;
		}
		return new CheckInfo(isInCheck, inCheckMask, checkCount);
	}

	private long isLongCastleBlackEnable(int kingSquare, long enemies, long friends, Position position, long inCheck) {

		final long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
		final long piecesInterruption1 = ((1L << 58) & (enemies | friends)) >>> 58;
		final long piecesInterruption2 = ((1L << 59) & (enemies | friends)) >>> 59;
		final long piecesInterruption3 = ((1L << 57) & (enemies | friends)) >>> 57;
		final long castleEnable = position.bq();
		final long[] testBits1 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 59);
			testBits1[index] = testBits1[index] & ~(1L << 60);
		}
		testBits1[Piece.BK.ordinal() - 1] = 1L << 59;
		final long check1 = inCheck(Piece.BK.ordinal(), testBits1, position.wm(), BLACK_PAWN_MATRIX_2[59]);
		final long[] testBits2 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 58);
			testBits2[index] = testBits2[index] & ~(1L << 60);
		}
		testBits2[Piece.BK.ordinal() - 1] = 1L << 58;
		final long check2 = inCheck(Piece.BK.ordinal(), testBits2, position.wm(), BLACK_PAWN_MATRIX_2[58]);

		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & castleEnable
				& ~check1 & ~check2 & ~inCheck;
	}

	private long isLongCastleWhiteEnable(int kingSquare, long enemies, long friends, Position position, long inCheck) {
		final long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
		final long piecesInterruption1 = ((1L << 2) & (enemies | friends)) >>> 2;
		final long piecesInterruption2 = ((1L << 3) & (enemies | friends)) >>> 3;
		final long piecesInterruption3 = ((1L << 1) & (enemies | friends)) >>> 1;
		final long castleEnable = position.wq();
		final long[] testBits1 = position.getBits().clone();
		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 3);
			testBits1[index] = testBits1[index] & ~(1L << 4);
		}
		testBits1[Piece.WK.ordinal() - 1] = 1L << 3;
		final long check1 = inCheck(Piece.WK.ordinal(), testBits1, position.wm(), WHITE_PAWN_MATRIX_2[3]);
		final long[] testBits2 = position.getBits().clone();
		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 2);
			testBits2[index] = testBits2[index] & ~(1L << 4);
		}
		testBits2[Piece.WK.ordinal() - 1] = 1L << 2;
		final long check2 = inCheck(Piece.WK.ordinal(), testBits2, position.wm(), WHITE_PAWN_MATRIX_2[2]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & castleEnable
				& ~check1 & ~check2 & ~inCheck;
	}

	private long isShortCastleBlackEnable(int kingSquare, long enemies, long friends, Position position, long inCheck) {
		final long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
		final long piecesInterruption1 = ((1L << 61) & (enemies | friends)) >>> 61;
		final long piecesInterruption2 = ((1L << 62) & (enemies | friends)) >>> 62;
		final long castleEnable = position.bk();
		final long[] testBits1 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 61);
			testBits1[index] = testBits1[index] & ~(1L << 60);
		}
		testBits1[Piece.BK.ordinal() - 1] = 1L << 61;
		final long check1 = inCheck(Piece.BK.ordinal(), testBits1, position.wm(), BLACK_PAWN_MATRIX_2[61]);
		final long[] testBits2 = position.getBits().clone();
		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 62);
			testBits2[index] = testBits2[index] & ~(1L << 60);
		}
		testBits2[Piece.BK.ordinal() - 1] = 1L << 62;
		final long check2 = inCheck(Piece.BK.ordinal(), testBits2, position.wm(), BLACK_PAWN_MATRIX_2[62]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & castleEnable & ~check1 & ~check2 & ~inCheck;
	}

	private long isShortCastleWhiteEnable(int kingSquare, long enemies, long friends, Position position, long inCheck) {

		final long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
		final long piecesInterruption1 = ((1L << 5) & (enemies | friends)) >>> 5;
		final long piecesInterruption2 = ((1L << 6) & (enemies | friends)) >>> 6;
		final long castleEnable = position.wk();
		final long[] testBits1 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 5);
			testBits1[index] = testBits1[index] & ~(1L << 4);
		}
		testBits1[Piece.WK.ordinal() - 1] = 1L << 5;
		final long check1 = inCheck(Piece.WK.ordinal(), testBits1, position.wm(), WHITE_PAWN_MATRIX_2[5]);
		final long[] testBits2 = position.getBits().clone();

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 6);
			testBits2[index] = testBits2[index] & ~(1L << 4);
		}
		testBits2[Piece.WK.ordinal() - 1] = 1L << 6;
		final long check2 = inCheck(Piece.WK.ordinal(), testBits2, position.wm(), WHITE_PAWN_MATRIX_2[6]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & castleEnable & ~check1 & ~check2 & ~inCheck;
	}

	private void kingMoves(int square, int pieceType, long enemies, long friends, Position pos, long inCheck,
			List<Position> children) {
		final int[] kingDirections = KING_MATRIX[square];
		final long emptyOrEnemy = ~friends;
		long moves = 0L;
		for (int move : kingDirections) {
			moves = moves | (1L << move);
		}
		generateKingPositions(moves & emptyOrEnemy, pieceType, square, enemies, pos, children);
		long castleMoves = 0L;
		castleMoves = castleMoves | (isShortCastleWhiteEnable(square, enemies, friends, pos, inCheck) << 6);
		castleMoves = castleMoves | (isLongCastleWhiteEnable(square, enemies, friends, pos, inCheck) << 2);
		castleMoves = castleMoves | (isShortCastleBlackEnable(square, enemies, friends, pos, inCheck) << 62);
		castleMoves = castleMoves | (isLongCastleBlackEnable(square, enemies, friends, pos, inCheck) << 58);
		generateCastlePositions(castleMoves, pieceType, square, pos, children);
	}

	private void knightMoves(long br, int square, int pieceType, long enemies, long friends, Position pos,
			long checkMask, long inCheckMask, List<Position> children) {
		final int[] knightDirections = KNIGHT_MATRIX[square];
		final long emptySquares = ~(enemies | friends);
		final long emptyOrEnemy = emptySquares | enemies;
		long moves = 0L;
		for (int move : knightDirections) {
			moves = moves | (1L << move);
		}
		final long[] pin = new long[] { -1L, 0L };
		final long pinMask = pin[(int) ((br & checkMask) >>> squaresMap(br & checkMask))];
		long legalMoves = moves & emptyOrEnemy & pinMask & inCheckMask;

		while (legalMoves != 0L) {
			final long move = legalMoves & -legalMoves;
			final Position newPosition = pos.makeClone();
			makeMove(newPosition, move, pieceType, square, enemies, pos);
			children.add(newPosition);
			legalMoves = legalMoves & ~move;
		}
	}

	private void makeCastle(Position position, long move, int pieceType, int originSquare) {
		final long[] bits = position.getBits();

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

	private void makeCoronation(Position position, long move, int pieceType, int pieceToCrown, int originSquare) {
		final long[] bits = position.getBits();
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

	private void makeEnPassantCapture(Position position, long capture, long move, int pieceType, int originSquare) {
		final long[] bits = position.getBits();
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

	private void makeMove(Position child, long move, int pieceType, int originSquare, long enemies, Position parent) {
		final long[] bits = child.getBits();
		for (int index : INDEXES) {
			bits[index] = bits[index] & (~move);
		}
		bits[pieceType - 1] = (bits[pieceType - 1] & (~(1L << originSquare))) | move;
		child.setBits(bits);
		child.changeColorToMove();
		applyCastleRules(child);
		applyHalfMoveRule(child, move, enemies, parent);
		child.increaseMovesCounter();
		child.setEnPassant(-1);
	}

	private void pawnMoves(long br, int square, int[] pawnsDirections, int pieceType, int[][] matrix1, int[][] matrix2,
			int kingSquare, long enemies, long friends, Position position, long checkMask, long inCheckMask,
			long nextWhiteMove, List<Position> children) {
		final int[] captureArray = matrix2[square];
		long captureMoves = 0L;
		long captureCoronationMoves = 0L;
		for (int squareToCapture : captureArray) {
			captureCoronationMoves = captureCoronationMoves | ((1L & isPromotion(squareToCapture)) << squareToCapture);
			captureMoves = captureMoves | ((1L & ~isPromotion(squareToCapture)) << squareToCapture);
		}
		final int normalizedEnPassant = transformEnPassant(position.getEnPassant(), nextWhiteMove);
		final long possibleEnPassant = (1L << normalizedEnPassant) & captureMoves;
		captureMoves = captureMoves & enemies;
		captureCoronationMoves = captureCoronationMoves & enemies;
		final int[] advanceMatrix = matrix1[square];
		long advanceMoves = 0L;
		long advanceCoronationMoves = 0L;
		long advanceEnPassantMoves = 0L;
		for (int squareToOccupy : advanceMatrix) {
			advanceCoronationMoves = advanceCoronationMoves | ((1L & isPromotion(squareToOccupy)) << squareToOccupy);
			advanceEnPassantMoves = advanceEnPassantMoves
					| ((1L & isEnPassant(square, squareToOccupy, position.wm())) << squareToOccupy);
			advanceMoves = advanceMoves | ((1L & ~isPromotion(squareToOccupy)
					& ~isEnPassant(square, squareToOccupy, position.wm())) << squareToOccupy);
		}
		final long visible = visibleSquaresFast(new int[] { 4, 5, 6, 7 }, square, friends, enemies);
		advanceMoves = advanceMoves & ~(friends | enemies | ~visible);
		advanceCoronationMoves = advanceCoronationMoves & ~(friends | enemies | ~visible);
		advanceEnPassantMoves = advanceEnPassantMoves & ~(friends | enemies | ~visible);
		final long pseudoCoronationMoves = advanceCoronationMoves | captureCoronationMoves;
		final long pseudoLegalMoves = advanceMoves | captureMoves;
		final long operation = (br & checkMask) >>> squaresMap(br);
		final long defense = defenseDirection(kingSquare, square);
		final long[] pin1 = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
		final long pinMask1 = pin1[(int) operation];
		final long[] pin2 = new long[] { -1L, pseudoCoronationMoves & checkMask & defense };
		final long pinMask2 = pin2[(int) operation];
		final long[] pin3 = new long[] { -1L, advanceEnPassantMoves & checkMask & defense };
		final long pinMask3 = pin3[(int) operation];
		final long legalMoves = pseudoLegalMoves & pinMask1 & inCheckMask;
		final long legalCoronationMoves = pseudoCoronationMoves & pinMask2 & inCheckMask;
		final long legalAdvanceEnPassantMoves = advanceEnPassantMoves & pinMask3 & inCheckMask;
		generatePositions(legalMoves, pieceType, square, pawnsDirections, enemies, position, children);
		generateCoronations(legalCoronationMoves, pieceType, square, pawnsDirections, position, children);
		generatePositionsWithEnPassant(legalAdvanceEnPassantMoves, pieceType, square, pawnsDirections, enemies,
				position, children);
		generateEnPassantCaptures(possibleEnPassant, pieceType, square, pawnsDirections, captureArray, position,
				children);
	}

	private void queenMoves(long br, int square, int pieceType, int kingSquare, long friends, long enemies,
			Position position, long checkMask, long inCheckMask, List<Position> children) {
		final long defense = defenseDirection(kingSquare, square);
		final long pseudoLegalMoves = visibleSquaresFast(QUEEN_DIRECTIONS, square, friends, enemies);
		final long[] pin = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
		final long pinMask = pin[(int) ((br & checkMask) >>> squaresMap(br & checkMask))];
		long legalMoves = pseudoLegalMoves & pinMask & inCheckMask;
		while (legalMoves != 0L) {
			final long move = legalMoves & -legalMoves;
			final Position newPosition = position.makeClone();
			makeMove(newPosition, move, pieceType, square, enemies, position);
			children.add(newPosition);
			legalMoves = legalMoves & ~move;
		}

	}

	private void rookMoves(long br, int square, int pieceType, int[] pawnsDirections, int kingSquare, long enemies,
			long friends, Position position, long checkMask, long inCheckMask, List<Position> children) {
		final long defense = defenseDirection(kingSquare, square);
		final long pseudoLegalMoves = visibleSquaresFast(ROOK_DIRECTIONS, square, friends, enemies);
		final long[] pin = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
		final long pinMask = pin[(int) ((br & checkMask) >>> squaresMap(br))];
		long legalMoves = pseudoLegalMoves & pinMask & inCheckMask;

		while (legalMoves != 0L) {
			long move = legalMoves & -legalMoves;
			Position newPosition = position.makeClone();
			makeMove(newPosition, move, pieceType, square, enemies, position);
			newPosition.changeColorToMove();
			applyCastleRules(newPosition);
			newPosition.changeColorToMove();
			children.add(newPosition);
			legalMoves = legalMoves & ~move;
		}

	}

	private static int squaresMap(long input) {
		return Long.numberOfTrailingZeros(input);
	}

	@Override
	public String toString() {
		return "Generator 1.2.0";
	}

	private static int transformEnPassant(int enPassant, long whiteMoveNumeric) {
		return 8 * (-2 * (int) whiteMoveNumeric + 1) + enPassant;
	}

	protected static long visibleSquares(long[] bits, int[] directionsIndexs, int square, long whiteMoveNumeric) {
		long moves = 0L;
		final long black = bits[6] | bits[7] | bits[8] | bits[9] | bits[10] | bits[11];
		final long white = bits[0] | bits[1] | bits[2] | bits[3] | bits[4] | bits[5];
		long friends;
		long enemies;
		if (whiteMoveNumeric == 1) {
			friends = white;
			enemies = black;
		} else {
			friends = black;
			enemies = white;
		}
		for (int index : directionsIndexs) {

			moves = moves
					| VISIBLE_METRICS.getVisible(square, index, QUEEN_MEGAMATRIX[square][index], friends, enemies);
		}
		return moves;
	}

	private static long visibleSquaresFast(int[] directionsIndexs, int square, long friends, long enemies) {
		long moves = 0L;
		for (int index : directionsIndexs) {

			moves = moves
					| VISIBLE_METRICS.getVisible(square, index, QUEEN_MEGAMATRIX[square][index], friends, enemies);
		}
		return moves;
	}

}
