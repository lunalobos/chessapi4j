package chessapi4j.functional;
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
import chessapi4j.Piece;
import chessapi4j.Util;

import java.util.Arrays;

/**
 * @author lunalobos
 * @since 1.2.9
 */
final class MatrixUtil {
    private static final Logger logger = Factory.getLogger(MatrixUtil.class);
    public MatrixUtil(){
        logger.instantiation();
    }

    public final int[][] blackPawnMatrix1 = new int[][] { {}, {}, {}, {}, {}, {}, {}, {}, { 0 }, { 1 },
            { 2 }, { 3 }, { 4 }, { 5 }, { 6 }, { 7 }, { 8 }, { 9 }, { 10 }, { 11 }, { 12 }, { 13 }, { 14 }, { 15 },
            { 16 }, { 17 }, { 18 }, { 19 }, { 20 }, { 21 }, { 22 }, { 23 }, { 24 }, { 25 }, { 26 }, { 27 }, { 28 },
            { 29 }, { 30 }, { 31 }, { 32 }, { 33 }, { 34 }, { 35 }, { 36 }, { 37 }, { 38 }, { 39 }, { 40, 32 },
            { 41, 33 }, { 42, 34 }, { 43, 35 }, { 44, 36 }, { 45, 37 }, { 46, 38 }, { 47, 39 }, { 48 }, { 49 }, { 50 },
            { 51 }, { 52 }, { 53 }, { 54 }, { 55 } };
    public final int[][] blackPawnMatrix2 = new int[][] { {}, {}, {}, {}, {}, {}, {}, {}, { 1 }, { 0, 2 },
            { 1, 3 }, { 2, 4 }, { 3, 5 }, { 4, 6 }, { 5, 7 }, { 6 }, { 9 }, { 8, 10 }, { 9, 11 }, { 10, 12 },
            { 11, 13 }, { 12, 14 }, { 13, 15 }, { 14 }, { 17 }, { 16, 18 }, { 17, 19 }, { 18, 20 }, { 19, 21 },
            { 20, 22 }, { 21, 23 }, { 22 }, { 25 }, { 24, 26 }, { 25, 27 }, { 26, 28 }, { 27, 29 }, { 28, 30 },
            { 29, 31 }, { 30 }, { 33 }, { 32, 34 }, { 33, 35 }, { 34, 36 }, { 35, 37 }, { 36, 38 }, { 37, 39 }, { 38 },
            { 41 }, { 40, 42 }, { 41, 43 }, { 42, 44 }, { 43, 45 }, { 44, 46 }, { 45, 47 }, { 46 }, { 49 }, { 48, 50 },
            { 49, 51 }, { 50, 52 }, { 51, 53 }, { 52, 54 }, { 53, 55 }, { 54 } };
    public final long[] blackPawnCaptureMoves = Arrays.stream(blackPawnMatrix2)
                .mapToLong(e -> {
                    var moves = 0L;
                    for(var sq : e){
                        moves |= (1L <<sq);
                    }
                    return moves;
                }).toArray();
    public final int[][] kingMatrix = new int[][] { { 9, 8, 1 }, { 10, 8, 9, 2, 0 }, { 11, 9, 10, 3, 1 },
            { 12, 10, 11, 4, 2 }, { 13, 11, 12, 5, 3 }, { 14, 12, 13, 6, 4 }, { 15, 13, 14, 7, 5 }, { 14, 15, 6 },
            { 17, 1, 16, 0, 9 }, { 18, 16, 0, 2, 17, 1, 10, 8 }, { 19, 17, 1, 3, 18, 2, 11, 9 },
            { 20, 18, 2, 4, 19, 3, 12, 10 }, { 21, 19, 3, 5, 20, 4, 13, 11 }, { 22, 20, 4, 6, 21, 5, 14, 12 },
            { 23, 21, 5, 7, 22, 6, 15, 13 }, { 22, 6, 23, 7, 14 }, { 25, 9, 24, 8, 17 },
            { 26, 24, 8, 10, 25, 9, 18, 16 }, { 27, 25, 9, 11, 26, 10, 19, 17 }, { 28, 26, 10, 12, 27, 11, 20, 18 },
            { 29, 27, 11, 13, 28, 12, 21, 19 }, { 30, 28, 12, 14, 29, 13, 22, 20 }, { 31, 29, 13, 15, 30, 14, 23, 21 },
            { 30, 14, 31, 15, 22 }, { 33, 17, 32, 16, 25 }, { 34, 32, 16, 18, 33, 17, 26, 24 },
            { 35, 33, 17, 19, 34, 18, 27, 25 }, { 36, 34, 18, 20, 35, 19, 28, 26 }, { 37, 35, 19, 21, 36, 20, 29, 27 },
            { 38, 36, 20, 22, 37, 21, 30, 28 }, { 39, 37, 21, 23, 38, 22, 31, 29 }, { 38, 22, 39, 23, 30 },
            { 41, 25, 40, 24, 33 }, { 42, 40, 24, 26, 41, 25, 34, 32 }, { 43, 41, 25, 27, 42, 26, 35, 33 },
            { 44, 42, 26, 28, 43, 27, 36, 34 }, { 45, 43, 27, 29, 44, 28, 37, 35 }, { 46, 44, 28, 30, 45, 29, 38, 36 },
            { 47, 45, 29, 31, 46, 30, 39, 37 }, { 46, 30, 47, 31, 38 }, { 49, 33, 48, 32, 41 },
            { 50, 48, 32, 34, 49, 33, 42, 40 }, { 51, 49, 33, 35, 50, 34, 43, 41 }, { 52, 50, 34, 36, 51, 35, 44, 42 },
            { 53, 51, 35, 37, 52, 36, 45, 43 }, { 54, 52, 36, 38, 53, 37, 46, 44 }, { 55, 53, 37, 39, 54, 38, 47, 45 },
            { 54, 38, 55, 39, 46 }, { 57, 41, 56, 40, 49 }, { 58, 56, 40, 42, 57, 41, 50, 48 },
            { 59, 57, 41, 43, 58, 42, 51, 49 }, { 60, 58, 42, 44, 59, 43, 52, 50 }, { 61, 59, 43, 45, 60, 44, 53, 51 },
            { 62, 60, 44, 46, 61, 45, 54, 52 }, { 63, 61, 45, 47, 62, 46, 55, 53 }, { 62, 46, 63, 47, 54 },
            { 49, 48, 57 }, { 48, 50, 49, 58, 56 }, { 49, 51, 50, 59, 57 }, { 50, 52, 51, 60, 58 },
            { 51, 53, 52, 61, 59 }, { 52, 54, 53, 62, 60 }, { 53, 55, 54, 63, 61 }, { 54, 55, 62 } };
    public final long[] kingMoves = Arrays.stream(kingMatrix)
            .mapToLong(squares ->
                Arrays.stream(squares).mapToLong(sq -> 1L << sq).reduce(0L, (a,b) -> a | b))
            .toArray();
    public final int[] kings = new int[] { Piece.BK.ordinal(), Piece.WK.ordinal() };
    public final int[][] knightMatrix = new int[][] { { 17, 10 }, { 18, 16, 11 }, { 19, 17, 12, 8 },
            { 20, 18, 13, 9 }, { 21, 19, 14, 10 }, { 22, 20, 15, 11 }, { 23, 21, 12 }, { 22, 13 }, { 25, 18, 2 },
            { 26, 24, 19, 3 }, { 27, 25, 20, 16, 4, 0 }, { 28, 26, 21, 17, 5, 1 }, { 29, 27, 22, 18, 6, 2 },
            { 30, 28, 23, 19, 7, 3 }, { 31, 29, 20, 4 }, { 30, 21, 5 }, { 33, 26, 1, 10 }, { 34, 32, 27, 2, 0, 11 },
            { 35, 33, 28, 24, 3, 1, 12, 8 }, { 36, 34, 29, 25, 4, 2, 13, 9 }, { 37, 35, 30, 26, 5, 3, 14, 10 },
            { 38, 36, 31, 27, 6, 4, 15, 11 }, { 39, 37, 28, 7, 5, 12 }, { 38, 29, 6, 13 }, { 41, 34, 9, 18 },
            { 42, 40, 35, 10, 8, 19 }, { 43, 41, 36, 32, 11, 9, 20, 16 }, { 44, 42, 37, 33, 12, 10, 21, 17 },
            { 45, 43, 38, 34, 13, 11, 22, 18 }, { 46, 44, 39, 35, 14, 12, 23, 19 }, { 47, 45, 36, 15, 13, 20 },
            { 46, 37, 14, 21 }, { 49, 42, 17, 26 }, { 50, 48, 43, 18, 16, 27 }, { 51, 49, 44, 40, 19, 17, 28, 24 },
            { 52, 50, 45, 41, 20, 18, 29, 25 }, { 53, 51, 46, 42, 21, 19, 30, 26 }, { 54, 52, 47, 43, 22, 20, 31, 27 },
            { 55, 53, 44, 23, 21, 28 }, { 54, 45, 22, 29 }, { 57, 50, 25, 34 }, { 58, 56, 51, 26, 24, 35 },
            { 59, 57, 52, 48, 27, 25, 36, 32 }, { 60, 58, 53, 49, 28, 26, 37, 33 }, { 61, 59, 54, 50, 29, 27, 38, 34 },
            { 62, 60, 55, 51, 30, 28, 39, 35 }, { 63, 61, 52, 31, 29, 36 }, { 62, 53, 30, 37 }, { 58, 33, 42 },
            { 59, 34, 32, 43 }, { 60, 56, 35, 33, 44, 40 }, { 61, 57, 36, 34, 45, 41 }, { 62, 58, 37, 35, 46, 42 },
            { 63, 59, 38, 36, 47, 43 }, { 60, 39, 37, 44 }, { 61, 38, 45 }, { 41, 50 }, { 42, 40, 51 },
            { 43, 41, 52, 48 }, { 44, 42, 53, 49 }, { 45, 43, 54, 50 }, { 46, 44, 55, 51 }, { 47, 45, 52 },
            { 46, 53 } };
    public final long[] knightMoves = Arrays.stream(knightMatrix)
                .mapToLong(e -> {
                    var moves = 0L;
                    for (int sq : e) {
                        moves |= (1L << sq);
                    }
                    return moves;
                }).toArray();
    public final int[] knights = new int[] { Piece.BN.ordinal(), Piece.WN.ordinal() };
    public final int[][] whitePawnMatrix1 = new int[][] { { 8 }, { 9 }, { 10 }, { 11 }, { 12 }, { 13 },
            { 14 }, { 15 }, { 16, 24 }, { 17, 25 }, { 18, 26 }, { 19, 27 }, { 20, 28 }, { 21, 29 }, { 22, 30 },
            { 23, 31 }, { 24 }, { 25 }, { 26 }, { 27 }, { 28 }, { 29 }, { 30 }, { 31 }, { 32 }, { 33 }, { 34 }, { 35 },
            { 36 }, { 37 }, { 38 }, { 39 }, { 40 }, { 41 }, { 42 }, { 43 }, { 44 }, { 45 }, { 46 }, { 47 }, { 48 },
            { 49 }, { 50 }, { 51 }, { 52 }, { 53 }, { 54 }, { 55 }, { 56 }, { 57 }, { 58 }, { 59 }, { 60 }, { 61 },
            { 62 }, { 63 }, {}, {}, {}, {}, {}, {}, {}, {} };

