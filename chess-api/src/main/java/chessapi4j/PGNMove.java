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

import java.util.List;
import java.util.Objects;

/**
 * Represents a move with Portable Game Notation (PGN) properties. This class extends the
 * Move class and adds PGN specific properties. The position previous to the move is
 * needed to be passed in the constructors. ToString method returns the SAN move
 * notation. 
 *
 * @author lunalobos
 * @since 1.1.0
 */
public class PGNMove extends Move {

	private List<Integer> suffixAnnotations;
	private List<PGNMove> rav;
	private String comment;
	private Position position;

	/**
	 * Constructs a PGNMove object with the specified origin, target, and promotion
	 * piece.
	 *
	 * @param origin          the origin square of the move
	 * @param target          the target square of the move
	 * @param coronationPiece the piece type for coronation (if applicable)
	 */
	public PGNMove(int origin, int target, int coronationPiece, Position position) {
		super(1L << target, origin, coronationPiece);
		this.position = position;
	}

	/**
	 * Constructs a PGNMove object based on a Move object.
	 *
	 * @param move: the Move object to construct from
	 */
	public PGNMove(Move move, Position position) {
		this(move.getOrigin(), move.getTarget(), move.getPromotionPiece(), position);
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

	/**
	 * Sets the position for the move. Using this method is not recommended.
	 * A position should be provided when creating the PGNMove object.
	 * @param position
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * Returns the position previous to the move.
	 * @return
	 */
	public Position getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return PGNHandler.toSAN(position, new Move(1L << getTarget(), getOrigin(), getPromotionPiece()));
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
				&& Objects.equals(rav, other.rav) && Objects.equals(suffixAnnotations, other.suffixAnnotations)
				&& Objects.equals(position, other.position);
	}

}
