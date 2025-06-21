/*
 * Copyright 2025 Miguel Angel Luna Lobos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/lunalobos/chessapi4j/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package chessapi4j;

import java.util.Arrays;
import java.util.List;

//singleton bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
final class GeneratorUtil {
    private static final Logger logger = LoggerFactory.getLogger(GeneratorUtil.class);
    // short castle black bitboard masks, element 0 for king mask, element 1 for
    // rook mask
    private static final long[] SCB_MASK = new long[] { 1L << 60, 1L << 63 };

    // short castle black squares, element 0 for king square, element 1 for rook
    // square
    private static final int[] SCB_SQUARES = new int[] { 60, 63 };

    // short castle white bitboard masks, element 0 for king mask, element 1 for
    // rook mask
    private static final long[] SCW_MASK = new long[] { 1L << 4, 1 << 7 };

    // short castle white squares, element 0 for king square, element 1 for rook
    // square
    private static final int[] SCW_SQUARES = new int[] { 4, 7 };

    // long castle black bitboard masks, element 0 for king mask, element 1 for rook
    // mask
    private static final long[] LCB_MASK = new long[] { 1L << 60, 1L << 56 };

    // long castle black squares, element 0 for king square, element 1 for rook
    // square
    private static final int[] LCB_SQUARES = new int[] { 60, 56 };

    // long castle white bitboard masks, element 0 for king mask, element 1 for rook
    // mask
    private static final long[] LCW_MASK = new long[] { 1L << 4, 1L};

    // long castle white squares, element 0 for king square, element 1 for rook
    // square
    private static final int[] LCW_SQUARES = new int[] { 4, 0 };

    private static final long[][][] CASTLE_MASK = new long[][][] { { SCB_MASK, LCB_MASK }, { SCW_MASK, LCW_MASK } };

    private static final int[][][] CASTLE_SQUARES = new int[][][] { { SCB_SQUARES, LCB_SQUARES },
            { SCW_SQUARES, LCW_SQUARES } };

    private static final int[] KING_PIECES = new int[] { Piece.BK.ordinal(), Piece.WK.ordinal() };
    private static final int[] ROOK_PIECES = new int[] { Piece.BR.ordinal(), Piece.WR.ordinal() };

    static final int[] INDEXES = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

    static final int[][] KING_MATRIX = new int[][] { { 9, 8, 1 }, { 10, 8, 9, 2, 0 }, { 11, 9, 10, 3, 1 },
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
	static final long[] KING_MOVES = Arrays.stream(KING_MATRIX)
			.mapToLong(squares ->
					Arrays.stream(squares).mapToLong(sq -> 1L << sq).reduce(0L, (a,b) -> a | b))
			.toArray();

    static final int[][] KNIGHT_MATRIX = new int[][] { { 17, 10 }, { 18, 16, 11 }, { 19, 17, 12, 8 },
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

    static final int[][] WHITE_PAWN_MATRIX_2 = new int[][] { { 9 }, { 8, 10 }, { 9, 11 }, { 10, 12 },
            { 11, 13 }, { 12, 14 }, { 13, 15 }, { 14 }, { 17 }, { 16, 18 }, { 17, 19 }, { 18, 20 }, { 19, 21 },
            { 20, 22 }, { 21, 23 }, { 22 }, { 25 }, { 24, 26 }, { 25, 27 }, { 26, 28 }, { 27, 29 }, { 28, 30 },
            { 29, 31 }, { 30 }, { 33 }, { 32, 34 }, { 33, 35 }, { 34, 36 }, { 35, 37 }, { 36, 38 }, { 37, 39 }, { 38 },
            { 41 }, { 40, 42 }, { 41, 43 }, { 42, 44 }, { 43, 45 }, { 44, 46 }, { 45, 47 }, { 46 }, { 49 }, { 48, 50 },
            { 49, 51 }, { 50, 52 }, { 51, 53 }, { 52, 54 }, { 53, 55 }, { 54 }, { 57 }, { 56, 58 }, { 57, 59 },
            { 58, 60 }, { 59, 61 }, { 60, 62 }, { 61, 63 }, { 62 }, {}, {}, {}, {}, {}, {}, {}, {} };

    static final int[][] BLACK_PAWN_MATRIX_2 = new int[][] { {}, {}, {}, {}, {}, {}, {}, {}, { 1 }, { 0, 2 },
            { 1, 3 }, { 2, 4 }, { 3, 5 }, { 4, 6 }, { 5, 7 }, { 6 }, { 9 }, { 8, 10 }, { 9, 11 }, { 10, 12 },
            { 11, 13 }, { 12, 14 }, { 13, 15 }, { 14 }, { 17 }, { 16, 18 }, { 17, 19 }, { 18, 20 }, { 19, 21 },
            { 20, 22 }, { 21, 23 }, { 22 }, { 25 }, { 24, 26 }, { 25, 27 }, { 26, 28 }, { 27, 29 }, { 28, 30 },
            { 29, 31 }, { 30 }, { 33 }, { 32, 34 }, { 33, 35 }, { 34, 36 }, { 35, 37 }, { 36, 38 }, { 37, 39 }, { 38 },
            { 41 }, { 40, 42 }, { 41, 43 }, { 42, 44 }, { 43, 45 }, { 44, 46 }, { 45, 47 }, { 46 }, { 49 }, { 48, 50 },
            { 49, 51 }, { 50, 52 }, { 51, 53 }, { 52, 54 }, { 53, 55 }, { 54 } };
    

    private final VisibleMetrics visibleMetrics;
	private final MatrixUtil matrixUtil;

    public GeneratorUtil(VisibleMetrics visibleMetrics, MatrixUtil matrixUtil) {
        this.visibleMetrics = visibleMetrics;
		this.matrixUtil = matrixUtil;
        logger.instantiation();
    }

    int squaresMap(long input) {
        return Long.numberOfTrailingZeros(input);
    }

    long defenseDirection(int kingSquare, int pieceSquare) {
        final int[][] matrix = matrixUtil.queenMegamatrix[kingSquare];
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

    long hasBitsPresent(long bitboard){
        var signum = Long.signum(bitboard);
        return ((long) signum * signum);
    }

    void applyCastleRules(Position position) {
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

    void generatePositions(long moves, int pieceType, int square, long enemies,
            Position position, List<Position> children) {
        while (moves != 0L) {
            final long move = moves & -moves;
            final Position newPosition = position.makeClone();
            makeMove(newPosition, move, pieceType, square, enemies, position);
            children.add(newPosition);
            moves = moves & ~move;
        }
    }

    void makeMove(Position child, long move, int pieceType, int originSquare, long enemies, Position parent) {

        //final long[] bits = child.getBits();
        //for (int index : INDEXES) {
        //    bits[index] = bits[index] & (~move);
        //}
        //bits[pieceType - 1] = (bits[pieceType - 1] & (~(1L << originSquare))) | move;
        //child.setBits(bits);
        //child.changeColorToMove();
        child.makeMove(originSquare, move, pieceType);
        applyCastleRules(child);
        child.increaseMovesCounter();
        child.setEnPassant(-1);
        applyHalfMoveRule(child, move, enemies, parent);
    }

    private void applyHalfMoveRule(Position p, long move, long enemies, Position position) {
        final int aux = (int) (6L & (position.wm() << 1 | position.wm() << 2));

        final long enemyOperation = enemies & move;

        final long pawnOperation = move & p.getBits()[Piece.BP.ordinal() - aux - 1];

        p.setHalfMovesCounter(
                new int[] { p.getHalfMovesCounter() + 1, 0 }[(int) ((pawnOperation >>> squaresMap(pawnOperation))
                        | (enemyOperation >>> squaresMap(enemyOperation)))]);
    }

    long inCheck(int kingPiece, long[] bits, long whiteMoveNumeric, int[] pawnsDirections) {

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
            final long visible = visibleMetrics.visibleBishop(bits, kingSquare, whiteMoveNumeric);
            isInCheck = isInCheck | hasBitsPresent(enemyBishopsAndQuens & visible);
        }

        // rooks directions
        final long enemyRooksAndQuens = bits[enemies[3] - 1] | bits[enemies[4] - 1];
        if (enemyRooksAndQuens != 0L) {
            final long visible = visibleMetrics.visibleRook(bits, kingSquare, whiteMoveNumeric);
            isInCheck = isInCheck | hasBitsPresent(enemyRooksAndQuens & visible);
        }
        return isInCheck;
    }

    long isInCheckD(Position position, int[] pawnsDirections) {
        final int kingPiece = -Piece.WK.ordinal() * (int) position.wm() + Piece.BK.ordinal();
        return inCheck(kingPiece, position.getBits(), position.wm(), pawnsDirections);
    }

    long isInCheck(Position position) {
        final int kingPiece = -Piece.WK.ordinal() * (int) position.wm() + Piece.BK.ordinal();
        final int kingSquare = squaresMap(position.getBits()[kingPiece - 1]);
        final int[][] pawnsDirectionChoice = new int[][] { BLACK_PAWN_MATRIX_2[kingSquare],
                WHITE_PAWN_MATRIX_2[kingSquare] };
        final int[] pawnsDirections = pawnsDirectionChoice[(int) position.wm()];
        return inCheck(kingPiece, position.getBits(), position.wm(), pawnsDirections);
    }

    long isLongCastleBlackEnable(int kingSquare, long enemies, long friends, Position position, long inCheck) {

		final long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
		final long piecesInterruption1 = ((1L << 58) & (enemies | friends)) >>> 58;
		final long piecesInterruption2 = ((1L << 59) & (enemies | friends)) >>> 59;
		final long piecesInterruption3 = ((1L << 57) & (enemies | friends)) >>> 57;
		final long castleEnable = position.bq();
		final long[] testBits1 = new long[12];
        System.arraycopy(position.getBits(), 0, testBits1, 0, 12);

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 59);
			testBits1[index] = testBits1[index] & ~(1L << 60);
		}
		testBits1[Piece.BK.ordinal() - 1] = 1L << 59;
		final long check1 = inCheck(Piece.BK.ordinal(), testBits1, position.wm(), BLACK_PAWN_MATRIX_2[59]);
		final long[] testBits2 = new long[12];
        System.arraycopy(position.getBits(), 0, testBits2, 0, 12);

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 58);
			testBits2[index] = testBits2[index] & ~(1L << 60);
		}
		testBits2[Piece.BK.ordinal() - 1] = 1L << 58;
		final long check2 = inCheck(Piece.BK.ordinal(), testBits2, position.wm(), BLACK_PAWN_MATRIX_2[58]);

		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & castleEnable
				& ~check1 & ~check2 & ~inCheck;
	}

    long isLongCastleBlackEnable(int kingSquare, long enemies, long friends, long[] bitboards, long bq, long wm, long inCheck) {

		final long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
		final long piecesInterruption1 = ((1L << 58) & (enemies | friends)) >>> 58;
		final long piecesInterruption2 = ((1L << 59) & (enemies | friends)) >>> 59;
		final long piecesInterruption3 = ((1L << 57) & (enemies | friends)) >>> 57;
        final long[] testBits1 = new long[12];
        System.arraycopy(bitboards, 0, testBits1, 0, 12);

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 59);
			testBits1[index] = testBits1[index] & ~(1L << 60);
		}
		testBits1[Piece.BK.ordinal() - 1] = 1L << 59;
		final long check1 = inCheck(Piece.BK.ordinal(), testBits1, wm, BLACK_PAWN_MATRIX_2[59]);
		final long[] testBits2 = new long[12];
        System.arraycopy(bitboards, 0, testBits2, 0, 12);

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 58);
			testBits2[index] = testBits2[index] & ~(1L << 60);
		}
		testBits2[Piece.BK.ordinal() - 1] = 1L << 58;
		final long check2 = inCheck(Piece.BK.ordinal(), testBits2, wm, BLACK_PAWN_MATRIX_2[58]);

		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & bq
				& ~check1 & ~check2 & ~inCheck;
	}

	long isLongCastleWhiteEnable(int kingSquare, long enemies, long friends, Position position, long inCheck) {
		final long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
		final long piecesInterruption1 = ((1L << 2) & (enemies | friends)) >>> 2;
		final long piecesInterruption2 = ((1L << 3) & (enemies | friends)) >>> 3;
		final long piecesInterruption3 = ((1L << 1) & (enemies | friends)) >>> 1;
		final long castleEnable = position.wq();
		final long[] testBits1 = new long[12];
        System.arraycopy(position.getBits(), 0, testBits1, 0, 12);

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 3);
			testBits1[index] = testBits1[index] & ~(1L << 4);
		}
		testBits1[Piece.WK.ordinal() - 1] = 1L << 3;
		final long check1 = inCheck(Piece.WK.ordinal(), testBits1, position.wm(), WHITE_PAWN_MATRIX_2[3]);
		final long[] testBits2 = new long[12];
        System.arraycopy(position.getBits(), 0, testBits2, 0, 12);

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 2);
			testBits2[index] = testBits2[index] & ~(1L << 4);
		}
		testBits2[Piece.WK.ordinal() - 1] = 1L << 2;
		final long check2 = inCheck(Piece.WK.ordinal(), testBits2, position.wm(), WHITE_PAWN_MATRIX_2[2]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & castleEnable
				& ~check1 & ~check2 & ~inCheck;
	}

    long isLongCastleWhiteEnable(int kingSquare, long enemies, long friends, long[] bitboards, long wq, long wm,  long inCheck) {
		final long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
		final long piecesInterruption1 = ((1L << 2) & (enemies | friends)) >>> 2;
		final long piecesInterruption2 = ((1L << 3) & (enemies | friends)) >>> 3;
		final long piecesInterruption3 = ((1L << 1) & (enemies | friends)) >>> 1;
        final long[] testBits1 = new long[12];
        System.arraycopy(bitboards, 0, testBits1, 0, 12);

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 3);
			testBits1[index] = testBits1[index] & ~(1L << 4);
		}
		testBits1[Piece.WK.ordinal() - 1] = 1L << 3;
		final long check1 = inCheck(Piece.WK.ordinal(), testBits1, wm, WHITE_PAWN_MATRIX_2[3]);
		final long[] testBits2 = new long[12];
        System.arraycopy(bitboards, 0, testBits2, 0, 12);

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 2);
			testBits2[index] = testBits2[index] & ~(1L << 4);
		}
		testBits2[Piece.WK.ordinal() - 1] = 1L << 2;
		final long check2 = inCheck(Piece.WK.ordinal(), testBits2, wm, WHITE_PAWN_MATRIX_2[2]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & wq
				& ~check1 & ~check2 & ~inCheck;
	}

	long isShortCastleBlackEnable(int kingSquare, long enemies, long friends, Position position, long inCheck) {
		final long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
		final long piecesInterruption1 = ((1L << 61) & (enemies | friends)) >>> 61;
		final long piecesInterruption2 = ((1L << 62) & (enemies | friends)) >>> 62;
		final long castleEnable = position.bk();
		final long[] testBits1 = new long[12];
        System.arraycopy(position.getBits(), 0, testBits1, 0, 12);

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 61);
			testBits1[index] = testBits1[index] & ~(1L << 60);
		}
		testBits1[Piece.BK.ordinal() - 1] = 1L << 61;
		final long check1 = inCheck(Piece.BK.ordinal(), testBits1, position.wm(), BLACK_PAWN_MATRIX_2[61]);
		final long[] testBits2 = new long[12];
        System.arraycopy(position.getBits(), 0, testBits2, 0, 12);

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 62);
			testBits2[index] = testBits2[index] & ~(1L << 60);
		}
		testBits2[Piece.BK.ordinal() - 1] = 1L << 62;
		final long check2 = inCheck(Piece.BK.ordinal(), testBits2, position.wm(), BLACK_PAWN_MATRIX_2[62]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & castleEnable & ~check1 & ~check2 & ~inCheck;
	}

    long isShortCastleBlackEnable(int kingSquare, long enemies, long friends, long[] bitboards, long bk, long wm, long inCheck) {
		final long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
		final long piecesInterruption1 = ((1L << 61) & (enemies | friends)) >>> 61;
		final long piecesInterruption2 = ((1L << 62) & (enemies | friends)) >>> 62;
        final long[] testBits1 = new long[12];
        System.arraycopy(bitboards, 0, testBits1, 0, 12);

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 61);
			testBits1[index] = testBits1[index] & ~(1L << 60);
		}
		testBits1[Piece.BK.ordinal() - 1] = 1L << 61;
		final long check1 = inCheck(Piece.BK.ordinal(), testBits1, wm, BLACK_PAWN_MATRIX_2[61]);
		final long[] testBits2 = new long[12];
        System.arraycopy(bitboards, 0, testBits2, 0, 12);

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 62);
			testBits2[index] = testBits2[index] & ~(1L << 60);
		}
		testBits2[Piece.BK.ordinal() - 1] = 1L << 62;
		final long check2 = inCheck(Piece.BK.ordinal(), testBits2, wm, BLACK_PAWN_MATRIX_2[62]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & bk & ~check1 & ~check2 & ~inCheck;
	}

	long isShortCastleWhiteEnable(int kingSquare, long enemies, long friends, Position position, long inCheck) {

		final long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
		final long piecesInterruption1 = ((1L << 5) & (enemies | friends)) >>> 5;
		final long piecesInterruption2 = ((1L << 6) & (enemies | friends)) >>> 6;
		final long castleEnable = position.wk();
		final long[] testBits1 = new long[12];
        System.arraycopy(position.getBits(), 0, testBits1, 0, 12);

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 5);
			testBits1[index] = testBits1[index] & ~(1L << 4);
		}
		testBits1[Piece.WK.ordinal() - 1] = 1L << 5;
		final long check1 = inCheck(Piece.WK.ordinal(), testBits1, position.wm(), WHITE_PAWN_MATRIX_2[5]);
		final long[] testBits2 = new long[12];
        System.arraycopy(position.getBits(), 0, testBits2, 0, 12);

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 6);
			testBits2[index] = testBits2[index] & ~(1L << 4);
		}
		testBits2[Piece.WK.ordinal() - 1] = 1L << 6;
		final long check2 = inCheck(Piece.WK.ordinal(), testBits2, position.wm(), WHITE_PAWN_MATRIX_2[6]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & castleEnable & ~check1 & ~check2 & ~inCheck;
	}

    long isShortCastleWhiteEnable(int kingSquare, long enemies, long friends, long[] bitboards, long wk, long wm, long inCheck) {

		final long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
		final long piecesInterruption1 = ((1L << 5) & (enemies | friends)) >>> 5;
		final long piecesInterruption2 = ((1L << 6) & (enemies | friends)) >>> 6;
        final long[] testBits1 = new long[12];
        System.arraycopy(bitboards, 0, testBits1, 0, 12);

		for (int index : INDEXES) {
			testBits1[index] = testBits1[index] & ~(1L << 5);
			testBits1[index] = testBits1[index] & ~(1L << 4);
		}
		testBits1[Piece.WK.ordinal() - 1] = 1L << 5;
		final long check1 = inCheck(Piece.WK.ordinal(), testBits1, wm, WHITE_PAWN_MATRIX_2[5]);
		final long[] testBits2 = new long[12];
        System.arraycopy(bitboards, 0, testBits2, 0, 12);

		for (int index : INDEXES) {
			testBits2[index] = testBits2[index] & ~(1L << 6);
			testBits2[index] = testBits2[index] & ~(1L << 4);
		}
		testBits2[Piece.WK.ordinal() - 1] = 1L << 6;
		final long check2 = inCheck(Piece.WK.ordinal(), testBits2, wm, WHITE_PAWN_MATRIX_2[6]);
		return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & wk & ~check1 & ~check2 & ~inCheck;
	}

}