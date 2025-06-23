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

//singleton bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
class VisibleMetricsUtil {
	private static final Logger logger = LoggerFactory.getLogger(VisibleMetricsUtil.class);

	private final long[] opts = new long[] { 0L, 1L, 0b11L, 0b111L, 0b1111L, 0b11111L, 0b111111L,
			0b1111111L };

	private final long[][][] visibleOptions = new long[64][8][];

	private final int[] trailingZeros = new int[256];

	public VisibleMetricsUtil() {
		fillMap();
		logger.instantiation();
	}

	

	private void fillMap() {
		trailingZeros[0] = 7;
		for (int i = 1; i < 0b10000000; i++) {
			trailingZeros[i] = Integer.numberOfTrailingZeros(i);
		}
		for (int square = 0; square < 64; square++) {
			for (int directionIndex = 0; directionIndex < 8; directionIndex++) {
				int[] direction = Util.QUEEN_MEGAMATRIX[square][directionIndex];
				long[] options = new long[8];
				for (int optionIndex = 0; optionIndex < 8; optionIndex++) {
					long image = opts[optionIndex];
					long visible = 0L;
					int counter = 0;
					for (int sq : direction) {
						visible |= ((image & (1L << counter)) >>> counter) << sq;
						counter++;
					}
					options[optionIndex] = visible;
				}
				visibleOptions[square][directionIndex] = options;
			}
		}

	}

	final long computeVisible(int square, int[] directionsIndexes, int[][] directions, long friends,
			long enemies) {
		long moves = 0L;
		for (int index : directionsIndexes) {
			moves = moves
					| getVisible(square, index, directions[index], friends, enemies);
		}
		return moves;
	}

	final long getVisible(int square, int index, int[] direction, long friends, long enemies) {

		// space transformation: board -> direction
		int fimage = 0;
		int eimage = 0;
		int counter = 0;
		for (int sq : direction) {
			fimage |= (int) (((friends & (1L << sq)) >>> sq) << counter);
			eimage |= (int) (((enemies & (1L << sq)) >>> sq) << (counter + 1));
			counter++;
		}

		// image for direction space with bit population always <= 7
		final int image = (fimage | eimage) & 0b1111111;

		// trailing zeros count and visible bitboard selection
		return visibleOptions[square][index][trailingZeros[image]];
	}

	

	long visibleSquares(long[] bits, int[] directionsIndexes, int square, long whiteMoveNumeric) {
		long moves = 0L;
		final long black = bits[6] | bits[7] | bits[8] | bits[9] | bits[10] | bits[11];
		final long white = bits[0] | bits[1] | bits[2] | bits[3] | bits[4] | bits[5];
		long friends;
		long enemies;
		if (whiteMoveNumeric == 1L) {
			friends = white;
			enemies = black;
		} else {
			friends = black;
			enemies = white;
		}
		for (int index : directionsIndexes) {
			moves = moves
					| getVisible(square, index, Util.QUEEN_MEGAMATRIX[square][index], friends, enemies);
		}
		return moves;
	}
}
