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
final class BitboardGenerator {
     final static class CheckInfo {
        long inCheck;
        long inCheckMask;
        int checkCount;
        public CheckInfo(long inCheck, long inCheckMask, int checkCount) {
            super();
            this.inCheck = inCheck;
            this.inCheckMask = inCheckMask;
            this.checkCount = checkCount;
        }
    }
    private final PawnGenerator pawnGenerator;
    private final KnightGenerator knightGenerator;
    private final BishopGenerator bishopGenerator;
    private final RookGenerator rookGenerator;
    private final QueenGenerator queenGenerator;
    private final KingGenerator kingGenerator;
    private final VisibleMetrics visibleMetrics;
    private final InternalUtil internalUtil;
    private final MatrixUtil matrixUtil;

    public BitboardGenerator(PawnGenerator pawnGenerator, KnightGenerator knightGenerator,
                             BishopGenerator bishopGenerator, RookGenerator rookGenerator,
                             QueenGenerator queenGenerator, KingGenerator kingGenerator,
                             VisibleMetrics visibleMetrics, InternalUtil internalUtil,
                             MatrixUtil matrixUtil) {
        this.pawnGenerator = pawnGenerator;
        this.knightGenerator = knightGenerator;
        this.bishopGenerator = bishopGenerator;
        this.rookGenerator = rookGenerator;
        this.queenGenerator = queenGenerator;
        this.kingGenerator = kingGenerator;
        this.visibleMetrics = visibleMetrics;
        this.internalUtil = internalUtil;
        this.matrixUtil = matrixUtil;
    }

