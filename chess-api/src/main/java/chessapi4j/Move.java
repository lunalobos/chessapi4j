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

	public Move(long move, int origin, int coronationPiece) {
		this.move = move;
		this.origin = origin;
		this.promotionPiece = coronationPiece;
	}

	/**
	 * Origin square
	 * @return the origin square
	 */
	public int getOrigin() {
		return origin;
	}

	/**
	 * Bitboard representing the move. It's equivalent to {@code 1L << targetSquare}
	 * @return the bitboard move representation
	 */
	public long getMove() {
		return move;
	}

	/**
	 * Target square
	 * @return the target square
	 */
	public int getTarget() {
		return Long.numberOfTrailingZeros(move);
	}

	/**
	 * Promotion piece or -1 if there is no promotion.
	 * @return the promotion piece or -1 if there is no promotion
	 */
	public int getPromotionPiece() {
		return promotionPiece;
	}

	/**
	 * Origin square setter.
	 * @param origin
	 */
	public void setOrigin(int origin) {
		this.origin = origin;
	}

	/**
	 * Target square setter.
	 * @param target
	 */
	public void setTarget(int target) {
		move = 1L << target;
	}

	/**
	 * Promotion piece setter.
	 * @param promotionPiece
	 */
	public void setPromotionPiece(int promotionPiece) {
		this.promotionPiece = promotionPiece;
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
