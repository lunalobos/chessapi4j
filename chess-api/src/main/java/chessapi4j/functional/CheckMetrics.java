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
final class CheckMetrics {
    private static final Logger logger = Factory.getLogger(CheckMetrics.class);
    private final VisibleMetrics visibleMetrics;
    private final InternalUtil internalUtil;

    public CheckMetrics(VisibleMetrics visibleMetrics, InternalUtil internalUtil) {
        this.visibleMetrics = visibleMetrics;
        this.internalUtil = internalUtil;
        logger.instantiation();
    }

    public long inCheck(long[] bitboards, long wm) {
        var friendsAndEnemies = internalUtil.friendsAndEnemies(bitboards, wm);
        var friends = friendsAndEnemies[0];
        var enemies = friendsAndEnemies[1];
        var kingBitboard = bitboards[wm == 1L ? Piece.WK.ordinal() - 1 : Piece.BK.ordinal() - 1];
        var threats = visibleMetrics.immediateThreats(bitboards, friends, enemies);
        return internalUtil.hasBitsPresent(kingBitboard & threats);
    }
}
