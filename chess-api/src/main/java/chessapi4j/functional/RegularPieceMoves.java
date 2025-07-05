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
final class RegularPieceMoves {
    private final int piece;
    private final int square;
    private final long enemies;
    private final long $moves;
    private final List<Move> moves;

    public RegularPieceMoves(int piece, int square, long enemies, long moves, MoveFactory moveFactory) {
        this.piece = piece;
        this.square = square;
        this.enemies = enemies;
        this.$moves = moves;
        this.moves = CollectionUtil.bitboardToList(this.$moves,
                bitboard -> moveFactory.move(square, Long.numberOfTrailingZeros(bitboard)),
                BlockingList::new);
    }

    public RegularPieceMoves(Piece piece, Square square, Bitboard enemies, Bitboard moves, MoveFactory moveFactory) {
        this(piece.ordinal(), square.ordinal(), enemies.getValue(), moves.getValue(), moveFactory);
    }

    public long allMoves() {
        return $moves;
    }
}
