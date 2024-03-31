package chessapi4j;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a move with Portable Game Notation (PGN) properties.
 *
 * @author lunalobos
 */
public class PGNMove extends Move {

	private List<Integer> suffixAnnotations;
	private List<PGNMove> rav;
	private String comment;

	/**
	 * Constructs a PGNMove object with the specified origin, target, and promotion
	 * piece.
	 *
	 * @param origin          the origin square of the move
	 * @param destiny         the destiny square of the move
	 * @param coronationPiece the piece type for coronation (if applicable)
	 */
	public PGNMove(int origin, int destiny, int coronationPiece) {
		super(1L << destiny, origin, coronationPiece);
	}

	/**
	 * Constructs a PGNMove object based on a Move object.
	 *
	 * @param move: the Move object to construct from
	 */
	public PGNMove(Move move) {
		this(move.getOrigin(), move.getTarget(), move.getPromotionPiece());
	}

	/**
	 * Returns the suffix annotations associated with the move.
	 *
	 * <p>
	 * "Import format PGN allows for the use of traditional suffix annotations for
	 * moves. There are exactly six such annotations available: "!", "?", "!!",
	 * "!?", "?!", and "??". At most one such suffix annotation may appear per move,
	 * and if present, it is always the last part of the move symbol.
	 *
	 * <p>
	 * When exported, a move suffix annotation is translated into the corresponding
	 * Numeric Annotation Glyph as described in a later section of this document.
	 * For example, if the single move symbol "Qxa8?" appears in an import format
	 * PGN movetext, it would be replaced with the two adjacent symbols "Qxa8 $2"."
	 *
	 * <p>
	 * https://www.thechessdrum.net/PGN_Reference.txt
	 *
	 * @return the suffix annotations
	 */
	public List<Integer> getSuffixAnnotations() {
		return suffixAnnotations;
	}

	/**
	 * Sets the suffix annotations for the move.
	 *
	 * @param suffixAnnotations the suffix annotations to set
	 */
	public void setSuffixAnnotations(List<Integer> suffixAnnotations) {
		this.suffixAnnotations = suffixAnnotations;
	}

	/**
	 * Returns the Recursive Annotation Variation (RAV) associated with the move.
	 *
	 * <p>
	 * "An RAV (Recursive Annotation Variation) is a sequence of movetext containing
	 * one or more moves enclosed in parentheses. An RAV is used to represent an
	 * alternative variation. The alternate move sequence given by an RAV is one
	 * that may be legally played by first unplaying the move that appears
	 * immediately prior to the RAV. Because the RAV is a recursive construct, it
	 * may be nested."
	 *
	 * <p>
	 * https://www.thechessdrum.net/PGN_Reference.txt
	 *
	 * @return the RAV of moves
	 */
	public List<PGNMove> getRav() {
		return rav;
	}

	/**
	 * Sets the Recursive Annotation Variation (RAV) for the move.
	 *
	 * @param rav the RAV of moves to set
	 */
	public void setRav(List<PGNMove> rav) {
		this.rav = rav;
	}

	/**
	 * Returns the comment associated with the move.
	 *
	 * <p>
	 * "Comment text may appear in PGN data. There are two kinds of comments. The
	 * first kind is the "rest of line" comment; this comment type starts with a
	 * semicolon character and continues to the end of the line. The second kind
	 * starts with a left brace character and continues to the next right brace
	 * character. Comments cannot appear inside any token.
	 *
	 * <p>
	 * Brace comments do not nest; a left brace character appearing in a brace
	 * comment loses its special meaning and is ignored. A semicolon appearing
	 * inside of a brace comment loses its special meaning and is ignored. Braces
	 * appearing inside of a semicolon comments lose their special meaning and are
	 * ignored."
	 *
	 * <p>
	 * https://www.thechessdrum.net/PGN_Reference.txt
	 *
	 * @return the comment text
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment for the move.
	 *
	 * @param comment the comment text to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String toString() {
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
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(comment, rav, suffixAnnotations);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (!(obj instanceof PGNMove))
			return false;
		PGNMove other = (PGNMove) obj;
		return getOrigin() == other.getOrigin() && getTarget() == other.getTarget()
				&& getPromotionPiece() == other.getPromotionPiece() && Objects.equals(comment, other.comment)
				&& Objects.equals(rav, other.rav) && Objects.equals(suffixAnnotations, other.suffixAnnotations);
	}

}
