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

import chessapi4j.Util;

import java.util.List;

/**
 * @author lunalobos
 * @since 1.2.9
 */
final class PawnGenerator {
    private static final Logger logger = Factory.getLogger(PawnGenerator.class);
    private static final int[] EP_CHOICE = new int[] { 8, -8 };

    static long isPromotion(int finalSquare) {
        return ((((finalSquare >>> 3) & 7L) >>> 2) & ((((finalSquare >>> 3) & 7L) >>> 1) & 1L)
                & (((finalSquare >>> 3) & 7L) & 1L))
                | (((((63 - finalSquare) >>> 3) & 7L) >>> 2) & (((((63 - finalSquare) >>> 3) & 7L) >>> 1) & 1L)
                & ((((63 - finalSquare) >>> 3) & 7L) & 1L));
    }

    private static int transformEnPassant(int enPassant, long whiteMoveNumeric) {
        return 8 * (-2 * (int) whiteMoveNumeric + 1) + enPassant;
    }

    private final VisibleMetrics visibleMetrics;
    private final CheckMetrics checkMetrics;
    private final MatrixUtil matrixUtil;
    private final InternalUtil internalUtil;

    public PawnGenerator(VisibleMetrics visibleMetrics, CheckMetrics checkMetrics,
                         MatrixUtil matrixUtil, InternalUtil internalUtil) {
        this.visibleMetrics = visibleMetrics;
        this.checkMetrics = checkMetrics;
        this.matrixUtil = matrixUtil;
        this.internalUtil = internalUtil;
        logger.instantiation();
    }

    public PawnMoves pawnMoves(long br, int square, int[] pawnsDirections, int pieceType, int[][] matrix1,
                               int[][] matrix2, int kingSquare, long enemies, long friends, int ep, long wm,
                               long[] bitboards, long checkMask, long inCheckMask, long nextWhiteMove) {
        final int[] captureArray = matrix2[square];
        long captureMoves = 0L;
        long captureCoronationMoves = 0L;
        for (int squareToCapture : captureArray) {
            captureCoronationMoves = captureCoronationMoves | ((1L & isPromotion(squareToCapture)) << squareToCapture);
            captureMoves = captureMoves | ((1L & ~isPromotion(squareToCapture)) << squareToCapture);
        }
        final int normalizedEnPassant = transformEnPassant(ep, nextWhiteMove);
        final long possibleEnPassant = (1L << normalizedEnPassant) & captureMoves;
        captureMoves = captureMoves & enemies;
        captureCoronationMoves = captureCoronationMoves & enemies;
        final int[] advanceMatrix = matrix1[square];
        long advanceMoves = 0L;
        long advancePromotionMoves = 0L;
        long advanceEnPassantMoves = 0L;
        for (int squareToOccupy : advanceMatrix) {
            advancePromotionMoves = advancePromotionMoves | ((1L & isPromotion(squareToOccupy)) << squareToOccupy);
            advanceEnPassantMoves = advanceEnPassantMoves
                    | ((1L & isEnPassant(square, squareToOccupy, wm)) << squareToOccupy);
            advanceMoves = advanceMoves | ((1L & ~isPromotion(squareToOccupy)
                    & ~isEnPassant(square, squareToOccupy, wm)) << squareToOccupy);
        }
        final long visible = visibleMetrics.visibleSquaresRook(square, friends, enemies);
        advanceMoves = advanceMoves & ~(friends | enemies | ~visible);
        advancePromotionMoves = advancePromotionMoves & ~(friends | enemies | ~visible);
        advanceEnPassantMoves = advanceEnPassantMoves & ~(friends | enemies | ~visible);
        final long pseudoPromotionMoves = advancePromotionMoves | captureCoronationMoves;
        final long pseudoLegalMoves = advanceMoves | captureMoves;
        final long operation = (br & checkMask) >>> Long.numberOfTrailingZeros(br);
        final long defense = internalUtil.defenseDirection(kingSquare, square);
        final long[] pin1 = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
        final long pinMask1 = pin1[(int) operation];
        final long[] pin2 = new long[] { -1L, pseudoPromotionMoves & checkMask & defense };
        final long pinMask2 = pin2[(int) operation];
        final long[] pin3 = new long[] { -1L, advanceEnPassantMoves & checkMask & defense };
        final long pinMask3 = pin3[(int) operation];
        final long legalMoves = pseudoLegalMoves & pinMask1 & inCheckMask;
        final long legalPromotionMoves = pseudoPromotionMoves & pinMask2 & inCheckMask;
        final long legalAdvanceEnPassantMoves = advanceEnPassantMoves & pinMask3 & inCheckMask;
        return new PawnMoves(
                pieceType,
                square,
                enemies,
                legalMoves,
                legalAdvanceEnPassantMoves,
                legalPromotionMoves,
                generateEnPassantCaptureBitboard(
                        possibleEnPassant,
                        pieceType,
                        square,
                        bitboards,
                        wm));
    }

