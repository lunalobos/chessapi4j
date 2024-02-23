package chessapi4j.core;

import java.util.LinkedList;
import java.util.List;

import chessapi4j.Generator;
import chessapi4j.Piece;

/**
 *
 * @author lunalobos
 *
 */
abstract class AbstractGenerator implements Generator {
	protected static final int[] BISHOP_DIRECTIONS = new int[] { 0, 1, 2, 3 };
	protected static final int[] BISHOPS = new int[] { Piece.BB.ordinal(), Piece.WB.ordinal() };
	protected static final int[][] BLACK_PAWN_MATRIX_1 = new int[][] { {}, {}, {}, {}, {}, {}, {}, {}, { 0 }, { 1 },
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

	protected static final long[] SCB_MASK = new long[] { 1L << 60, 1L << 63 };
	protected static final int[] SCB_SQUARES = new int[] { 60, 63 };
	protected static final long[] SCW_MASK = new long[] { 1L << 4, 1 << 7 };
	protected static final int[] SCW_SQUARES = new int[] { 4, 7 };
	protected static final long[] LCB_MASK = new long[] { 1L << 60, 1L << 56 };
	protected static final int[] LCB_SQUARES = new int[] { 60, 56 };
	protected static final long[] LCW_MASK = new long[] { 1L << 4, 1 << 0 };
	protected static final int[] LCW_SQUARES = new int[] { 4, 0 };

	protected static final long[][][] CASTLE_MASK = new long[][][] { { SCB_MASK, LCB_MASK }, { SCW_MASK, LCW_MASK } };

	protected static final int[][][] CASTLE_SQUARES = new int[][][] { { SCB_SQUARES, LCB_SQUARES },
			{ SCW_SQUARES, LCW_SQUARES } };

	protected static final int[] INDEXES = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

	protected static final int[][] KING_MATRIX = new int[][] { { 9, 8, 1 }, { 10, 8, 9, 2, 0 }, { 11, 9, 10, 3, 1 },
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

	protected static final int[] KING_PIECES = new int[] { Piece.BK.ordinal(), Piece.WK.ordinal() };

	protected static final int[] KINGS = new int[] { Piece.BK.ordinal(), Piece.WK.ordinal() };

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
	protected static final int[] KNIGHTS = new int[] { Piece.BN.ordinal(), Piece.WN.ordinal() };

	protected static final int[][] WHITE_PAWN_MATRIX_1 = new int[][] { { 8 }, { 9 }, { 10 }, { 11 }, { 12 }, { 13 },
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
	protected static final int[][][] PAWN_MATRIX1 = new int[][][] { BLACK_PAWN_MATRIX_1, WHITE_PAWN_MATRIX_1 };
	protected static final int[][][] PAWN_MATRIX2 = new int[][][] { BLACK_PAWN_MATRIX_2, WHITE_PAWN_MATRIX_2 };
	protected static final int[] PAWNS = new int[] { Piece.BP.ordinal(), Piece.WP.ordinal() };
	protected static final int[] QUEEN_DIRECTIONS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
	protected static final int[][][] QUEEN_MEGAMATRIX = new int[][][] {
			{ { 9, 18, 27, 36, 45, 54, 63 }, {}, {}, {}, { 8, 16, 24, 32, 40, 48, 56 }, {}, { 1, 2, 3, 4, 5, 6, 7 },
					{} },
			{ { 10, 19, 28, 37, 46, 55 }, { 8 }, {}, {}, { 9, 17, 25, 33, 41, 49, 57 }, {}, { 2, 3, 4, 5, 6, 7 },
					{ 0 } },
			{ { 11, 20, 29, 38, 47 }, { 9, 16 }, {}, {}, { 10, 18, 26, 34, 42, 50, 58 }, {}, { 3, 4, 5, 6, 7 },
					{ 1, 0 } },
			{ { 12, 21, 30, 39 }, { 10, 17, 24 }, {}, {}, { 11, 19, 27, 35, 43, 51, 59 }, {}, { 4, 5, 6, 7 },
					{ 2, 1, 0 } },
			{ { 13, 22, 31 }, { 11, 18, 25, 32 }, {}, {}, { 12, 20, 28, 36, 44, 52, 60 }, {}, { 5, 6, 7 },
					{ 3, 2, 1, 0 } },
			{ { 14, 23 }, { 12, 19, 26, 33, 40 }, {}, {}, { 13, 21, 29, 37, 45, 53, 61 }, {}, { 6, 7 },
					{ 4, 3, 2, 1, 0 } },
			{ { 15 }, { 13, 20, 27, 34, 41, 48 }, {}, {}, { 14, 22, 30, 38, 46, 54, 62 }, {}, { 7 },
					{ 5, 4, 3, 2, 1, 0 } },
			{ {}, { 14, 21, 28, 35, 42, 49, 56 }, {}, {}, { 15, 23, 31, 39, 47, 55, 63 }, {}, {},
					{ 6, 5, 4, 3, 2, 1, 0 } },
			{ { 17, 26, 35, 44, 53, 62 }, {}, {}, { 1 }, { 16, 24, 32, 40, 48, 56 }, { 0 },
					{ 9, 10, 11, 12, 13, 14, 15 }, {} },
			{ { 18, 27, 36, 45, 54, 63 }, { 16 }, { 0 }, { 2 }, { 17, 25, 33, 41, 49, 57 }, { 1 },
					{ 10, 11, 12, 13, 14, 15 }, { 8 } },
			{ { 19, 28, 37, 46, 55 }, { 17, 24 }, { 1 }, { 3 }, { 18, 26, 34, 42, 50, 58 }, { 2 },
					{ 11, 12, 13, 14, 15 }, { 9, 8 } },
			{ { 20, 29, 38, 47 }, { 18, 25, 32 }, { 2 }, { 4 }, { 19, 27, 35, 43, 51, 59 }, { 3 }, { 12, 13, 14, 15 },
					{ 10, 9, 8 } },
			{ { 21, 30, 39 }, { 19, 26, 33, 40 }, { 3 }, { 5 }, { 20, 28, 36, 44, 52, 60 }, { 4 }, { 13, 14, 15 },
					{ 11, 10, 9, 8 } },
			{ { 22, 31 }, { 20, 27, 34, 41, 48 }, { 4 }, { 6 }, { 21, 29, 37, 45, 53, 61 }, { 5 }, { 14, 15 },
					{ 12, 11, 10, 9, 8 } },
			{ { 23 }, { 21, 28, 35, 42, 49, 56 }, { 5 }, { 7 }, { 22, 30, 38, 46, 54, 62 }, { 6 }, { 15 },
					{ 13, 12, 11, 10, 9, 8 } },
			{ {}, { 22, 29, 36, 43, 50, 57 }, { 6 }, {}, { 23, 31, 39, 47, 55, 63 }, { 7 }, {},
					{ 14, 13, 12, 11, 10, 9, 8 } },
			{ { 25, 34, 43, 52, 61 }, {}, {}, { 9, 2 }, { 24, 32, 40, 48, 56 }, { 8, 0 },
					{ 17, 18, 19, 20, 21, 22, 23 }, {} },
			{ { 26, 35, 44, 53, 62 }, { 24 }, { 8 }, { 10, 3 }, { 25, 33, 41, 49, 57 }, { 9, 1 },
					{ 18, 19, 20, 21, 22, 23 }, { 16 } },
			{ { 27, 36, 45, 54, 63 }, { 25, 32 }, { 9, 0 }, { 11, 4 }, { 26, 34, 42, 50, 58 }, { 10, 2 },
					{ 19, 20, 21, 22, 23 }, { 17, 16 } },
			{ { 28, 37, 46, 55 }, { 26, 33, 40 }, { 10, 1 }, { 12, 5 }, { 27, 35, 43, 51, 59 }, { 11, 3 },
					{ 20, 21, 22, 23 }, { 18, 17, 16 } },
			{ { 29, 38, 47 }, { 27, 34, 41, 48 }, { 11, 2 }, { 13, 6 }, { 28, 36, 44, 52, 60 }, { 12, 4 },
					{ 21, 22, 23 }, { 19, 18, 17, 16 } },
			{ { 30, 39 }, { 28, 35, 42, 49, 56 }, { 12, 3 }, { 14, 7 }, { 29, 37, 45, 53, 61 }, { 13, 5 }, { 22, 23 },
					{ 20, 19, 18, 17, 16 } },
			{ { 31 }, { 29, 36, 43, 50, 57 }, { 13, 4 }, { 15 }, { 30, 38, 46, 54, 62 }, { 14, 6 }, { 23 },
					{ 21, 20, 19, 18, 17, 16 } },
			{ {}, { 30, 37, 44, 51, 58 }, { 14, 5 }, {}, { 31, 39, 47, 55, 63 }, { 15, 7 }, {},
					{ 22, 21, 20, 19, 18, 17, 16 } },
			{ { 33, 42, 51, 60 }, {}, {}, { 17, 10, 3 }, { 32, 40, 48, 56 }, { 16, 8, 0 },
					{ 25, 26, 27, 28, 29, 30, 31 }, {} },
			{ { 34, 43, 52, 61 }, { 32 }, { 16 }, { 18, 11, 4 }, { 33, 41, 49, 57 }, { 17, 9, 1 },
					{ 26, 27, 28, 29, 30, 31 }, { 24 } },
			{ { 35, 44, 53, 62 }, { 33, 40 }, { 17, 8 }, { 19, 12, 5 }, { 34, 42, 50, 58 }, { 18, 10, 2 },
					{ 27, 28, 29, 30, 31 }, { 25, 24 } },
			{ { 36, 45, 54, 63 }, { 34, 41, 48 }, { 18, 9, 0 }, { 20, 13, 6 }, { 35, 43, 51, 59 }, { 19, 11, 3 },
					{ 28, 29, 30, 31 }, { 26, 25, 24 } },
			{ { 37, 46, 55 }, { 35, 42, 49, 56 }, { 19, 10, 1 }, { 21, 14, 7 }, { 36, 44, 52, 60 }, { 20, 12, 4 },
					{ 29, 30, 31 }, { 27, 26, 25, 24 } },
			{ { 38, 47 }, { 36, 43, 50, 57 }, { 20, 11, 2 }, { 22, 15 }, { 37, 45, 53, 61 }, { 21, 13, 5 }, { 30, 31 },
					{ 28, 27, 26, 25, 24 } },
			{ { 39 }, { 37, 44, 51, 58 }, { 21, 12, 3 }, { 23 }, { 38, 46, 54, 62 }, { 22, 14, 6 }, { 31 },
					{ 29, 28, 27, 26, 25, 24 } },
			{ {}, { 38, 45, 52, 59 }, { 22, 13, 4 }, {}, { 39, 47, 55, 63 }, { 23, 15, 7 }, {},
					{ 30, 29, 28, 27, 26, 25, 24 } },
			{ { 41, 50, 59 }, {}, {}, { 25, 18, 11, 4 }, { 40, 48, 56 }, { 24, 16, 8, 0 },
					{ 33, 34, 35, 36, 37, 38, 39 }, {} },
			{ { 42, 51, 60 }, { 40 }, { 24 }, { 26, 19, 12, 5 }, { 41, 49, 57 }, { 25, 17, 9, 1 },
					{ 34, 35, 36, 37, 38, 39 }, { 32 } },
			{ { 43, 52, 61 }, { 41, 48 }, { 25, 16 }, { 27, 20, 13, 6 }, { 42, 50, 58 }, { 26, 18, 10, 2 },
					{ 35, 36, 37, 38, 39 }, { 33, 32 } },
			{ { 44, 53, 62 }, { 42, 49, 56 }, { 26, 17, 8 }, { 28, 21, 14, 7 }, { 43, 51, 59 }, { 27, 19, 11, 3 },
					{ 36, 37, 38, 39 }, { 34, 33, 32 } },
			{ { 45, 54, 63 }, { 43, 50, 57 }, { 27, 18, 9, 0 }, { 29, 22, 15 }, { 44, 52, 60 }, { 28, 20, 12, 4 },
					{ 37, 38, 39 }, { 35, 34, 33, 32 } },
			{ { 46, 55 }, { 44, 51, 58 }, { 28, 19, 10, 1 }, { 30, 23 }, { 45, 53, 61 }, { 29, 21, 13, 5 }, { 38, 39 },
					{ 36, 35, 34, 33, 32 } },
			{ { 47 }, { 45, 52, 59 }, { 29, 20, 11, 2 }, { 31 }, { 46, 54, 62 }, { 30, 22, 14, 6 }, { 39 },
					{ 37, 36, 35, 34, 33, 32 } },
			{ {}, { 46, 53, 60 }, { 30, 21, 12, 3 }, {}, { 47, 55, 63 }, { 31, 23, 15, 7 }, {},
					{ 38, 37, 36, 35, 34, 33, 32 } },
			{ { 49, 58 }, {}, {}, { 33, 26, 19, 12, 5 }, { 48, 56 }, { 32, 24, 16, 8, 0 },
					{ 41, 42, 43, 44, 45, 46, 47 }, {} },
			{ { 50, 59 }, { 48 }, { 32 }, { 34, 27, 20, 13, 6 }, { 49, 57 }, { 33, 25, 17, 9, 1 },
					{ 42, 43, 44, 45, 46, 47 }, { 40 } },
			{ { 51, 60 }, { 49, 56 }, { 33, 24 }, { 35, 28, 21, 14, 7 }, { 50, 58 }, { 34, 26, 18, 10, 2 },
					{ 43, 44, 45, 46, 47 }, { 41, 40 } },
			{ { 52, 61 }, { 50, 57 }, { 34, 25, 16 }, { 36, 29, 22, 15 }, { 51, 59 }, { 35, 27, 19, 11, 3 },
					{ 44, 45, 46, 47 }, { 42, 41, 40 } },
			{ { 53, 62 }, { 51, 58 }, { 35, 26, 17, 8 }, { 37, 30, 23 }, { 52, 60 }, { 36, 28, 20, 12, 4 },
					{ 45, 46, 47 }, { 43, 42, 41, 40 } },
			{ { 54, 63 }, { 52, 59 }, { 36, 27, 18, 9, 0 }, { 38, 31 }, { 53, 61 }, { 37, 29, 21, 13, 5 }, { 46, 47 },
					{ 44, 43, 42, 41, 40 } },
			{ { 55 }, { 53, 60 }, { 37, 28, 19, 10, 1 }, { 39 }, { 54, 62 }, { 38, 30, 22, 14, 6 }, { 47 },
					{ 45, 44, 43, 42, 41, 40 } },
			{ {}, { 54, 61 }, { 38, 29, 20, 11, 2 }, {}, { 55, 63 }, { 39, 31, 23, 15, 7 }, {},
					{ 46, 45, 44, 43, 42, 41, 40 } },
			{ { 57 }, {}, {}, { 41, 34, 27, 20, 13, 6 }, { 56 }, { 40, 32, 24, 16, 8, 0 },
					{ 49, 50, 51, 52, 53, 54, 55 }, {} },
			{ { 58 }, { 56 }, { 40 }, { 42, 35, 28, 21, 14, 7 }, { 57 }, { 41, 33, 25, 17, 9, 1 },
					{ 50, 51, 52, 53, 54, 55 }, { 48 } },
			{ { 59 }, { 57 }, { 41, 32 }, { 43, 36, 29, 22, 15 }, { 58 }, { 42, 34, 26, 18, 10, 2 },
					{ 51, 52, 53, 54, 55 }, { 49, 48 } },
			{ { 60 }, { 58 }, { 42, 33, 24 }, { 44, 37, 30, 23 }, { 59 }, { 43, 35, 27, 19, 11, 3 }, { 52, 53, 54, 55 },
					{ 50, 49, 48 } },
			{ { 61 }, { 59 }, { 43, 34, 25, 16 }, { 45, 38, 31 }, { 60 }, { 44, 36, 28, 20, 12, 4 }, { 53, 54, 55 },
					{ 51, 50, 49, 48 } },
			{ { 62 }, { 60 }, { 44, 35, 26, 17, 8 }, { 46, 39 }, { 61 }, { 45, 37, 29, 21, 13, 5 }, { 54, 55 },
					{ 52, 51, 50, 49, 48 } },
			{ { 63 }, { 61 }, { 45, 36, 27, 18, 9, 0 }, { 47 }, { 62 }, { 46, 38, 30, 22, 14, 6 }, { 55 },
					{ 53, 52, 51, 50, 49, 48 } },
			{ {}, { 62 }, { 46, 37, 28, 19, 10, 1 }, {}, { 63 }, { 47, 39, 31, 23, 15, 7 }, {},
					{ 54, 53, 52, 51, 50, 49, 48 } },
			{ {}, {}, {}, { 49, 42, 35, 28, 21, 14, 7 }, {}, { 48, 40, 32, 24, 16, 8, 0 },
					{ 57, 58, 59, 60, 61, 62, 63 }, {} },
			{ {}, {}, { 48 }, { 50, 43, 36, 29, 22, 15 }, {}, { 49, 41, 33, 25, 17, 9, 1 }, { 58, 59, 60, 61, 62, 63 },
					{ 56 } },
			{ {}, {}, { 49, 40 }, { 51, 44, 37, 30, 23 }, {}, { 50, 42, 34, 26, 18, 10, 2 }, { 59, 60, 61, 62, 63 },
					{ 57, 56 } },
			{ {}, {}, { 50, 41, 32 }, { 52, 45, 38, 31 }, {}, { 51, 43, 35, 27, 19, 11, 3 }, { 60, 61, 62, 63 },
					{ 58, 57, 56 } },
			{ {}, {}, { 51, 42, 33, 24 }, { 53, 46, 39 }, {}, { 52, 44, 36, 28, 20, 12, 4 }, { 61, 62, 63 },
					{ 59, 58, 57, 56 } },
			{ {}, {}, { 52, 43, 34, 25, 16 }, { 54, 47 }, {}, { 53, 45, 37, 29, 21, 13, 5 }, { 62, 63 },
					{ 60, 59, 58, 57, 56 } },
			{ {}, {}, { 53, 44, 35, 26, 17, 8 }, { 55 }, {}, { 54, 46, 38, 30, 22, 14, 6 }, { 63 },
					{ 61, 60, 59, 58, 57, 56 } },
			{ {}, {}, { 54, 45, 36, 27, 18, 9, 0 }, {}, {}, { 55, 47, 39, 31, 23, 15, 7 }, {},
					{ 62, 61, 60, 59, 58, 57, 56 } } };
	protected static final int[] QUEENS = new int[] { Piece.BQ.ordinal(), Piece.WQ.ordinal() };
	protected static final int[] ROOK_DIRECTIONS = new int[] { 4, 5, 6, 7 };
	protected static final int[] ROOK_PIECES = new int[] { Piece.BR.ordinal(), Piece.WR.ordinal() };

	protected static final int[] ROOKS = new int[] { Piece.BR.ordinal(), Piece.WR.ordinal() };

	protected static final int[] SQUARES = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
			19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45,
			46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63 };

	protected static long isCoronation(int finalSquare) {
		return ((((finalSquare >>> 3) & 7L) >>> 2) & ((((finalSquare >>> 3) & 7L) >>> 1) & 1L)
				& (((finalSquare >>> 3) & 7L) & 1L))
				| (((((63 - finalSquare) >>> 3) & 7L) >>> 2) & (((((63 - finalSquare) >>> 3) & 7L) >>> 1) & 1L)
						& ((((63 - finalSquare) >>> 3) & 7L) & 1L));
	}

	protected static long isEnPassant(int originSquare, int finalSquare, long whiteMoveNumeric) {
		long difference = finalSquare - originSquare;
		long choice[] = new long[] { ~difference + 1, difference };
		long maskedDifference = 16L & choice[(int) whiteMoveNumeric];
		return maskedDifference >>> 4;
	}

	protected long isInCheck(BitPosition position) {
		int kingPiece = -Piece.WK.ordinal() * (int) position.getWhiteMoveNumeric() + Piece.BK.ordinal();
		int kingSquare = squaresMap(position.getBits()[kingPiece - 1]);
		if (kingSquare >= 64)
			System.out.println("finded");
		int[][] pawnsDirectionChoice = new int[][] { BLACK_PAWN_MATRIX_2[kingSquare], WHITE_PAWN_MATRIX_2[kingSquare] };
		int[] pawnsDirections = pawnsDirectionChoice[(int) position.getWhiteMoveNumeric()];
		return isInCheck(kingPiece, position.getBits(), position.getWhiteMoveNumeric(), pawnsDirections);
	}

	protected long isInCheck(BitPosition position, int[] pawnsDirections) {
		int kingPiece = -Piece.WK.ordinal() * (int) position.getWhiteMoveNumeric() + Piece.BK.ordinal();
		return isInCheck(kingPiece, position.getBits(), position.getWhiteMoveNumeric(), pawnsDirections);
	}

	protected long isInCheck(int kingPiece, long[] bits, long whiteMoveNumeric, int[] pawnsDirections) {

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
			isInCheck = isInCheck | (((enemyPawns & enemyPawnDangerLocation) >>> pawnDirection));
		}
		// kings directions
		int[] kingDirections = KING_MATRIX[kingSquare];
		long kingDirectionsBits = 0L;
		int enemyKing = enemys[5];
		for (int square : kingDirections) {
			kingDirectionsBits = kingDirectionsBits | (1L << square);
		}
		long operation = kingDirectionsBits & bits[enemyKing - 1];
		isInCheck = isInCheck | (operation >>> squaresMap(operation));
		// knight directions
		int enemyKnight = enemys[1];
		long enemyKnights = bits[enemyKnight - 1];
		int[] knightDirections = KNIGHT_MATRIX[kingSquare];
		long knightDirectionsBits;
		for (int square : knightDirections) {
			knightDirectionsBits = 1L << square;
			isInCheck = isInCheck | ((knightDirectionsBits & enemyKnights) >>> square);
		}
		// bishops directions
		long enemyBishopsAndQuens = bits[enemys[2] - 1] | bits[enemys[4] - 1];

		for (int i = 0; i < 4; i++) {
			long visible = visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);
			isInCheck = isInCheck | ((enemyBishopsAndQuens & visible) >>> squaresMap(enemyBishopsAndQuens & visible));
		}
		// rooks directions
		long enemyRooksAndQuens = bits[enemys[3] - 1] | bits[enemys[4] - 1];
		for (int i = 4; i < 8; i++) {
			long visible = visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);
			isInCheck = isInCheck | ((enemyRooksAndQuens & visible) >>> squaresMap(enemyRooksAndQuens & visible));
		}
		return isInCheck;
	}

	protected static List<Long> longToList(long pseudoLegalMoves) {
		List<Long> list = new LinkedList<>();
		long j = pseudoLegalMoves;
		while (j != 0L) {
			long lb = j & -j;
			list.add(lb);
			j = j & ~lb;
		}
		return list;
	}

	protected static int squaresMap(long input) {
		return Long.numberOfTrailingZeros(input);
	}

	protected static int transformEnPassant(int enPassant, long whiteMoveNumeric) {
		return 8 * (-2 * (int) whiteMoveNumeric + 1) + enPassant;
	}

	protected static long visibleDirection(int[] direction, long friends, long enemys) {
		long empty = ~(enemys | friends);
		long enemyOrEmpty = empty | enemys;
		long directionN = 0L;
		for (int index : direction) {
			directionN = directionN | (1L << index);
		}
		long directionVisible = directionN & enemyOrEmpty;
		long enemysDirection = enemys & directionN;
		long[] operator = new long[] { directionN, 0L };
		long directionI = directionN;
		long[] operator2 = new long[] { directionI, 0L };

		directionN = 0L;
		for (int index : direction) {
			int operation = (int) (((1L << index) & directionVisible) >>> index);
			int operation3 = (int) (((1L << index) & enemysDirection) >>> index);
			directionN = directionN | (1L << index);
			long operation2 = ~(operator[operation] ^ directionN) | directionN;
			long operation4 = ~(operator2[operation3] ^ directionI) | directionN;
			directionVisible = directionVisible & operation2 & operation4;
		}
		return directionVisible;
	}

	protected long visibleSquares(BitPosition position, int[] directionsIndexs, int square) {
		return visibleSquares(position.getBits(), directionsIndexs, square, position.getWhiteMoveNumeric());
	}

	protected long visibleSquares(long[] bits, int[] directionsIndexs, int square, long whiteMoveNumeric) {
		int[][] matrix = QUEEN_MEGAMATRIX[square];
		long moves = 0L;
		int aux = (int) (6L & (whiteMoveNumeric << 1 | whiteMoveNumeric << 2));
		long friends = bits[6 - aux] | bits[7 - aux] | bits[8 - aux] | bits[9 - aux] | bits[10 - aux] | bits[11 - aux];
		long enemys = bits[0 + aux] | bits[1 + aux] | bits[2 + aux] | bits[3 + aux] | bits[4 + aux] | bits[5 + aux];
		for (int index : directionsIndexs) {
			moves = moves | visibleDirection(matrix[index], friends, enemys);
		}
		return moves;
	}


}
