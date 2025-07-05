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
import lombok.Getter;

import java.util.List;

/**
 * @author lunalobos
 * @since 1.2.9
 */
@Getter
final class KingMoves {
    private final int kingPiece;
    private final int originSquare;
    private final long enemies;
    private final long $regularMoves;
    private final long $castleMoves;
    private final List<Move> regularMoves;
    private final List<Move> castleMoves;

    public KingMoves(int kingPiece, int originSquare, long enemies, long regularMoves, long castleMoves, MoveFactory moveFactory){
        this.kingPiece = kingPiece;
        this.originSquare = originSquare;
        this.$regularMoves = regularMoves;
        this.$castleMoves = castleMoves;
        this.enemies = enemies;
        this.regularMoves = CollectionUtil.bitboardToList(this.$regularMoves,
                bitboard -> moveFactory.move( this.originSquare, Long.numberOfTrailingZeros(bitboard)),
                BlockingList::new);
        this.castleMoves = CollectionUtil.bitboardToList(this.$castleMoves,
                bitboard -> moveFactory.move(this.originSquare, Long.numberOfTrailingZeros(bitboard)),
                BlockingList::new);
    }

    public KingMoves(Piece piece, Square square, Bitboard enemies, Bitboard regularMoves, Bitboard castleMoves, MoveFactory moveFactory){
        this(piece.ordinal(), square.ordinal(), enemies.getValue(), regularMoves.getValue(), castleMoves.getValue(), moveFactory);
    }

    public long allMoves(){
        return $regularMoves | $castleMoves;
    }
}
