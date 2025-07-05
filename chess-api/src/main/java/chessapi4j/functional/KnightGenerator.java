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
final class KnightGenerator {
    private static final Logger logger = Factory.getLogger(KnightGenerator.class);
    private final MatrixUtil matrixUtil;
    private final MoveFactory moveFactory;
    public KnightGenerator(MatrixUtil matrixUtil, MoveFactory moveFactory) {
        this.matrixUtil = matrixUtil;
        this.moveFactory = moveFactory;
        logger.instantiation();
    }

    public RegularPieceMoves knightMoves(long br, int square, int pieceType, long enemies, long friends, long checkMask,
                                                    long inCheckMask) {
        final long emptyOrEnemy = ~friends;
        long moves = matrixUtil.knightMoves[square];
        final long[] pin = new long[] { -1L, 0L };
        final long pinMask = pin[(int) ((br & checkMask) >>> Long.numberOfTrailingZeros(br & checkMask))];
        return new RegularPieceMoves(pieceType, square, enemies,moves & emptyOrEnemy & pinMask & inCheckMask, moveFactory);
    }
}
