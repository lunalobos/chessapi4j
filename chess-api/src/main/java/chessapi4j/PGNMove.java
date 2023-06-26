package chessapi4j;

import java.util.List;
/**
 * Represents a move with Portable Game Notation (PGN) properties.
 * @author lunalobos
 */
public class PGNMove implements Move {

	private int origin;
	private int destiny;
	private int coronationPiece;
	private List<Integer> suffixAnnotations;
	private List<PGNMove> rav;
	private String comment;
	
	/**
	 * Constructs a PGNMove object with the specified origin, destiny, and coronation piece.
	 *
	 * @param origin           the origin square of the move
	 * @param destiny          the destiny square of the move
	 * @param coronationPiece  the piece type for coronation (if applicable)
	 */
	public PGNMove(int origin, int destiny, int coronationPiece) {
		this.origin = origin;
		this.destiny = destiny;
		this.coronationPiece = coronationPiece;
	}
	
	/**
	 * Constructs a PGNMove object based on a Move object.
	 *
	 * @param move  the Move object to construct from
	 */
	public PGNMove(Move move) {
		this(move.getOrigin(), move.getDestiny(), move.getCoronationPiece());
	}

	/**
	 * Returns the suffix annotations associated with the move.
	 * 
	 * <p>"Import format PGN allows for the use of traditional suffix annotations for
	 * moves. There are exactly six such annotations available: "!", "?", "!!",
	 * "!?", "?!", and "??". At most one such suffix annotation may appear per move,
	 * and if present, it is always the last part of the move symbol.
	 * 
	 * <p> When exported, a move suffix annotation is translated into the corresponding
	 * Numeric Annotation Glyph as described in a later section of this document.
	 * For example, if the single move symbol "Qxa8?" appears in an import format
	 * PGN movetext, it would be replaced with the two adjacent symbols "Qxa8 $2"."
	 * 
	 * <p> https://www.thechessdrum.net/PGN_Reference.txt
	 *
	 * @return the suffix annotations
	 */
	public List<Integer> getSuffixAnnotations() {
		return suffixAnnotations;
	}
	
	/**
	 * Sets the suffix annotations for the move.
	 *
	 * @param suffixAnnotations  the suffix annotations to set
	 */
	public void setSuffixAnnotations(List<Integer> suffixAnnotations) {
		this.suffixAnnotations = suffixAnnotations;
	}
	
	/**
	 * Returns the Recursive Annotation Variation (RAV) associated with the move.
	 * 
	 * <p>"An RAV (Recursive Annotation Variation) is a sequence of movetext containing
	 * one or more moves enclosed in parentheses. An RAV is used to represent an
	 * alternative variation. The alternate move sequence given by an RAV is one
	 * that may be legally played by first unplaying the move that appears
	 * immediately prior to the RAV. Because the RAV is a recursive construct, it
	 * may be nested."
	 * 
	 * <p> https://www.thechessdrum.net/PGN_Reference.txt
	 * 
	 * @return the RAV of moves
	 */
	public List<PGNMove> getRav() {
		return rav;
	}
	
	/**
	 * Sets the Recursive Annotation Variation (RAV) for the move.
	 *
	 * @param rav  the RAV of moves to set
	 */
	public void setRav(List<PGNMove> rav) {
		this.rav = rav;
	}
	
	/**
	 * Returns the comment associated with the move.
	 * 
	 * <p>"Comment text may appear in PGN data. There are two kinds of comments. The
	 * first kind is the "rest of line" comment; this comment type starts with a
	 * semicolon character and continues to the end of the line. The second kind
	 * starts with a left brace character and continues to the next right brace
	 * character. Comments cannot appear inside any token.
	 * 
	 * <p>Brace comments do not nest; a left brace character appearing in a brace
	 * comment loses its special meaning and is ignored. A semicolon appearing
	 * inside of a brace comment loses its special meaning and is ignored. Braces
	 * appearing inside of a semicolon comments lose their special meaning and are
	 * ignored."
	 * 
	 * <p>https://www.thechessdrum.net/PGN_Reference.txt
	 * 
	 * @return the comment text
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * Sets the comment for the move.
	 *
	 * @param comment  the comment text to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int getOrigin() {
		return origin;
	}

	@Override
	public int getDestiny() {
		return destiny;
	}

	@Override
	public int getCoronationPiece() {
		return coronationPiece;
	}

	@Override
	public void setOrigin(int origin) {
		this.origin = origin;
	}

	@Override
	public void setDestiny(int destiny) {
		this.destiny = destiny;
	}

	@Override
	public void setCoronationPiece(int coronationPiece) {
		this.coronationPiece = coronationPiece;
	}

	@Override
	public String toString() {
		return stringRepresentation();
	}
}
