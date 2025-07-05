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
import java.util.List;

/**
 * @author lunalobos
 * @since 1.2.9
 */
final class KingGenerator {
    private static final Logger logger = Factory.getLogger(KingGenerator.class);
    private final VisibleMetrics visibleMetrics;
    private final MatrixUtil matrixUtil;
    private final MoveFactory moveFactory;

    public KingGenerator(VisibleMetrics visibleMetrics, MatrixUtil matrixUtil, MoveFactory moveFactory) {
        this.visibleMetrics = visibleMetrics;
        this.matrixUtil = matrixUtil;
        this.moveFactory = moveFactory;
        logger.instantiation();
    }

    public KingMoves kingMoves(int square, int pieceType, long enemies, long friends, long inCheck, long[] bitboards,
                                          long wm, long wk, long wq, long bk, long bq) {
        final var emptyOrEnemy = ~friends;
        var moves = matrixUtil.kingMoves[square];
        var threats = visibleMetrics.threats(bitboards, friends & ~(1L << square), enemies, square, wm);
        var regularMoves = moves & emptyOrEnemy & ~threats;
        if(inCheck == 1L){
            return new KingMoves(pieceType, square, enemies, regularMoves, 0L, moveFactory);
        }
        var castleMoves = 0L;
        castleMoves = castleMoves | (isShortCastleWhiteEnable(square, enemies, friends, wk, inCheck, threats) << 6);
        castleMoves = castleMoves | (isLongCastleWhiteEnable(square, enemies, friends, wq, inCheck, threats) << 2);
        castleMoves = castleMoves | (isShortCastleBlackEnable(square, enemies, friends, bk, inCheck, threats) << 62);
        castleMoves = castleMoves | (isLongCastleBlackEnable(square, enemies, friends, bq, inCheck, threats) << 58);
        return new KingMoves(pieceType, square, enemies, regularMoves, castleMoves, moveFactory);
    }


    void generateCastlePositions(List<Move> moves, int kingPiece, int square, Position position,
                                 List<Tuple<Position,Move>> children) {
        moves.forEach(m -> {
            final long move = m.getMove();
            // bitboards
            var bitboards = position.bitboards();
            for (int index : matrixUtil.indexes) {
                bitboards[index] = bitboards[index] & (~move);
            }
            bitboards[kingPiece - 1] = (bitboards[kingPiece - 1] & (~(1L << square))) | move;
            long rookMove = 0L;
            rookMove = rookMove | (((1L << 6) & (move)) >> 1);
            rookMove = rookMove | (((1L << 2) & (move)) << 1);
            rookMove = rookMove | (((1L << 62) & (move)) >> 1);
            rookMove = rookMove | (((1L << 58) & (move)) << 1);
            long rookOrigin = 0L;
            rookOrigin = rookOrigin | (((1L << 6) & (move)) << 1);
            rookOrigin = rookOrigin | (((1L << 2) & (move)) >> 2);
            rookOrigin = rookOrigin | (((1L << 62) & (move)) << 1);
            rookOrigin = rookOrigin | (((1L << 58) & (move)) >> 2);
            int rookType = kingPiece - 2;
            for (int i = 0; i < bitboards.length; i++) {
                bitboards[i] = bitboards[i] & (~rookMove);
            }
            bitboards[rookType - 1] = (bitboards[rookType - 1] & (~rookOrigin)) | rookMove;
            // color
            var wm = ~position.wm() & 1L;
            // castle
            var castleInfo = new CastleInfo(position.wk(), position.wq(), position.bk(), position.bq(), matrixUtil)
                    .applyCastleRules(bitboards, wm);
            var wk = castleInfo.getWk();
            var wq = castleInfo.getWq();
            var bk = castleInfo.getBk();
            var bq = castleInfo.getBq();
            // half moves counter
            var hm = position.halfMovesCounter() + 1;
            // moves counter
            var mc = position.movesCounter() + (int) (1L & wm);
            // en passant
            var ep = -1;
            // add new immutable instance
            var newPosition = new Position(bitboards, wm, wk, wq, bk, bq, ep, mc, hm);
            children.add(new Tuple<>(newPosition, m));
        });
    }

    long isShortCastleWhiteEnable(int kingSquare, long enemies, long friends, long wk, long inCheck, long threats) {
        final long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
        final long piecesInterruption1 = ((1L << 5) & (enemies | friends)) >>> 5;
        final long piecesInterruption2 = ((1L << 6) & (enemies | friends)) >>> 6;
        final long check1 = (threats & (1L << 5)) >>> 5;
        final long check2 = (threats & (1L << 6)) >>> 6;
        return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & wk & ~check1 & ~check2 & ~inCheck;
    }

    long isShortCastleBlackEnable(int kingSquare, long enemies, long friends, long bk, long inCheck, long threats) {
        final long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
        final long piecesInterruption1 = ((1L << 61) & (enemies | friends)) >>> 61;
        final long piecesInterruption2 = ((1L << 62) & (enemies | friends)) >>> 62;
        final long check1 = (threats & (1L << 61)) >>> 61;
        final long check2 = (threats & (1L << 62)) >>> 62;
        return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & bk & ~check1 & ~check2 & ~inCheck;
    }

    long isLongCastleWhiteEnable(int kingSquare, long enemies, long friends, long wq,  long inCheck, long threats) {
        final long kingLocation = ((1L << kingSquare) & (1L << 4)) >>> 4;
        final long piecesInterruption1 = ((1L << 2) & (enemies | friends)) >>> 2;
        final long piecesInterruption2 = ((1L << 3) & (enemies | friends)) >>> 3;
        final long piecesInterruption3 = ((1L << 1) & (enemies | friends)) >>> 1;
        final long check1 = (threats & (1L << 3)) >>> 3;
        final long check2 = (threats & (1L << 2)) >>> 2;
        return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & wq
                & ~check1 & ~check2 & ~inCheck;
    }

    long isLongCastleBlackEnable(int kingSquare, long enemies, long friends, long bq, long inCheck, long threats) {
        final long kingLocation = ((1L << kingSquare) & (1L << 60)) >>> 60;
        final long piecesInterruption1 = ((1L << 58) & (enemies | friends)) >>> 58;
        final long piecesInterruption2 = ((1L << 59) & (enemies | friends)) >>> 59;
        final long piecesInterruption3 = ((1L << 57) & (enemies | friends)) >>> 57;
        final long check1 = (threats & (1L << 58)) >>> 58;
        final long check2 = (threats & (1L << 59)) >>> 59;
        return kingLocation & ~piecesInterruption1 & ~piecesInterruption2 & ~piecesInterruption3 & bq
                & ~check1 & ~check2 & ~inCheck;
    }

}
