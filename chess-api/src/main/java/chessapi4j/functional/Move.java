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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Move representation. This class is immutable so it's thread-safe and functional friendly.
 * @author lunalobos
 * @since 1.2.9
 */
@Getter
public class Move {
    /**
     * -- GETTER --
     *  Bitboard representing the move. It's equivalent to
     *
     * @return the bitboard move representation
     */
    private final long move;
    /**
     * -- GETTER --
     *  Origin square
     *
     * @return the origin square
     */
    private final int origin;
    /**
     * -- GETTER --
     *  Promotion piece or -1 if there is no promotion.
     *
     * @return the promotion piece or -1 if there is no promotion
     */
    private final int promotionPiece;

    /**
     * Creates a new move instance
     *
     * @param move           the bitboard target representation as a long
     * @param origin         the origin square as an int
     * @param promotionPiece the promotion piece as an int
     */
    public Move(long move, int origin, int promotionPiece) {
        this.move = move;
        this.origin = origin;
        this.promotionPiece = promotionPiece;
    }

    /**
     * Creates a new move instance
     *
     * @param origin the origin square
     * @param target the target square
     *
     * @since 1.2.7
     */
    public Move(Square origin, Square target) {
        this(1L << target.ordinal(), origin.ordinal(), -1);
    }

    /**
     * Creates a new move instance
     *
     * @param origin the origin square
     * @param target the target square
     * @param promotionPiece the promotion piece
     *
     * @since 1.2.7
     */
    public Move(Square origin, Square target, Piece promotionPiece) {
        this(1L << target.ordinal(), origin.ordinal(), promotionPiece.ordinal());
    }

    /**
     * Creates a new move instance
     *
     * @param origin the origin square
     * @param target the target square
     *
     * @since 1.2.7
     */
    public Move(int origin, int target) {
        this.move = 1L << target;
        this.origin = origin;
        this.promotionPiece = -1;
    }

    /**
     * Origin square
     *
     * @return the origin square
     *
     * @since 1.2.3
     */
    public Square origin() {
        return Square.get(origin);
    }

    /**
     * Wrapped bitboard representing the move.
     *
     * @return the bitboard move representation
     *
     * @since 1.2.3
     */
    public Bitboard bitboardMove() {
        return new Bitboard(move);
    }

    /**
     * Target square
     *
     * @return the target square
     */
    public int getTarget() {
        return Long.numberOfTrailingZeros(move);
    }

    /**
     * Target square
     *
     * @return the target square
     *
     * @since 1.2.3
     */
    public Square target() {
        return Square.get(Long.numberOfTrailingZeros(move));
    }

    /**
     * Promotion piece or {@code Piece.EMPTY} if there is no promotion.
     *
     * @return the promotion piece or {@code Piece.EMPTY} if there is no promotion
     *
     * @since 1.2.3
     */
    public Piece promotionPiece() {
        if (promotionPiece < 0)
            return Piece.EMPTY;
        else {
            return Piece.values()[promotionPiece];
        }
    }

    @Override
    public String toString() {
        if (Long.bitCount(move) != 1)
            return "0000";
        var sb = new StringBuilder();
        sb.append(origin().getName()).append(target().getName());
        if (getPromotionPiece() < 0)
            return sb.toString();
        else {
            String regex = "(?<color>[BW])(?<piece>[NBRQ])";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(Piece.values()[getPromotionPiece()].toString());
            boolean found = matcher.find();
            if (!found)
                throw new IllegalArgumentException("Invalid move.");
            String promotion = matcher.group("piece").toLowerCase();
            return sb.append(promotion).toString();// UCI notation;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(move, origin, promotionPiece);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof chessapi4j.Move))
            return false;
        chessapi4j.Move other = (chessapi4j.Move) obj;
        return getOrigin() == other.getOrigin() && getTarget() == other.getTarget()
                && getPromotionPiece() == other.getPromotionPiece();
    }
}