    private long isEnPassant(int originSquare, int finalSquare, long whiteMoveNumeric) {
        final long difference = finalSquare - originSquare;
        final long[] choice = new long[] { ~difference + 1, difference };
        final long maskedDifference = 16L & choice[(int) whiteMoveNumeric];
        return maskedDifference >>> 4;
    }

    public void generatePromotions(List<Move> moves, int pieceType, int square, Position position,
                                   List<Tuple<Position,Move>> children) {

        moves.forEach(m -> {
            final var move = m.getMove();
            var promotionPiece = m.getPromotionPiece();

            // bitboards
            var bitboards = position.bitboards();
            for (var index = 0; index < 12; index++) {
                bitboards[index] = bitboards[index] & (~move);
            }
            bitboards[pieceType - 1] = (bitboards[pieceType - 1] & (~(1L << square)));
            bitboards[promotionPiece - 1] = bitboards[promotionPiece - 1] | move;
            // color
            var wm = ~position.wm() & 1L;
            // castle
            final int whiteMove = (int) wm;

            final long[] scMask = matrixUtil.castleMask[whiteMove][0];
            final long[] lcMask = matrixUtil.castleMask[whiteMove][1];
            final int[] scSquares = matrixUtil.castleSquares[whiteMove][0];
            final int[] lcSquares = matrixUtil.castleSquares[whiteMove][1];

            final long rookBits = bitboards[matrixUtil.rookPieces[whiteMove] - 1];
            final long kingBits = bitboards[matrixUtil.kingPieces[whiteMove] - 1];

            final long scBitsMasked = ((rookBits & scMask[1]) >>> scSquares[1]) & ((kingBits & scMask[0]) >>> scSquares[0]);
            final long lcBitsMasked = ((rookBits & lcMask[1]) >>> lcSquares[1]) & ((kingBits & lcMask[0]) >>> lcSquares[0]);

            var wk = new long[] { position.wk(), scBitsMasked & position.wk() }[whiteMove];
            var wq = new long[] { position.wq(), lcBitsMasked & position.wq() }[whiteMove];
            var bk = new long[] { position.bk() & scBitsMasked, position.bk() }[whiteMove];
            var bq = new long[] { position.bq() & lcBitsMasked, position.bq() }[whiteMove];
            // half moves counter
            var hm = 0;
            // moves counter
            var mc = (position.movesCounter() + (int) (1L & wm));
            // en passant
            var ep = -1;
            // new immutable instance added
            var newPosition = new Position(bitboards, wm, wk, wq, bk, bq, ep, mc,hm);
            children.add(new Tuple<>(newPosition, m));
        });
    }

    void generateEnPassantCaptures(Move m, int pieceType, int originSquare, Position position,
                                   List<Tuple<Position,Move>> children){
        final var move = m.getMove();
        final long capture = 1L << (Long.numberOfTrailingZeros(move) + EP_CHOICE[(int) position.wm()]);
        // bitboards
        var bitboards = position.bitboards();
        for (var index = 0; index < 12; index++) {
            bitboards[index] = bitboards[index] & (~capture);
        }
        bitboards[pieceType - 1] = (bitboards[pieceType - 1] & (~(1L << originSquare))) | move;
        // color
        var wm  = ~position.wm() & 1L;
        // half moves counter
        var hm = 0;
        // moves counter
        var mc = (position.movesCounter() + (int) (1L & wm));
        // en passant
        var ep = -1;
        // add new immutable instance
        var newPosition = new Position(bitboards, wm, position.wk(), position.wq(), position.bk(), position.bq(),
                ep, mc, hm);
        children.add(new Tuple<>(newPosition, m));
    }

    private long generateEnPassantCaptureBitboard(long move, int pieceType, int originSquare, long[] bitboards,
                                                  long wm) {
        final long capture = 1L << (Long.numberOfTrailingZeros(move) + EP_CHOICE[(int) wm]);
        var newBitboards = Util.copyBitboards(bitboards);
        for (var index = 0; index < 12; index++) {
            newBitboards[index] = newBitboards[index] & (~capture);
        }
        newBitboards[pieceType - 1] = (newBitboards[pieceType - 1] & ~(1L << originSquare)) | move;
        return move * (~checkMetrics.inCheck(newBitboards, wm) & 1L);
    }

}