    public final int[][] whitePawnMatrix2 = new int[][] { { 9 }, { 8, 10 }, { 9, 11 }, { 10, 12 },
            { 11, 13 }, { 12, 14 }, { 13, 15 }, { 14 }, { 17 }, { 16, 18 }, { 17, 19 }, { 18, 20 }, { 19, 21 },
            { 20, 22 }, { 21, 23 }, { 22 }, { 25 }, { 24, 26 }, { 25, 27 }, { 26, 28 }, { 27, 29 }, { 28, 30 },
            { 29, 31 }, { 30 }, { 33 }, { 32, 34 }, { 33, 35 }, { 34, 36 }, { 35, 37 }, { 36, 38 }, { 37, 39 }, { 38 },
            { 41 }, { 40, 42 }, { 41, 43 }, { 42, 44 }, { 43, 45 }, { 44, 46 }, { 45, 47 }, { 46 }, { 49 }, { 48, 50 },
            { 49, 51 }, { 50, 52 }, { 51, 53 }, { 52, 54 }, { 53, 55 }, { 54 }, { 57 }, { 56, 58 }, { 57, 59 },
            { 58, 60 }, { 59, 61 }, { 60, 62 }, { 61, 63 }, { 62 }, {}, {}, {}, {}, {}, {}, {}, {} };
    public final long[] whitePawnCaptureMoves = Arrays.stream(whitePawnMatrix2)
                .mapToLong(e ->{
                    var moves = 0L;
                    for(var sq : e){
                        moves |= (1L << sq);
                    }
                    return moves;
                }).toArray();
    public final int[][][] pawnMatrix1 = new int[][][] { blackPawnMatrix1, whitePawnMatrix1 };
    public final int[][][] pawnMatrix2 = new int[][][] { blackPawnMatrix2, whitePawnMatrix2 };
    public final int[] pawns = new int[] { Piece.BP.ordinal(), Piece.WP.ordinal() };
    public final int[] queenDirections = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
    public final int[][][] queenMegamatrix = Util.QUEEN_MEGAMATRIX;
    public final int[] queens = new int[] { Piece.BQ.ordinal(), Piece.WQ.ordinal() };
    public final int[] rookDirections = new int[] { 4, 5, 6, 7 };
    public final int[] rooks = new int[] { Piece.BR.ordinal(), Piece.WR.ordinal() };
    public final int[] bishopDirections = new int[] { 0, 1, 2, 3 };
    public final int[] bishops = new int[] { Piece.BB.ordinal(), Piece.WB.ordinal() };

