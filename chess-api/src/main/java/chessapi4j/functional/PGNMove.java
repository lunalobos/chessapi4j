package chessapi4j.functional;

import java.util.Deque;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
/**
 * Represents a move with Portable Game Notation (PGN) properties. This class extends the
 * Move class and adds PGN specific properties. The position previous to the move is
 * needed to be passed in the constructors. ToString method returns the SAN move
 * notation. 
 * <p>Instances of this class are immutable, therefore thread-safe and functional 
 * friendly.</p>
 *
 * @author lunalobos
 */
@Getter
public class PGNMove extends Move {

    /**
     * -- GETTER --
     *  Returns the suffix annotations associated with the move.
     *  <p>
     *  "Import format PGN allows for the use of traditional suffix annotations for
     *  moves. There are exactly six such annotations available: "!", "?", "!!",
     *  "!?", "?!", and "??". At most one such suffix annotation may appear per move,
     *  and if present, it is always the last part of the move symbol.
     *  </p>
     *  <p>
     *  When exported, a move suffix annotation is translated into the corresponding
     *  Numeric Annotation Glyph as described in a later section of this document.
     *  For example, if the single move symbol "Qxa8?" appears in an import format
     *  PGN movetext, it would be replaced with the two adjacent symbols "Qxa8 $2"."
     *  </p>
     *  <a href="https://www.thechessdrum.net/PGN_Reference.txt">PGN_Reference.txt</a>
     *
     * @return the suffix annotations
     */
    private final Deque<Integer> suffixAnnotations;
    /**
     * -- GETTER --
     *  Returns the Recursive Annotation Variation (RAV) associated with the move.
     *  <p>
     *  "An RAV (Recursive Annotation Variation) is a sequence of movetext containing
     *  one or more moves enclosed in parentheses. An RAV is used to represent an
     *  alternative variation. The alternate move sequence given by an RAV is one
     *  that may be legally played by first unplaying the move that appears
     *  immediately prior to the RAV. Because the RAV is a recursive construct, it
     *  may be nested."
     *  </p>
     *  <a href="https://www.thechessdrum.net/PGN_Reference.txt">PGN_Reference.txt</a>
     *
     * @return the RAV of moves
     */
    private final Deque<PGNMove> rav;
    /**
     * -- GETTER --
     *  Returns the comment associated with the move.
     *  <p>
     *  "Comment text may appear in PGN data. There are two kinds of comments. The
     *  first kind is the "rest of line" comment; this comment type starts with a
     *  semicolon character and continues to the end of the line. The second kind
     *  starts with a left brace character and continues to the next right brace
     *  character. Comments cannot appear inside any token.
     *  </p>
     *  <p>
     *  Brace comments do not nest; a left brace character appearing in a brace
     *  comment loses its special meaning and is ignored. A semicolon appearing
     *  inside of a brace comment loses its special meaning and is ignored. Braces
     *  appearing inside of a semicolon comments lose their special meaning and are
     *  ignored."
     *  </p>
     *  <a href="https://www.thechessdrum.net/PGN_Reference.txt">PGN_Reference.txt</a>
     *
     * @return the comment text
     */
    private final String comment;
    /**
     * -- GETTER --
     *  Returns the position previous to the move.
     *
     * @return the position previous to the move
     */
    private final Position position;

    /**
	 * Constructs a PGNMove object with no coronation piece, suffix annotations, RAV, or comment.
	 *
	 * @param origin            the origin square of the move
	 * @param target            the target square of the move
	 * @param position          the position previous to the move
	 */
	public PGNMove(int origin, int target, Position position) {
		super(1L << target, origin, -1);
		this.position = position;
        this.suffixAnnotations = new BlockingList<Integer>().block();
        this.rav = new BlockingList<PGNMove>().block();
        this.comment = null;
	}


    /**
	 * Constructs a PGNMove object with no suffix annotations, RAV, or comment.
	 *
	 * @param origin            the origin square of the move
	 * @param target            the target square of the move
	 * @param coronationPiece   the piece type for coronation (if applicable)
	 * @param position          the position previous to the move
	 */
	public PGNMove(int origin, int target, int coronationPiece, Position position) {
		super(1L << target, origin, coronationPiece);
		this.position = position;
        this.suffixAnnotations = new BlockingList<Integer>().block();
        this.rav = new BlockingList<PGNMove>().block();
        this.comment = null;
	}


	/**
	 * Constructs a PGNMove object.
	 *
	 * @param origin            the origin square of the move
	 * @param target            the target square of the move
	 * @param coronationPiece   the piece type for coronation (if applicable)
	 * @param position          the position previous to the move
     * @param suffixAnnotations the suffix annotations for the move
     * @param rav               the RAV of moves for the move
     * @param comment           the comment for the move
	 */
	public PGNMove(int origin, int target, int coronationPiece, Position position, Deque<Integer> suffixAnnotations, 
            Deque<PGNMove> rav, String comment) {
		super(1L << target, origin, coronationPiece);
		this.position = position;
        this.suffixAnnotations = new BlockingList<>(suffixAnnotations).block();
        this.rav = new BlockingList<>(rav).block();
        this.comment = comment;
	}

    @Override
	public String toString() {
		return PGNHandler.toSAN(position, new Move(1L << getTarget(), getOrigin(), getPromotionPiece()));
	}

	@Override
	public int hashCode() {
		final int prime = 48947;
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
				&& Objects.equals(rav, other.rav) && Objects.equals(suffixAnnotations, other.suffixAnnotations)
				&& Objects.equals(position, other.position);
	}

}
