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


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lunalobos
 * @since 1.2.9
 */
@Getter
@AllArgsConstructor
final class CastleInfo{
    private long wk, wq, bk, bq;
    private final MatrixUtil matrixUtil;

    CastleInfo applyCastleRules(long[] bitboards, long wm){
        final var whiteMove = (int) wm;
        final var scMask = matrixUtil.castleMask[whiteMove][0];
        final var lcMask = matrixUtil.castleMask[whiteMove][1];
        final var scSquares = matrixUtil.castleSquares[whiteMove][0];
        final var lcSquares = matrixUtil.castleSquares[whiteMove][1];
        final var rookBits = bitboards[matrixUtil.rookPieces[whiteMove] - 1];
        final var kingBits = bitboards[matrixUtil.kingPieces[whiteMove] - 1];
        final var scBitsMasked = ((rookBits & scMask[1]) >>> scSquares[1]) & ((kingBits & scMask[0]) >>> scSquares[0]);
        final var lcBitsMasked = ((rookBits & lcMask[1]) >>> lcSquares[1]) & ((kingBits & lcMask[0]) >>> lcSquares[0]);
        wk = new long[] { wk, scBitsMasked & wk }[whiteMove];
        wq = new long[] { wq, lcBitsMasked & wq }[whiteMove];
        bk = new long[] { bk & scBitsMasked, bk }[whiteMove];
        bq = new long[] { bq & lcBitsMasked, bq }[whiteMove];

        return this;
    }
}
