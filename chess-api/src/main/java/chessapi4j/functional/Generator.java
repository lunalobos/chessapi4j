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

import java.util.List;
import java.util.function.Function;

/**
 * This class is used to generate all the legal moves for a given position.
 * @author lunalobos
 * @since 1.2.9
 */
final public class Generator {

    private final PawnGenerator pawnGenerator;
    private final KingGenerator kingGenerator;
    private final MatrixUtil matrixUtil;

    Generator(PawnGenerator pawnGenerator, KingGenerator kingGenerator, MatrixUtil matrixUtil){
        this.pawnGenerator = pawnGenerator;
        this.kingGenerator = kingGenerator;
        this.matrixUtil = matrixUtil;
    }

    /**
     * Generate all the legal moves for a given position. The result is a {@code List} of {@code Tuple<Position,Move>}
     * where the first element is the new position and the second element is the move.
     * 
     * @param position the parent position
     * @return a {@code List} with all the legal moves 
     */
    public List<Tuple<Position,Move>> legalMoves(Position position){
        var info = position.movesInfo();
        return generatePositions(info, position);
    }

    private List<Tuple<Position,Move>> generatePositions(final MovesInfo info, final Position position){
        var children = new BlockingList<Tuple<Position,Move>>();
        pawnMoves(info, position, children);
        knightBishopAndQueenMoves(info, position, children);
        rookMoves(info, position, children);
        kingMoves(info, position, children);
        return children.block();
    }

    private void kingMoves(MovesInfo info, Position position, List<Tuple<Position,Move>> children) {
        var kingMoves = info.getKingMoves();

        // regular moves
        generatePositions(
                info.getKingMoves().getRegularMoves(),
                info.getKingMoves().getKingPiece(),
                info.getKingMoves().getOriginSquare(),
                info.getKingMoves().getEnemies(),
                position,
                m -> -1,
                castleInfo -> bitboards -> wm -> castleInfo.applyCastleRules(bitboards,wm),
                children
        );
        // castle moves
        kingGenerator.generateCastlePositions(
                kingMoves.getCastleMoves(),
                kingMoves.getKingPiece(),
                kingMoves.getOriginSquare(),
                position,
                children
        );
    }

    private void rookMoves(MovesInfo info, Position position, List<Tuple<Position,Move>> children) {
        info.getRookMoves().forEach(rpm -> {
            var piece = rpm.getPiece();
            var square = rpm.getSquare();
            // moves
            generatePositions(rpm.getMoves(), piece, square, rpm.getEnemies(), position,
                    (m) -> -1, castleInfo -> bitboards -> wm -> castleInfo
                            .applyCastleRules(bitboards,wm).applyCastleRules(bitboards, ~wm & 1L), children);
        });
    }

    private void knightBishopAndQueenMoves(MovesInfo info, Position position,
                                                                   List<Tuple<Position,Move>> children) {

        info.getKnightMoves().forEach(rpm -> {
            var piece = rpm.getPiece();
            var square = rpm.getSquare();
            // moves
            generatePositions(rpm.getMoves(), piece, square, rpm.getEnemies(), position,
                    (m) -> -1, castleInfo -> bitboards -> wm -> castleInfo
                            .applyCastleRules(bitboards,wm), children);
        });
        info.getBishopMoves().forEach(rpm -> {
            var piece = rpm.getPiece();
            var square = rpm.getSquare();
            // moves
            generatePositions(rpm.getMoves(), piece, square, rpm.getEnemies(), position,
                    (m) -> -1, castleInfo -> bitboards -> wm -> castleInfo
                            .applyCastleRules(bitboards,wm), children);
        });
        info.getQueenMoves().forEach(rpm -> {
            var piece = rpm.getPiece();
            var square = rpm.getSquare();
            // moves
            generatePositions(rpm.getMoves(), piece, square, rpm.getEnemies(), position,
                    (m) -> -1, castleInfo -> bitboards -> wm -> castleInfo
                            .applyCastleRules(bitboards,wm), children);
        });
    }

    private void pawnMoves(MovesInfo info, Position position, List<Tuple<Position,Move>> children) {
        info.getPawnMoves().forEach(pm -> {
            // regular moves
            generatePositions(pm.getRegularMoves(), pm.getPawnPiece(), pm.getOriginSquare(),
                    pm.getEnemies(), position, (m) -> -1,
                    castleInfo -> bitboards -> wm -> castleInfo
                            .applyCastleRules(bitboards,wm), children);
            // promotions
            pawnGenerator.generatePromotions(pm.getPromotionMoves(), pm.getPawnPiece(),
                    pm.getOriginSquare(), position, children);
            // two squares advance (with en passant moves)
            generatePositions(pm.getAdvanceEpMoves(), pm.getPawnPiece(), pm.getOriginSquare(),
                    pm.getEnemies(), position, Long::numberOfTrailingZeros,
                    castleInfo -> bitboards -> wm -> castleInfo.applyCastleRules(bitboards,wm),
                    children);
            // en passant captures
            pm.getEpCapture().ifPresent(move -> pawnGenerator.generateEnPassantCaptures(move, pm.getPawnPiece(),
                    pm.getOriginSquare(), position, children));
        });
    }

    void generatePositions(List<Move> moves, int pieceType, int square, long enemies, Position position,
                                       Function<Long,Integer> epFunction,
                                       Function<CastleInfo, Function<long[], Function<Long, CastleInfo>>> castleFunction,
                                                   List<Tuple<Position,Move>> children) {
        moves.forEach(m -> {
            final long move = m.getMove();
            // pieces
            var bitboards = position.bitboards();
            for (var index = 0; index < 12; index++) {
                bitboards[index] = bitboards[index] & (~move);
            }
            bitboards[pieceType - 1] = (bitboards[pieceType - 1] & (~(1L << square))) | move;
            // color
            var wm = ~position.wm() & 1L;
            // castle
            var castleInfo = castleFunction
                    .apply(new CastleInfo(position.wk(), position.wq(), position.bk(), position.bq(), matrixUtil))
                    .apply(bitboards).apply(wm);
            var wk = castleInfo.getWk();
            var wq = castleInfo.getWq();
            var bk = castleInfo.getBk();
            var bq = castleInfo.getBq();
            // moves counter
            var mc = position.movesCounter() + (int) (1L & wm);
            // en passant
            var ep = epFunction.apply(move);
            // half moves counter
            final var aux = (int) (6L & (position.wm() << 1 | position.wm() << 2));
            final var enemyOperation = enemies & move;
            final var pawnOperation = move & bitboards[Piece.BP.ordinal() - aux - 1];
            final var hm = new int[] { position.halfMovesCounter() + 1, 0 }
                    [(int) ((pawnOperation >>> Long.numberOfTrailingZeros(pawnOperation)) |
                    (enemyOperation >>> Long.numberOfTrailingZeros(enemyOperation)))];
            // new immutable instance added
            var tuple = new Tuple<>(new Position(bitboards,wm, wk, wq, bk, bq, ep, mc, hm), m);
            children.add(tuple);
        });
    }
}
