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

import java.util.Random;

//singleton bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
class VisibleMagic {
    private static final Logger logger = LoggerFactory.getLogger(VisibleMagic.class);
    private final VisibleMetricsUtil visibleMetricsUtils;
    private MagicNumbers bishopMagicNumbers;
    private MagicNumbers rookMagicNumbers;
    private final Random random;

    public VisibleMagic(VisibleMetricsUtil visibleMetricsUtils, Random random) {
        this.visibleMetricsUtils = visibleMetricsUtils;
        this.random = random;
        magic();
        logger.instantiation();
        
    }

    private void magic() {
        var rookBits = 20;
        var rookSize = new Size(rookBits);

        var bishopBits = 18;
        var bishopSize = new Size(bishopBits);

        var combinator = new Combinator();

        var rookHasher = new MagicHasher(rookSize.getBits(), Util.QUEEN_MEGAMATRIX, Util.ROOK_DIRECTIONS);
        var bishopHasher = new MagicHasher(bishopSize.getBits(), Util.QUEEN_MEGAMATRIX, Util.BISHOP_DIRECTIONS);

        rookMagicNumbers = new MagicNumbers(combinator, visibleMetricsUtils, rookHasher, rookSize.getCapacity(),
                Util.ROOK_DIRECTIONS, random);

        bishopMagicNumbers = new MagicNumbers(combinator, visibleMetricsUtils, bishopHasher, bishopSize.getCapacity(),
                Util.BISHOP_DIRECTIONS, random);

        bishopMagicNumbers.calculate();
        logger.debug("Bishop magic numbers calculated");
        rookMagicNumbers.calculate();
        logger.debug("Rook magic numbers calculated");
    }

    public long visibleBishop(int square, long friends, long enemies) {
        var occupied = friends | enemies;
        var hashed = bishopMagicNumbers.visibleHashed(square, occupied);
        return hashed & ~friends;
    }

    public long visibleRook(int square, long friends, long enemies) {
        var occupied = friends | enemies;
        var hashed = rookMagicNumbers.visibleHashed(square, occupied);
        return hashed & ~friends;
    }
}
