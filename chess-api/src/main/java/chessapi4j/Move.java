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
package chessapi4j;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//bean
/**
 * Move representation. To string method returns a long algebraic move notation
 * representation modified for the UCI protocol.
 *
 * @author lunalobos
 */
public class Move {

    private long move;

    private int origin;

	private int promotionPiece;

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

	/**
	 * Origin square setter.
	 * 
	 * @param origin the origin square
	 */
	public void setOrigin(int origin) {
		this.origin = origin;
	}

	/**
	 * Origin square setter.
	 * 
	 * @param origin the origin square
	 *
	 * @since 1.2.3
	 */
	public void setOrigin(Square origin) {
		this.origin = origin.ordinal();
	}

	/**
	 * Target square setter.
	 * 
	 * @param target the target square
	 */
	public void setTarget(int target) {
		move = 1L << target;
	}

	/**
	 * Target square setter.
	 * 
	 * @param target the target square
	 *
	 * @since 1.2.3
	 */
	public void setTarget(Square target) {
		move = 1L << target.ordinal();
	}

	/**
	 * Promotion piece setter.
	 * 
	 * @param promotionPiece the promotion piece
	 */
	public void setPromotionPiece(int promotionPiece) {
		this.promotionPiece = promotionPiece;
	}

	/**
	 * Promotion piece setter.
	 * 
	 * @param promotionPiece the promotion piece
	 *
	 * @since 1.2.3
	 */
	public void setPromotionPiece(Piece promotionPiece) {
		this.promotionPiece = promotionPiece.ordinal();
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
		if (!(obj instanceof Move))
			return false;
		Move other = (Move) obj;
		return getOrigin() == other.getOrigin() && getTarget() == other.getTarget()
				&& getPromotionPiece() == other.getPromotionPiece();
	}

	/**
	 * The move bitboard as a long
	 * @return the move bitboard as a long
	 */
	public long getMove() {
		return this.move;
	}

	/**
	 * The origin square index
	 * @return the origin square index
	 */
	public int getOrigin() {
		return this.origin;
	}

	/**
	 * The promotion piece index
	 * @return the promotion piece index
	 */
	public int getPromotionPiece() {
		return this.promotionPiece;
	}
}
