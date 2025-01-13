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

/**
 * Move representation. To string method returns a long algebraic move notation
 * representation modified for the UCI protocol.
 *
 * @author lunalobos
 *
 */
public class Move {

	private long move;
	private int origin, promotionPiece;

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
	 */
	public int getOrigin() {
		return origin;
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
	 * Bitboard representing the move. It's equivalent to {@code 1L << targetSquare}
	 * 
	 * @return the bitboard move representation
	 */
	public long getMove() {
		return move;
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
	 * Promotion piece or -1 if there is no promotion.
	 * 
	 * @return the promotion piece or -1 if there is no promotion
	 */
	public int getPromotionPiece() {
		return promotionPiece;
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
	 * @param origin
	 */
	public void setOrigin(int origin) {
		this.origin = origin;
	}

	/**
	 * Origin square setter.
	 * 
	 * @param origin
	 *
	 * @since 1.2.3
	 */
	public void setOrigin(Square origin) {
		this.origin = origin.ordinal();
	}

	/**
	 * Target square setter.
	 * 
	 * @param target
	 */
	public void setTarget(int target) {
		move = 1L << target;
	}

	/**
	 * Target square setter.
	 * 
	 * @param target
	 *
	 * @since 1.2.3
	 */
	public void setTarget(Square target) {
		move = 1L << target.ordinal();
	}

	/**
	 * Promotion piece setter.
	 * 
	 * @param promotionPiece
	 */
	public void setPromotionPiece(int promotionPiece) {
		this.promotionPiece = promotionPiece;
	}

	/**
	 * Promotion piece setter.
	 * 
	 * @param promotionPiece
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
		int y = Util.getRow(getOrigin()) + 1;
		int y_ = Util.getRow(getTarget()) + 1;
		String collum = Util.getColLetter(getOrigin());
		String collum_ = Util.getColLetter(getTarget());
		if (getPromotionPiece() < 0)
			return collum + y + collum_ + y_;
		else {
			String regex = "(?<color>[BW])(?<piece>[NBRQ])";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(Piece.values()[getPromotionPiece()].toString());
			boolean finded = matcher.find();
			if (!finded)
				throw new IllegalArgumentException("Invalid move.");
			String promotion = matcher.group("piece").toLowerCase();
			return collum + y + collum_ + y_ + promotion;// UCI notation;
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
}
