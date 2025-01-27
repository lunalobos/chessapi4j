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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

//singleton bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
class MagicNumbers {
    private static final Logger logger = LoggerFactory.getLogger(MagicNumbers.class);
    private Combinator combinator;
    private VisibleMetricsUtil visibleMetricsUtil;
    private Hasher hasher;
    private long[] magicNumbersArray;
    private Long[][] perfectHashMaps;
    private FastFailLongMap map;
    private int[] directionIndexes;
    private Random random;

    public MagicNumbers(Combinator combinator, VisibleMetricsUtil visibleMetricsUtil, Hasher hasher, int capacity,
            int[] directionIndexes, Random random) {
        this.combinator = combinator;
        this.visibleMetricsUtil = visibleMetricsUtil;
        this.hasher = hasher;
        perfectHashMaps = new Long[64][];
        map = new FastFailLongMap(capacity);
        this.directionIndexes = directionIndexes;
        this.random = random;
        logger.instanciation();
    }

    protected final void calculate() {
        final Map<Integer, Long> magicNumbers = new HashMap<>();
        IntStream.range(0, 64).mapToObj(i -> i)
                .forEach(square -> {
                    boolean searching = true;
                    var combinations = combinator.compute(square, directionIndexes);
                    while (searching) { // don't worry about this loop, it will allways converge =)
                        var magicNumber = BigInteger.probablePrime(63, random).longValue();
                        if (magicNumber < 0) {
                            magicNumber = -magicNumber;
                        }
                        map.clear();
                        var isMagic = true;
                        for (long combination : combinations) {
                            var directions = Util.QUEEN_MEGAMATRIX[square];
                            var visible = visibleMetricsUtil.computeVisible(square, directionIndexes, directions, 0L, combination);
                            var index = hasher.hash(combination, magicNumber, square);
                            try {
                                if (map.containsKey(index)) {
                                    if (map.get(index).equals(visible)) {
                                        continue;
                                    } else {
                                        isMagic = false;
                                    }
                                } else {
                                    map.put(index, visible);
                                }
                            } catch (MappingException e) {
                                logger.fatal("MappingException: %s", e.getMessage());
                            }
                        }
                        if (isMagic) {
                            searching = false;
                            magicNumbers.put(square, magicNumber);
                            perfectHashMaps[square] = map.toLongArray();
                            break;
                        }
                    }
                });
        magicNumbersArray = new long[magicNumbers.size()];
        for (var entry : magicNumbers.entrySet()) {
            magicNumbersArray[entry.getKey()] = entry.getValue();
        }

    }

    public long[] getMagicNumbersArray() {
        return magicNumbersArray;
    }

    public Long[][] getPerfectHashMaps() {
        return perfectHashMaps;
    }

    public long visibleHashed(int square, long ocuppied) {
        var magic = magicNumbersArray[square];
        var hash = hasher.hash(ocuppied, magic, square);
        var result = perfectHashMaps[square][hash];
        return result == null ? 0L : result;
    }
}
