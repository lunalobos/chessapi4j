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

import java.util.List;

//singleton bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
class KnightGenerator {
	private static final Logger logger = LoggerFactory.getLogger(KnightGenerator.class);
	private final GeneratorUtil generatorUtil;
	private final MatrixUtil matrixUtil;

	public KnightGenerator(GeneratorUtil generatorUtil, MatrixUtil matrixUtil) {
		this.generatorUtil = generatorUtil;
		this.matrixUtil = matrixUtil;
		logger.instantiation();
	}

	public void knightMoves(long br, int square, int pieceType, long enemies, long friends, Position pos,
			long checkMask, long inCheckMask, List<Position> children) {
		final int[] knightDirections = matrixUtil.knightMatrix[square];
		final long emptyOrEnemy = ~friends;
		long moves = 0L;
		for (int move : knightDirections) {
			moves = moves | (1L << move);
		}
		final long[] pin = new long[] { -1L, 0L };
		final long pinMask = pin[(int) ((br & checkMask) >>> generatorUtil.squaresMap(br & checkMask))];
		long legalMoves = moves & emptyOrEnemy & pinMask & inCheckMask;

		while (legalMoves != 0L) {
			final long move = legalMoves & -legalMoves;
			final Position newPosition = pos.makeClone();
			generatorUtil.makeMove(newPosition, move, pieceType, square, enemies, pos);
			children.add(newPosition);
			legalMoves = legalMoves & ~move;
		}
	}

}
