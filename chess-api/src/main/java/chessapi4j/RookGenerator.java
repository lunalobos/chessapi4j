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
final class RookGenerator {
    private static final Logger logger = LoggerFactory.getLogger(RookGenerator.class);
    private VisibleMetrics visibleMetrics;
    private GeneratorUtil generatorUtil;

    public RookGenerator(VisibleMetrics visibleMetrics, GeneratorUtil generatorUtil) {
        this.visibleMetrics = visibleMetrics;
        this.generatorUtil = generatorUtil;
        logger.instanciation();
    }

    public void rookMoves(long br, int square, int pieceType, int[] pawnsDirections, int kingSquare, long enemies,
            long friends, Position position, long checkMask, long inCheckMask, List<Position> children) {
        final long defense = generatorUtil.defenseDirection(kingSquare, square);
        final long pseudoLegalMoves = visibleMetrics.visibleSquaresRook(square, friends, enemies);
        final long[] pin = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
        final long pinMask = pin[(int) ((br & checkMask) >>> generatorUtil.squaresMap(br))];
        long legalMoves = pseudoLegalMoves & pinMask & inCheckMask;

        while (legalMoves != 0L) {
            long move = legalMoves & -legalMoves;
            Position newPosition = position.makeClone();
            generatorUtil.makeMove(newPosition, move, pieceType, square, enemies, position);
            newPosition.changeColorToMove();
            generatorUtil.applyCastleRules(newPosition);
            newPosition.changeColorToMove();
            children.add(newPosition);
            legalMoves = legalMoves & ~move;
        }

    }

}