    // short castle black bitboard masks, element 0 for king mask, element 1 for
    // rook mask
    public final long[] scbMask = new long[] { 1L << 60, 1L << 63 };

    // short castle black squares, element 0 for king square, element 1 for rook
    // square
    public final int[] scbSquares = new int[] { 60, 63 };

    // short castle white bitboard masks, element 0 for king mask, element 1 for
    // rook mask
    public final long[] scwMask = new long[] { 1L << 4, 1 << 7 };

    // short castle white squares, element 0 for king square, element 1 for rook
    // square
    public final int[] scwSquares = new int[] { 4, 7 };

    // long castle black bitboard masks, element 0 for king mask, element 1 for rook
    // mask
    public final long[] lcbMask = new long[] { 1L << 60, 1L << 56 };

    // long castle black squares, element 0 for king square, element 1 for rook
    // square
    public final int[] lcbSquares = new int[] { 60, 56 };

    // long castle white bitboard masks, element 0 for king mask, element 1 for rook
    // mask
    public final long[] lcwMask = new long[] { 1L << 4, 1 << 0 };

    // long castle white squares, element 0 for king square, element 1 for rook
    // square
    public final int[] lcwSquares = new int[] { 4, 0 };

    public final long[][][] castleMask = new long[][][] { {scbMask, lcbMask}, {scwMask, lcwMask} };

    public final int[][][] castleSquares = new int[][][] { {scbSquares, lcbSquares},
            {scwSquares, lcwSquares} };

    public final int[] kingPieces = new int[] { Piece.BK.ordinal(), Piece.WK.ordinal() };
    public final int[] rookPieces = new int[] { Piece.BR.ordinal(), Piece.WR.ordinal() };

    public final int[] indexes = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
}
