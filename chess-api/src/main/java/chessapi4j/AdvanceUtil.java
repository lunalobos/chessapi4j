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

import java.util.function.Function;

import java.util.stream.IntStream;

/**
 * Low-level utilities that are not entirely safe but very fast.
 *
 * @author lunalobos
 *
 * @since 1.2.0
 */
public class AdvanceUtil {

	private static final Integer[] PIECE_MASK = new Integer[] { 1 << Piece.EMPTY.ordinal(), 1 << Piece.WP.ordinal(),
			1 << Piece.WN.ordinal(), 1 << Piece.WB.ordinal(), 1 << Piece.WR.ordinal(), 1 << Piece.WQ.ordinal(),
			1 << Piece.WK.ordinal(), 1 << Piece.BP.ordinal(), 1 << Piece.BN.ordinal(), 1 << Piece.BB.ordinal(),
			1 << Piece.BR.ordinal(), 1 << Piece.BQ.ordinal(), 1 << Piece.BK.ordinal(), };

	private static final Integer[] MATERIAL = new Integer[] {
			PIECE_MASK[Piece.EMPTY.ordinal()] | PIECE_MASK[Piece.WK.ordinal()] | PIECE_MASK[Piece.BK.ordinal()], // K k
			PIECE_MASK[Piece.WN.ordinal()] | PIECE_MASK[Piece.BN.ordinal()] | PIECE_MASK[Piece.WK.ordinal()]
					| PIECE_MASK[Piece.BK.ordinal()], // KN kn
			PIECE_MASK[Piece.WN.ordinal()] | PIECE_MASK[Piece.WK.ordinal()] | PIECE_MASK[Piece.BK.ordinal()], // KN k
			PIECE_MASK[Piece.BN.ordinal()] | PIECE_MASK[Piece.WK.ordinal()] | PIECE_MASK[Piece.BK.ordinal()], // K kn
			PIECE_MASK[Piece.WN.ordinal()] | PIECE_MASK[Piece.BB.ordinal()] | PIECE_MASK[Piece.WK.ordinal()]
					| PIECE_MASK[Piece.BK.ordinal()], // KN kb
			PIECE_MASK[Piece.WB.ordinal()] | PIECE_MASK[Piece.BN.ordinal()] | PIECE_MASK[Piece.WK.ordinal()]
					| PIECE_MASK[Piece.BK.ordinal()], // KB kn
			PIECE_MASK[Piece.WB.ordinal()] | PIECE_MASK[Piece.BB.ordinal()] | PIECE_MASK[Piece.WK.ordinal()]
					| PIECE_MASK[Piece.BK.ordinal()], // KB kb
			PIECE_MASK[Piece.WB.ordinal()] | PIECE_MASK[Piece.WK.ordinal()] | PIECE_MASK[Piece.BK.ordinal()], // KB k
			PIECE_MASK[Piece.BB.ordinal()] | PIECE_MASK[Piece.WK.ordinal()] | PIECE_MASK[Piece.BK.ordinal()] // K kb
	};

	private static final IntFunction[] BIT_COUNT_FUNCTIONS = new IntFunction[] { i -> 0, i -> i,
			i -> 1 << Piece.values().length, i -> 1 << Piece.values().length, i -> 1 << Piece.values().length,
			i -> 1 << Piece.values().length, i -> 1 << Piece.values().length, i -> 1 << Piece.values().length,
			i -> 1 << Piece.values().length, i -> 1 << Piece.values().length, i -> 1 << Piece.values().length,
			i -> 1 << Piece.values().length };

	private static final Integer[] INVERSE = new Integer[] { 1, 0 };

	private static final int[] HALF_MOVES = new int[500];

	/**
	 * Returns {@code 1} if the position is in check, {@code 0} otherwise.
	 *
	 * @param position
	 * @return {@code 1} if the position is in check, {@code 0} otherwise.
	 */
	public static int isInCheck(Position position) {
		return (int) GeneratorFactory.pseudoInternalSingleton.isInCheck(position);
	}

	static {
		for (int i = 0; i < 50; i++) {
			HALF_MOVES[i] = 0;
		}
		for (int i = 50; i < 500; i++) {
			HALF_MOVES[i] = 1;
		}
	}

	/**
	 * Returns {@code 1} if the position is in a draw due to insufficient material,
	 * {@code 0} otherwise.
	 *
	 * @param position
	 * @return {@code 1} if the position is in a draw due to insufficient material,
	 *         {@code 0} otherwise.
	 */
	public static int lackOfMaterial(Position position) {
		int material = IntStream.range(1, Piece.values().length)
				.map(i -> BIT_COUNT_FUNCTIONS[Long.bitCount(position.getBits()[i - 1])].apply(PIECE_MASK[i]))
				.reduce(0, (a, b) -> a | b);
		return Arrays.stream(MATERIAL).mapToInt(i -> i).map(i -> INVERSE[Integer.signum(i ^ material)]).reduce(0,
				(a, b) -> a | b);
	}

	/**
	 * Returns {@code 1} if the position can be a draw due to the 50-move rule,
	 * {@code 0} otherwise.
	 *
	 * @param position
	 * @return {@code 1} if the position can be a draw due to the 50-move rule,
	 *         {@code 0} otherwise.
	 */
	public static int fiftyMoves(Position position) {
		return HALF_MOVES[position.getHalfMovesCounter()];
	}

}

interface IntFunction extends Function<Integer, Integer> {

}