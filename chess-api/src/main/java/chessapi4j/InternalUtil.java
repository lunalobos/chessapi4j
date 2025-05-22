package chessapi4j;

class InternalUtil {
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

    public final String toFen(int[] squares, boolean isWhiteMove, boolean shortCastleWhite, boolean shortCastleBlack,
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
                        rowFenSB.append("");
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
            enPassant = "" + Util.getColLetter(Util.getCol(enPassantSquare))
                    + (Util.getRow(enPassantSquare) == 3 ? 3 : 6);
        String halfMoveClock = "" + halfMovesCounter;

        String fullMoveCounter = "" + movesCounter;

        fenSB.append(" ").append(sideToMove).append(" ").append(castleAbility).append(" ").append(enPassant).append(" ")
                .append(halfMoveClock).append(" ").append(fullMoveCounter);

        return fenSB.toString();
    }
}
