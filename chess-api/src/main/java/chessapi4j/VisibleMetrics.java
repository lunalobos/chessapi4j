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
 */
class VisibleMetrics {
	private static final Logger logger = LoggerFactory.getLogger(VisibleMetrics.class);
	protected static final int[] BISHOP_DIRECTIONS = new int[] { 0, 1, 2, 3 };
	protected static final int[] ROOK_DIRECTIONS = new int[] { 4, 5, 6, 7 };

	private VisibleMetricsUtil visibleMetricsUtil;
	private VisibleMagic magic;
	public VisibleMetrics(VisibleMetricsUtil visibleMetricsUtil, VisibleMagic magic) {
		this.visibleMetricsUtil = visibleMetricsUtil;
		this.magic = magic;
		logger.instanciation();
	}

	protected final long computeVisible(int square, int[] directionsIndexs, int[][] directions, long friends,
			long enemies) {
		return visibleMetricsUtil.computeVisible(square, directionsIndexs, directions, friends, enemies);
	}

	protected final long getVisible(int square, int index, int[] direction, long friends, long enemies) {
		return visibleMetricsUtil.getVisible(square, index, direction, friends, enemies);
	}

	protected long visibleSquares(long[] bits, int[] directionsIndexs, int square, long whiteMoveNumeric) {
		return visibleMetricsUtil.visibleSquares(bits, directionsIndexs, square, whiteMoveNumeric);
	}

	protected long visibleBishop(long[] bits, int square, long whiteMoveNumeric) {

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

		return visibleSquaresBishop(square, friends, enemies);
	}

	protected long visibleRook(long[] bits, int square, long whiteMoveNumeric) {

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

		return visibleSquaresRook(square, friends, enemies);
	}

	protected long visibleQueen(long[] bits, int square, long whiteMoveNumeric) {

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

		return visibleSquaresQueen(square, friends, enemies);
	}

	protected long visibleSquaresFast(int[] directionsIndexs, int square, long friends, long enemies) {
		long moves = 0L;
		for (int index : directionsIndexs) {
			moves = moves
					| getVisible(square, index, Util.QUEEN_MEGAMATRIX[square][index], friends, enemies);
		}
		return moves;
	}

	protected long visibleSquaresBishop(int square, long friends, long enemies) {
		//return visibleSquaresFast(BISHOP_DIRECTIONS, square, friends, enemies);
		return magic.visibleBishop(square, friends, enemies);
	}

	protected long visibleSquaresRook(int square, long friends, long enemies) {
		//return visibleSquaresFast(ROOK_DIRECTIONS, square, friends, enemies);
		return magic.visibleRook(square, friends, enemies);
	}

	protected long visibleSquaresQueen(int square, long friends, long enemies) {
		return visibleSquaresBishop(square, friends, enemies) | visibleSquaresRook(square, friends, enemies);
	}

}
