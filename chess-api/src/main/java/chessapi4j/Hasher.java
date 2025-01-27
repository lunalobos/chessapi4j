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

//bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
class Hasher {
    private static final Logger logger = LoggerFactory.getLogger(Hasher.class);
    private int indexBits;
    private long[] maskMatrix;
  

    public Hasher(int indexBits, int[][][] queenMatrix, int[] directions) {
        this.indexBits = indexBits;
        maskMatrix = createMaskMatrix(queenMatrix, directions);
        logger.instanciation();
    }

    //public int hash(long ocuppied, long magic, int square, int[] directionsIndexs, int[][] directions) {
        //var mask = visibleMetricsUtil.computeVisible(square, directionsIndexs, directions, 0L, 0L);
        //var blockers = ocuppied & mask;
        //return (int) ((blockers * magic) >>> (64 - indexBits));
        //return magicHash(ocuppied, magic, square);
    //}

    private long[] createMaskMatrix(int[][][] queenMatrix, int[] directions) {
        var maskMatrix = new long[64];
        for (int square = 0; square < 64; square++) {
            var mask = 0L;
            for (int directionIndex : directions) {

                for (int squareIndex : queenMatrix[square][directionIndex]) {
                    mask |= 1L << squareIndex;
                }

            }
            maskMatrix[square] = mask;
        }
        return maskMatrix;
    }

    public int hash(long ocuppied, long magic, int square){
        var mask = maskMatrix[square];
        var blockers = ocuppied & mask;
        return (int) ((blockers * magic) >>> (64 - indexBits));
    }
}
