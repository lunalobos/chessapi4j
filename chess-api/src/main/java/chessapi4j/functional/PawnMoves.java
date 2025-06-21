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

import chessapi4j.Bitboard;
import chessapi4j.Piece;
import chessapi4j.Square;
import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author lunalobos
 * @since 1.2.9
 */
@Data
final class PawnMoves {
    private int pawnPiece;
    private int originSquare;
    private long enemies;
    private long $regularMoves;
    private List<Move> regularMoves;
    private long $advanceEpMoves;
    private List<Move> advanceEpMoves;
    private long $promotionMoves;
    private List<Move> promotionMoves;
    private long $epCapture;
    private Move $epCaptureMove;

    public PawnMoves(int pawnPiece, int originSquare, long enemies, long regularMoves, long advanceEpMoves,
                     long promotionMoves, long epCapture){
        this.pawnPiece = pawnPiece;
        this.originSquare = originSquare;
        this.enemies = enemies;
        this.$regularMoves = regularMoves;
        this.regularMoves = CollectionUtil.bitboardToList(this.$regularMoves,
                move -> new Move(move, this.originSquare, -1),
                BlockingList::new);
        this.$advanceEpMoves = advanceEpMoves;
        this.advanceEpMoves = CollectionUtil.bitboardToList(this.$advanceEpMoves,
                move -> new Move(move, this.originSquare, -1),
                        BlockingList::new);
        this.$promotionMoves = promotionMoves;
        this.promotionMoves = CollectionUtil.bitboardToCollectedList(this.$promotionMoves,
                bitboard -> {
                    Move queen;
                    Move rook;
                    Move bishop;
                    Move knight;
                    if(pawnPiece == Piece.WP.ordinal()){
                        queen = new Move(bitboard, this.originSquare, Piece.WQ.ordinal());
                        rook = new Move(bitboard, this.originSquare, Piece.WR.ordinal());
                        bishop = new Move(bitboard, this.originSquare, Piece.WB.ordinal());
                        knight = new Move(bitboard, this.originSquare, Piece.WN.ordinal());
                    } else {
                        queen = new Move(bitboard, this.originSquare, Piece.BQ.ordinal());
                        rook = new Move(bitboard, this.originSquare, Piece.BR.ordinal());
                        bishop = new Move(bitboard, this.originSquare, Piece.BB.ordinal());
                        knight = new Move(bitboard, this.originSquare, Piece.BN.ordinal());
                    }
                    return List.of(queen, rook, bishop, knight);
                }, BlockingList::new);
        this.$epCapture = epCapture;
        this.$epCaptureMove = epCapture != 0L ?
                new Move(epCapture, this.originSquare, -1) :
                null;
    }

    public PawnMoves(Piece piece, Square square, Bitboard enemies, Bitboard regularMoves, Bitboard advanceEpMoves,
                     Bitboard promotionMoves, Bitboard epCapture){
        this(piece.ordinal(), square.ordinal(), enemies.getValue(), regularMoves.getValue(), advanceEpMoves.getValue(),
                promotionMoves.getValue(), epCapture.getValue());
    }

    public Optional<Move> getEpCapture(){
        return Optional.ofNullable($epCaptureMove);
    }

    public long allMoves(){
        return $regularMoves | $advanceEpMoves | $promotionMoves | $epCapture;
    }
}
