package chessapi4j.core;

import java.util.Arrays;
import java.util.List;

import chessapi4j.Position;

/**
 * Utility class to avoid boilerplate code.
 * 
 * @author lunalobos
 *
 */
public class Util {
	private static final String[] COLS = new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };

	public static final int[][][] QUEEN_MEGAMATRIX = AbstractGenerator.QUEEN_MEGAMATRIX;

	public static final int[] QUEEN_DIRECTIONS = AbstractGenerator.QUEEN_DIRECTIONS;

	public static final int[] BISHOP_DIRECTIONS = AbstractGenerator.BISHOP_DIRECTIONS;

	public static final int[] ROOK_DIRECTIONS = AbstractGenerator.ROOK_DIRECTIONS;

	public static final int[][] WHITE_PAWN_MATRIX_2 = AbstractGenerator.WHITE_PAWN_MATRIX_2;

	public static final int[][] BLACK_PAWN_MATRIX_2 = AbstractGenerator.BLACK_PAWN_MATRIX_2;

	public static final int[][] KNIGHT_MATRIX = AbstractGenerator.KNIGHT_MATRIX;

	public static long visibleSquares(Position position, int[] directionsIndexs, int square) {
		BitPosition p;
		if (position instanceof BitPosition)
			p = (BitPosition) position;
		else
			p = new BitPosition(position.toFen());
		return AbstractGenerator.visibleSquares(p, directionsIndexs, square);
	}

	public static List<Long> longToList(long bitRep) {
		return AbstractGenerator.longToList(bitRep);
	}

	public static int getCol(int square) {
		return square & 7;
	}

	public static int getRow(int square) {
		return square >> 3;
	}

	public static String getColLetter(int square) {
		int colNum = getCol(square);
		return COLS[colNum];
	}

	public static int getSquareIndex(int col, int row) {
		return col + row * 8;
	}

	public static boolean isCoronation(int destinySquare) {
		return AbstractGenerator.isCoronation(destinySquare) == 1L;
	}

	public static int getSquareIndex(String square) {
		char[] chars = square.toCharArray();
		int collum = getColIndex(new String(new char[] { chars[0] }));
		int row = Integer.parseInt(new String(new char[] { chars[1] })) - 1;
		return getSquareIndex(collum, row);
	}

	public static int getColIndex(String col) {
		return Arrays.binarySearch(COLS, col);
	}

	public static boolean isInCheck(Position position) {
		if (position instanceof BitPosition)
			return AbstractGenerator.isInCheck((BitPosition) position) == 1;
		else
			return AbstractGenerator.isInCheck(new BitPosition(position.toFen())) == 1;
	}

	public static int countPieces(Position pos) {
		long[] bits = pos.getBits();
		int sum = 0;
		for (long bitRep : bits) {
			sum += Long.bitCount(bitRep);
		}
		return sum;
	}
}
