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
final class BishopGenerator {
	private static final Logger logger = LoggerFactory.getLogger(BishopGenerator.class);
    private VisibleMetrics visibleMetrics;
    private GeneratorUtil generatorUtil;

    public BishopGenerator(VisibleMetrics visibleMetrics, GeneratorUtil generatorUtil) {
        this.visibleMetrics = visibleMetrics;
        this.generatorUtil = generatorUtil;
		logger.instanciation();
    }

    public void bishopMoves(long br, int square, int pieceType, int kingSquare, long enemies, long friends,
			Position position, long checkMask, long inCheckMask, List<Position> children) {

		final long pseudoLegalMoves = visibleMetrics.visibleSquaresBishop(square, friends, enemies);
		final long[] pin = new long[] { -1L, pseudoLegalMoves & checkMask & generatorUtil.defenseDirection(kingSquare, square) };
		final long isPin = pin[(int) ((br & checkMask) >>> generatorUtil.squaresMap(br & checkMask))];
		long legalMoves = pseudoLegalMoves & isPin & inCheckMask;

		while (legalMoves != 0L) {
			final long move = legalMoves & -legalMoves;
			final Position newPosition = position.makeClone();
			generatorUtil.makeMove(newPosition, move, pieceType, square, enemies, position);
			children.add(newPosition);
			legalMoves = legalMoves & ~move;
		}

	}


}
