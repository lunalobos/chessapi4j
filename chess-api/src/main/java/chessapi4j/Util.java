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
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class. Not all of these class methods are safe, be careful.
 *
 * @author lunalobos
 * @since 1.0.0
 */
public class Util {
	private static final String[] COLS = new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };

	/**
	 * This matrix contains all the information about queen's movements. The idea is
	 * that the first index corresponds to the square and the second one to the
	 * direction and sense ({@code MoveDirection} ordinal order ). After calling
	 * these two, we obtain an array of the squares to which the queen can move if
	 * the board is empty in progressive order (without the initial square).
	 */
	public static final int[][][] QUEEN_MEGAMATRIX = new int[][][] {
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

	/**
	 * This array indicates the directions in which the queen can move according to
	 * the ordinal indices of the {@code MoveDirection} enum class.
	 */
	public static final int[] QUEEN_DIRECTIONS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };

	/**
	 * This array indicates the directions in which the bishop can move according to
	 * the ordinal indices of the {@code MoveDirection} enum class.
	 */
	public static final int[] BISHOP_DIRECTIONS = new int[] { 0, 1, 2, 3 };

	/**
	 * This array indicates the directions in which the rook can move according to
	 * the ordinal indices of the {@code MoveDirection} enum class.
	 */
	public static final int[] ROOK_DIRECTIONS = new int[] { 4, 5, 6, 7 };

	/**
	 * Retrieves visible squares bitboard representation.
	 *
	 * @param position
	 * @param directionAndSense
	 * @param square
	 * @return
	 *
	 * @since 1.2.3
	 */
	public static long visibleSquares(Position position, MoveDirection directionAndSense, Square square) {
		return GeneratorFactory.visibleMetrics.visibleSquares(position.getBits(),
				new int[] { directionAndSense.ordinal() }, square.ordinal(),
				position.wm());
	}

	/**
	 * Retrieves visible squares in all directions in a long as bitboard representation.
	 *
	 * @param position
	 * @param square
	 * @return
	 *
	 * @since 1.2.3
	 */
	public static long visibleSquares(Position position, Square square) {
		var piece = position.getPiece(square);
		var side = piece.side();
		long friends;
		long enemies;
		if (side == Side.WHITE) {
			friends = Bitboard.or(position.getBitboard(Piece.WP), position.getBitboard(Piece.WN),
					position.getBitboard(Piece.WB), position.getBitboard(Piece.WR), position.getBitboard(Piece.WQ),
					position.getBitboard(Piece.WK)).getValue();
			enemies = Bitboard.or(position.getBitboard(Piece.BP), position.getBitboard(Piece.BN),
					position.getBitboard(Piece.BB), position.getBitboard(Piece.BR), position.getBitboard(Piece.BQ),
					position.getBitboard(Piece.BK)).getValue();
		} else {
			friends = Bitboard.or(position.getBitboard(Piece.BP), position.getBitboard(Piece.BN),
					position.getBitboard(Piece.BB), position.getBitboard(Piece.BR), position.getBitboard(Piece.BQ),
					position.getBitboard(Piece.BK)).getValue();
			enemies = Bitboard.or(position.getBitboard(Piece.WP), position.getBitboard(Piece.WN),
					position.getBitboard(Piece.WB), position.getBitboard(Piece.WR), position.getBitboard(Piece.WQ),
					position.getBitboard(Piece.WK)).getValue();

		}
		return GeneratorFactory.visibleMetrics.visibleSquaresQueen(square.ordinal(), friends, enemies);
	}

	/*
	 * Retrieves visible squares bitboard representation.
	 */
	protected static long visibleSquares(Position position, int[] directionsIndexs, int square) {

		return GeneratorFactory.visibleMetrics.visibleSquares(position.getBits(), directionsIndexs, square,
				position.wm());
	}

	/**
	 * Separates the given bitboard in individuals bitboards, one for each bit.
	 *
	 * @param bitRep
	 * @return a list containing the separated bitboards
	 */
	public static List<Long> longToList(long bitRep) {
		long j = bitRep;
		List<Long> output = new LinkedList<>();
		while (j != 0) {
			long b = j & -j;
			j &= ~b;
			output.add(b);
		}
		return output;
	}

	/**
	 * Column number for the given square (zero-based)
	 *
	 * @param square
	 * @return the column number for the given square
	 */
	public static int getCol(int square) {
		return square & 7;
	}

	/**
	 * Column number for the given square object
	 *
	 * @param square
	 * @return the column number for the given square
	 *
	 * @since 1.2.3
	 */
	public static int getCol(Square square) {
		return square.ordinal() & 7;
	}

	/**
	 * Row number for the given square (zero-based)
	 *
	 * @param square
	 * @return the row number for the given square
	 */
	public static int getRow(int square) {
		return square >> 3;
	}

	/**
	 * Row number for the given square object
	 *
	 * @param square
	 * @return the row number for the given square
	 *
	 * @since 1.2.3
	 */
	public static int getRow(Square square) {
		return square.ordinal() >> 3;
	}

	/**
	 * Column character for the given square
	 *
	 * WARNING: the square number can only be in the range of 0 (included) to 64
	 * (excluded) otherwise ArrayIndexOutOfBoundsException will be thrown.
	 *
	 * @param square
	 * @return the column character for the given square
	 *
	 * @throws ArrayIndexOutOfBoundsException if square is not a valid
	 * square number
	 */
	public static String getColLetter(int square) {
		int colNum = getCol(square);
		return COLS[colNum];
	}

	/**
	 * Square for the given column and row
	 *
	 * @param col
	 * @param row
	 * @return the column character for the given square
	 */
	public static int getSquareIndex(int col, int row) {
		return col + row * 8;
	}

	/**
	 * Promotion checker for the given target square
	 *
	 * @param targetSquare
	 * @return true if it is a promotion square
	 */
	public static boolean isPromotion(int targetSquare) {
		return Generator.isPromotion(targetSquare) == 1L;
	}

	/**
	 * Promotion checker for the given target square. This method returns true if
	 * the given square is in the 1st or 8th row.
	 * <p>
	 * This method doesn't work for relative promotions based on a specific side; it
	 * simply checks the row.
	 *
	 * @param targetSquare
	 * @return true if it is a promotion square
	 *
	 * @since 1.2.3
	 */
	public static boolean isPromotion(Square targetSquare) {
		return PawnGenerator.isPromotion(targetSquare.ordinal()) == 1L;
	}

	/**
	 * Square number for the given square in algebraic notation.
	 * <p>
	 * WARNING: If the input string is not a valid square, the behavior of this
	 * function is unpredictable and may result in fatal exceptions.
	 * <p>
	 * For a secure functionality, resort to the method
	 * {@link #getSquareIndex(Square)}.
	 *
	 * @param square
	 * @return square number for the given square in algebraic notation
	 */
	public static int getSquareIndex(String square) {
		char[] chars = square.toCharArray();
		int collum = getColIndex(new String(new char[] { chars[0] }));
		int row = Integer.parseInt(new String(new char[] { chars[1] })) - 1;
		return getSquareIndex(collum, row);
	}

	/**
	 * Retrieves the square object for the given algebraic notation square.
	 *
	 * @param square the algebraic notation square
	 * @return the square object for the given algebraic notation square
	 *
	 * @since 1.2.7
	 */
	public static Square getSquare(String square) {
		return Square.values()[getSquareIndex(square)];
	}

	/**
	 * Square number for the given square in algebraic notation.
	 *
	 * @param square
	 * @return square number for the given square in algebraic notation
	 *
	 * @since 1.2.3
	 */
	public static int getSquareIndex(Square square) {
		return square.ordinal();
	}

	/**
	 * Column number for the given column character
	 * <p>
	 * If the input string is not a valid column, the result will be less than
	 * {@code 0}.
	 *
	 * @param column the column name (a, b, c, ... , h)
	 * @return the column number for the given column character
	 */
	public static int getColIndex(String column) {
		return Arrays.binarySearch(COLS, column);
	}

	/**
	 * True if the position is in check false otherwise.
	 * <p>
	 * WARNING: If the position inserted is not a valid position, the behavior of
	 * this method is undefined and may result in fatal exceptions.
	 *
	 * @param position the position to check for a check
	 * @return true if the position is in check false otherwise
	 */
	public static boolean isInCheck(Position position) {
		return GeneratorFactory.generatorUtil.isInCheck(position) == 1;
	}

	/**
	 * Piece counter for the given position.
	 *
	 * @param position the position to look at
	 * @return the number of pieces for the given position
	 */
	public static int countPieces(Position position) {
		long[] bits = position.getBits();
		int sum = 0;
		for (long bitRep : bits) {
			sum += Long.bitCount(bitRep);
		}
		return sum;
	}
}