    CheckInfo isInCheckWithMask(int kingPiece, long[] bits, long whiteMoveNumeric, int[] pawnsDirections) {
        long inCheckMask = 0L;
        int checkCount = 0;
        final int kingSquare = Long.numberOfTrailingZeros(bits[kingPiece - 1]);
        long isInCheck = 0L;
        final int aux = (int) (6L & (whiteMoveNumeric << 1 | whiteMoveNumeric << 2));
        final int[] enemies = { Piece.WP.ordinal() + aux, Piece.WN.ordinal() + aux, Piece.WB.ordinal() + aux,
                Piece.WR.ordinal() + aux, Piece.WQ.ordinal() + aux, Piece.WK.ordinal() + aux };
        // pawns directions

        for (int pawnDirection : pawnsDirections) {

            final long enemyPawnDangerLocation = 1L << pawnDirection;
            final long operation = ((bits[enemies[0] - 1] & enemyPawnDangerLocation) >>> pawnDirection);
            isInCheck = isInCheck | operation;

            inCheckMask = inCheckMask | new long[] { 0L, enemyPawnDangerLocation }[(int) operation];
            checkCount += (int) operation;
        }
        // kings directions (only used in test cases)

        long kingDirectionsBits = 0L;

        for (int square : matrixUtil.kingMatrix[kingSquare]) {
            kingDirectionsBits = kingDirectionsBits | (1L << square);
        }
        final long operation = kingDirectionsBits & bits[enemies[5] - 1];
        final long operation3 = (operation >>> Long.numberOfTrailingZeros(operation));
        isInCheck = isInCheck | operation3;
        checkCount += (int) operation3;
        // knight directions

        long knightDirectionsBits;
        for (int square : matrixUtil.knightMatrix[kingSquare]) {
            knightDirectionsBits = 1L << square;

            final long operation2 = ((knightDirectionsBits & bits[enemies[1] - 1]) >>> square);
            isInCheck = isInCheck | operation2;
            inCheckMask = inCheckMask | new long[] { 0L, knightDirectionsBits }[(int) operation2];
            checkCount += (int) operation2;
        }
        // bishops directions
        final long enemyBishopsAndQueens = bits[enemies[2] - 1] | bits[enemies[4] - 1];

        for (int i = 0; i < 4; i++) {
            final long visible = visibleMetrics.visibleSquares(bits, new int[] { i },
                    kingSquare, whiteMoveNumeric);

            final long isPresent = internalUtil.hasBitsPresent(enemyBishopsAndQueens & visible);
            isInCheck = isInCheck | isPresent;
            inCheckMask = inCheckMask | new long[] { 0L, visible }[(int) isPresent];
            checkCount += (int) isPresent;
        }

        // rooks directions
        final long enemyRooksAndQueens = bits[enemies[3] - 1] | bits[enemies[4] - 1];
        for (int i = 4; i < 8; i++) {
            final long visible = visibleMetrics.visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);

            final long isPresent = internalUtil.hasBitsPresent(enemyRooksAndQueens & visible);
            isInCheck = isInCheck | isPresent;
            inCheckMask = inCheckMask | new long[] { 0L, visible }[(int) isPresent];
            checkCount += (int) isPresent;
        }
        return new CheckInfo(isInCheck, inCheckMask, checkCount);
    }

    long createCheckMask(int kingSquare, long enemies, long friends, long wm, long nextWhiteMove,
                         long[] bitboards) {
        final long empty = ~(enemies | friends);
        final int[] enemyRookChoice = new int[] { Piece.WR.ordinal(), Piece.BR.ordinal() };
        final int[] enemyQueenChoice = new int[] { Piece.WQ.ordinal(), Piece.BQ.ordinal() };
        final int[] enemyBishopChoice = new int[] { Piece.WB.ordinal(), Piece.BB.ordinal() };
        final int enemyRook = enemyRookChoice[(int) wm];
        final int enemyQueen = enemyQueenChoice[(int) wm];
        final int enemyBishop = enemyBishopChoice[(int) wm];
        long checkMask = 0L;
        for (int j = 0; j < 4; j++) {
            final long visibleEmptyOrFriendsRD = visibleMetrics.visibleSquares(bitboards,
                    new int[] { matrixUtil.rookDirections[j] },
                    kingSquare, nextWhiteMove);
            final long friendsRD = visibleEmptyOrFriendsRD & ~empty;
            final long[] testBitsRD = new long[12];
            System.arraycopy(bitboards, 0, testBitsRD, 0, 12);
            for (int i = 0; i < 12; i++) {
                testBitsRD[i] = testBitsRD[i] & ~friendsRD;
            }
            final long visibleEmptyOrEnemyRD = visibleMetrics.visibleSquares(testBitsRD,
                    new int[] { matrixUtil.rookDirections[j] }, kingSquare, wm);
            final long enemiesThreadsRD = visibleEmptyOrEnemyRD
                    & (bitboards[enemyRook - 1] | bitboards[enemyQueen - 1]);
            final long[] choice = new long[] { 0L, friendsRD | visibleEmptyOrEnemyRD };
            checkMask = checkMask | choice[(int) internalUtil.hasBitsPresent(enemiesThreadsRD)];
        }
        for (int j = 0; j < 4; j++) {
            final long visibleEmptyOrFriendsBD = visibleMetrics.visibleSquares(bitboards,
                    new int[] { matrixUtil.bishopDirections[j] },
                    kingSquare, nextWhiteMove);
            final long friendsBD = visibleEmptyOrFriendsBD & ~empty;
            final long[] testBitsBD = new long[12];
            System.arraycopy(bitboards, 0, testBitsBD, 0, 12);
            for (int i = 0; i < 12; i++) {
                testBitsBD[i] = testBitsBD[i] & ~friendsBD;
            }
            final long visibleEmptyOrEnemyBD = visibleMetrics.visibleSquares(testBitsBD,
                    new int[] { matrixUtil.bishopDirections[j] },
                    kingSquare, wm);
            final long enemiesThreadsBD = visibleEmptyOrEnemyBD
                    & (bitboards[enemyBishop - 1] | bitboards[enemyQueen - 1]);
            final long[] choice = new long[] { 0L, friendsBD | visibleEmptyOrEnemyBD };
            checkMask = checkMask | choice[(int) internalUtil.hasBitsPresent(enemiesThreadsBD)];
        }
        return checkMask;
    }

    MovesInfo fillChildrenList(long[] bitboards, long friends, long enemies, long wm, long wk, long wq, long bk,
                               long bq, int enPassant, long checkMask, long inCheckMask, long nextWhiteMove,
                               long inCheck, int pawnPiece, int kingSquare, int knightPiece, int bishopPiece,
                               int rookPiece, int queenPiece, int kingPiece, int[] pawnsDirections, int[][] matrix1,
                               int[][] matrix2) {
        var moveInfo = new MovesInfo();
        // Pawn Moves
        var pawnMoves = CollectionUtil.bitboardToList(bitboards[pawnPiece - 1],bitboard ->
                pawnGenerator.pawnMoves(bitboard, Long.numberOfTrailingZeros(bitboard), pawnsDirections, pawnPiece,
                matrix1, matrix2, kingSquare, enemies, friends, enPassant, wm, bitboards, checkMask, inCheckMask,
                nextWhiteMove), BlockingList::new);
        moveInfo.addPawnMoves(((BlockingList<PawnMoves>)pawnMoves).block());
        // Knight Moves
        var knightMoves = CollectionUtil.bitboardToList(bitboards[knightPiece - 1], bitboard ->
             knightGenerator.knightMoves(bitboard, Long.numberOfTrailingZeros(bitboard), knightPiece, enemies,
                    friends, checkMask, inCheckMask), BlockingList::new);
        moveInfo.addKnightMoves(((BlockingList<RegularPieceMoves>)knightMoves).block());
        // Bishop Moves
        var bishopMoves = CollectionUtil.bitboardToList(bitboards[bishopPiece - 1], bitboard ->
             bishopGenerator.bishopMoves(bitboard, Long.numberOfTrailingZeros(bitboard), bishopPiece, kingSquare, enemies,
                    friends, checkMask, inCheckMask), BlockingList::new);
        moveInfo.addBishopMoves(((BlockingList<RegularPieceMoves>)bishopMoves).block());
        // Rook Moves
        var rookMoves = CollectionUtil.bitboardToList(bitboards[rookPiece - 1], bitboard ->
            rookGenerator.rookMoves(bitboard, Long.numberOfTrailingZeros(bitboard), rookPiece, kingSquare, enemies,
                    friends, checkMask, inCheckMask), BlockingList::new);
        moveInfo.addRookMoves(((BlockingList<RegularPieceMoves>)rookMoves).block());
        // Queen Moves
        var queenMoves = CollectionUtil.bitboardToList(bitboards[queenPiece - 1], bitboard ->
            queenGenerator.queenMoves(bitboard, Long.numberOfTrailingZeros(bitboard), queenPiece, kingSquare,
                    friends, enemies, checkMask, inCheckMask), BlockingList::new);
        moveInfo.addQueenMoves(((BlockingList<RegularPieceMoves>)queenMoves).block());
        // King Moves
        var kingMoves = kingGenerator.kingMoves(Long.numberOfTrailingZeros(bitboards[kingPiece - 1]), kingPiece,
                enemies, friends, inCheck, bitboards, wm, wk, wq, bk, bq);
        moveInfo.addKingMoves(kingMoves);
        return moveInfo;
    }

    public MovesInfo generateMoveInfo(long[] bitboards, long wm, long wk, long wq, long bk, long bq, int enPassant) {
        final int aux = (int) (6L & (wm << 1 | wm << 2));
        final long friends = bitboards[Piece.BP.ordinal() - aux - 1] | bitboards[Piece.BN.ordinal() - aux - 1]
                | bitboards[Piece.BB.ordinal() - aux - 1] | bitboards[Piece.BR.ordinal() - aux - 1]
                | bitboards[Piece.BQ.ordinal() - aux - 1] | bitboards[Piece.BK.ordinal() - aux - 1];
        final long enemies = bitboards[Piece.WP.ordinal() + aux - 1] | bitboards[Piece.WN.ordinal() + aux - 1]
                | bitboards[Piece.WB.ordinal() + aux - 1] | bitboards[Piece.WR.ordinal() + aux - 1]
                | bitboards[Piece.WQ.ordinal() + aux - 1] | bitboards[Piece.WK.ordinal() + aux - 1];
        final long nextWhiteMove = (~wm) & 1L;
        final int whiteMove = (int) wm;
        final int kingPiece = matrixUtil.kings[whiteMove];
        final int kingSquare = Long.numberOfTrailingZeros(bitboards[kingPiece - 1]);
        final int[] pawnsDirections = new int[][] { matrixUtil.blackPawnMatrix2[kingSquare],
                matrixUtil.whitePawnMatrix2[kingSquare] }[(int) wm];
        final CheckInfo info = isInCheckWithMask(kingPiece, bitboards, wm, pawnsDirections);
        long inCheckMask = info.inCheckMask;
        final long checkMask = createCheckMask(kingSquare, enemies, friends, wm, nextWhiteMove, bitboards);
        final long[] choice = new long[] { -1L, inCheckMask, 0L, 0L, 0L, 0L };
        inCheckMask = choice[info.checkCount];
        return fillChildrenList(bitboards, friends, enemies, wm, wk, wq, bk, bq, enPassant, checkMask, inCheckMask,
                nextWhiteMove, info.inCheck, matrixUtil.pawns[whiteMove], kingSquare, matrixUtil.knights[whiteMove],
                matrixUtil.bishops[whiteMove], matrixUtil.rooks[whiteMove], matrixUtil.queens[whiteMove], kingPiece,
                pawnsDirections, matrixUtil.pawnMatrix1[whiteMove], matrixUtil.pawnMatrix2[whiteMove]);
    }
}
