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
package chessapi4j.functional;
/**
 * @author lunalobos
 * @since 1.2.9
 */
final class QueenGenerator {
    private static final Logger logger = Factory.getLogger(QueenGenerator.class);

    private final VisibleMetrics visibleMetrics;
    private final InternalUtil internalUtil;
    private final MoveFactory moveFactory;
    public QueenGenerator(VisibleMetrics visibleMetrics, InternalUtil internalUtil, MoveFactory moveFactory){
        this.visibleMetrics = visibleMetrics;
        this.internalUtil = internalUtil;
        this.moveFactory = moveFactory;
        logger.instantiation();
    }

    public RegularPieceMoves queenMoves(long br, int square, int pieceType, int kingSquare, long friends, long enemies,
                                                   long checkMask, long inCheckMask) {
        final long defense = internalUtil.defenseDirection(kingSquare, square);
        final long pseudoLegalMoves = visibleMetrics.visibleSquaresQueen(square, friends, enemies);
        final long[] pin = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
        final long pinMask = pin[(int) ((br & checkMask) >>> Long.numberOfTrailingZeros(br & checkMask))];
        return new RegularPieceMoves(pieceType, square, enemies, pseudoLegalMoves & pinMask & inCheckMask, moveFactory);
    }
}
