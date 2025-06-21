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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
/**
 * @author lunalobos
 * @since 1.2.9
 */
final class ZobristHasher {

    private long[][] zobristTable;
    private long[] zobristCastle;
    private long[] zobristEnPassant;
    private long zobristTurn;

    public ZobristHasher() {
        var longProvider = new LongProvider();
        zobristTable = new long[12][64];
        zobristCastle = new long[4];
        zobristEnPassant = new long[64];
        for (int piece = 0; piece < 12; piece++) {
            for (int square = 0; square < 64; square++) {
                zobristTable[piece][square] = longProvider.nextLong();
            }
        }
        for (int i = 0; i < 4; i++) {
            zobristCastle[i] = longProvider.nextLong();
        }
        for (int i = 0; i < 64; i++) {
            zobristEnPassant[i] = longProvider.nextLong();
        }
        zobristTurn = longProvider.nextLong();

    }

    public long computeZobristHash(long[] bitBoards, boolean whiteMove, boolean shortCastleWhite, boolean longCastleWhite,
                                   boolean shortCastleBlack, boolean longCastleBlack, int enPassant) {
        var zobristHash = 0L;
        var bits = new long[12];
        System.arraycopy(bitBoards, 0, bits, 0, 12);
        for (int piece = 0; piece < 12; piece++) {
            long bitboard = bits[piece];
            while (bitboard != 0) {
                int square = Long.numberOfTrailingZeros(bitboard);
                zobristHash ^= zobristTable[piece][square];
                bitboard &= bitboard - 1;
            }
        }
        if (whiteMove)
            zobristHash ^= zobristTurn;
        if (shortCastleWhite)
            zobristHash ^= zobristCastle[0];
        if (longCastleWhite)
            zobristHash ^= zobristCastle[1];
        if (shortCastleBlack)
            zobristHash ^= zobristCastle[2];
        if (longCastleBlack)
            zobristHash ^= zobristCastle[3];
        if (enPassant != -1)
            zobristHash ^= zobristEnPassant[enPassant];
        return zobristHash;
    }
}
/**
 * @author lunalobos
 * @since 1.2.9
 */
final class LongProvider {
    private final Long[] backedArray;
    private int pointer;
    //private Random random;

    public LongProvider(){//Random random) {
        pointer = 0;
        try (var is = this.getClass().getClassLoader().getResourceAsStream("zobrist.txt")) {
            backedArray = Arrays.stream(new String(is.readAllBytes(), StandardCharsets.UTF_8).split("\n"))
                    .map(row -> Long.parseUnsignedLong(row.trim(), 16))
                    .collect(Collectors.toCollection(() -> new ArrayList<>(837)))
                    .toArray(new Long[837]);
        } catch (IOException | NumberFormatException e) {
            throw new ResourceAccessException("zobrist.txt", e);
        }

    }

    public long nextLong() {
        if (pointer == backedArray.length) {
            throw new RuntimeException("No more longs");
        }
        return backedArray[pointer++];
    }
}
/**
 * @author lunalobos
 * @since 1.2.9
 */
class ResourceAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceAccessException(String resource, Throwable cause) {
        super(String.format("Can not access resource %s. Error message: %s", resource, cause.getMessage()), cause);
    }
}