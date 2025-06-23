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
import chessapi4j.Util;

import java.util.stream.IntStream;
import java.util.stream.Stream;
/**
 * @author lunalobos
 * @since 1.2.9
 */
final class InternalUtil {
    private static final Logger logger = Factory.getLogger(InternalUtil.class);

    private final MatrixUtil matrixUtil;

    public InternalUtil(MatrixUtil matrixUtil){
        this.matrixUtil = matrixUtil;
        logger.instantiation();
    }

    public Stream<IndexedValue<Long>> arraytoLongStream(long[] array) {
        return IntStream.range(0, array.length).mapToObj(i -> new IndexedValue<>(i, array[i]));
    }

    public long[] friendsAndEnemies(long[] bitboards, boolean isWhiteMove) {
        final var black = bitboards[6] | bitboards[7] | bitboards[8] | bitboards[9] | bitboards[10] | bitboards[11];
        final var white = bitboards[0] | bitboards[1] | bitboards[2] | bitboards[3] | bitboards[4] | bitboards[5];
        long friends;
        long enemies;
        if (isWhiteMove) {
            friends = white;
            enemies = black;
        } else {
            friends = black;
            enemies = white;
        }
        return new long[] { friends, enemies };
    }

    public long[] friendsAndEnemies(long[] bitboards, long wm) {
        final var black = bitboards[6] | bitboards[7] | bitboards[8] | bitboards[9] | bitboards[10] | bitboards[11];
        final var white = bitboards[0] | bitboards[1] | bitboards[2] | bitboards[3] | bitboards[4] | bitboards[5];
        long friends;
        long enemies;
        if (wm == 1L) {
            friends = white;
            enemies = black;
        } else {
            friends = black;
            enemies = white;
        }
        return new long[] { friends, enemies };
    }

    public String toFen(int[] squares, boolean isWhiteMove, boolean shortCastleWhite, boolean shortCastleBlack,
                              boolean longCastleWhite, boolean longCastleBlack, int enPassantSquare, int halfMovesCounter, int movesCounter) {
        StringBuilder fenSB = new StringBuilder();

        for (int i = 7; i >= 0; i--) {
            StringBuilder rowFenSB = new StringBuilder();
            int emptyCounter = 0;
            for (int j = i * 8; j < i * 8 + 8; j++) {
                if (squares[j] == Piece.EMPTY.ordinal())
                    emptyCounter++;
                else if (emptyCounter != 0) {
                    rowFenSB.append((Integer) emptyCounter);
                    emptyCounter = 0;
                }
                if (squares[j] == Piece.EMPTY.ordinal() && j == i * 8 + 7)
                    rowFenSB.append((Integer) emptyCounter);
                switch (Piece.values()[squares[j]]) {
                    case WP:
                        rowFenSB.append("P");
                        break;
                    case WN:
                        rowFenSB.append("N");
                        break;
                    case WB:
                        rowFenSB.append("B");
                        break;
                    case WR:
                        rowFenSB.append("R");
                        break;
                    case WQ:
                        rowFenSB.append("Q");
                        break;
                    case WK:
                        rowFenSB.append("K");
                        break;
                    case BP:
                        rowFenSB.append("p");
                        break;
                    case BN:
                        rowFenSB.append("n");
                        break;
                    case BB:
                        rowFenSB.append("b");
                        break;
                    case BR:
                        rowFenSB.append("r");
                        break;
                    case BQ:
                        rowFenSB.append("q");
                        break;
                    case BK:
                        rowFenSB.append("k");
                        break;
                    default:
                }
            }
            if (i == 7)
                fenSB.append(rowFenSB);
            else
                fenSB.append("/").append(rowFenSB);
        }
        String sideToMove = isWhiteMove ? "w" : "b";
        String castleAbility = "";

        if (shortCastleWhite)
            castleAbility += "K";
        if (longCastleWhite)
            castleAbility += "Q";
        if (shortCastleBlack)
            castleAbility += "k";
        if (longCastleBlack)
            castleAbility += "q";

        if (!shortCastleWhite && !longCastleWhite && !shortCastleBlack && !longCastleBlack)
            castleAbility += "-";
        String enPassant;
        if (enPassantSquare == -1)
            enPassant = "-";
        else
            enPassant = Util.getColLetter(Util.getCol(enPassantSquare))
                    + (Util.getRow(enPassantSquare) == 3 ? 3 : 6);
        String halfMoveClock = "" + halfMovesCounter;

        String fullMoveCounter = "" + movesCounter;

        fenSB.append(" ").append(sideToMove).append(" ").append(castleAbility).append(" ").append(enPassant).append(" ")
                .append(halfMoveClock).append(" ").append(fullMoveCounter);

        return fenSB.toString().trim();
    }

    public String stringRepresentation(int[] squares, String fen){
        StringBuilder sb = new StringBuilder();
        sb.append("\n+---+---+---+---+---+---+---+---+ \n");
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                switch (Piece.values()[squares[i * 8 + j]]) {
                    case WP:
                        sb.append("| P ");
                        break;
                    case WN:
                        sb.append("| N ");
                        break;
                    case WB:
                        sb.append("| B ");
                        break;
                    case WR:
                        sb.append("| R ");
                        break;
                    case WQ:
                        sb.append("| Q ");
                        break;
                    case WK:
                        sb.append("| K ");
                        break;
                    case BP:
                        sb.append("| p ");
                        break;
                    case BN:
                        sb.append("| n ");
                        break;
                    case BB:
                        sb.append("| b ");
                        break;
                    case BR:
                        sb.append("| r ");
                        break;
                    case BQ:
                        sb.append("| q ");
                        break;
                    case BK:
                        sb.append("| k ");
                        break;
                    default:
                        sb.append("|   ");
                }
                if (j == 7) {
                    int row = i + 1;
                    sb.append("| ").append(row).append("\n");
                    sb.append("+---+---+---+---+---+---+---+---+ \n");
                }
            }
        }
        sb.append("  a   b   c   d   e   f   g   h \n");
        sb.append("Fen: ").append(fen);
        return sb.toString();
    }

    long defenseDirection(int kingSquare, int pieceSquare) {
        final int[][] matrix = matrixUtil.queenMegamatrix[kingSquare];
        long result = 0L;
        int d = 0;

        for (int i = 1; i < 9; i++) {
            for (int square : matrix[i - 1]) {
                long operation1 = (1L << pieceSquare) & (1L << square);

                d = d | (new int[] { 0, -1 }[(int) (operation1 >>> Long.numberOfTrailingZeros(operation1))] & i);
            }
        }
        final int[][] matrix2 = new int[][] { {}, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5],
                matrix[6], matrix[7] };
        for (int square : matrix2[d]) {
            result = result | (1L << square);
        }
        return result;
    }

    long hasBitsPresent(long bitboard){
        var signum = Long.signum(bitboard);
        return ((long) signum * signum);
    }
}
