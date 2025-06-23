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

import chessapi4j.Piece;

/**
 * @author lunalobos
 * @since 1.2.9
 */
final class CheckmateMetrics {
    private static final Logger logger = Factory.getLogger(CheckmateMetrics.class);
    private final VisibleMetrics visibleMetrics;
    private final InternalUtil internalUtil;

    public CheckmateMetrics(VisibleMetrics visibleMetrics, InternalUtil internalUtil) {
        this.visibleMetrics = visibleMetrics;
        this.internalUtil = internalUtil;
        logger.instantiation();
    }

    public boolean isCheckmate(long[] bitboards, boolean isWhiteMove, long wk, long wq, long bk, long bq,
                               int enPassant, long legalMoves) {
        var friendsAndEnemies = internalUtil.friendsAndEnemies(bitboards, isWhiteMove);
        var friends = friendsAndEnemies[0];
        var enemies = friendsAndEnemies[1];
        var kingSquare = Long.numberOfTrailingZeros(
                bitboards[isWhiteMove ? Piece.WK.ordinal() - 1 : Piece.BK.ordinal() - 1]);
        var enemiesVisible = visibleMetrics.immediateThreats(bitboards, friends, enemies);
        var kingVisible = visibleMetrics.visibleSquaresKing(kingSquare, friends) & (1L << kingSquare);
        var kingInCheckAndHasNoMoves = (kingVisible & enemiesVisible) == kingVisible;
        return kingInCheckAndHasNoMoves && legalMoves == 0L;
    }
}
