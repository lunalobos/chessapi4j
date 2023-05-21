package chessapi4j;

import java.util.List;

/**
 * Implementations of this interface are intended to generate all possible moves
 * from an original position.
 * 
 * @author lunalobos
 *
 */
public interface Generator {

	/**
	 * Creates the list of legal positions that arise from this particular position.
	 */
	void generateLegalMoves();

	/**
	 * If the generateLegalMoves() method was previously called, this method returns
	 * the calculated positions.
	 * 
	 * @return the children list
	 */
	List<? extends Position> getChildren();

	/**
	 * Returns the original position from which all possible positions arise.
	 * 
	 * @return the original position
	 */
	Position getPosition();

	/**
	 * Sets the list of possible positions. It may lead to inconsistencies.
	 * 
	 * @param childs
	 */
	void setChildren(List<Position> childs);

	/**
	 * Sets the original position. If used, the generateLegalMoves() method must be
	 * called again.
	 * 
	 * @param position
	 */
	void setPosition(Position position);

	/**
	 * Returns a children list of the current instance, each with a position derived
	 * from the original.
	 * 
	 * @return the list of child instances
	 */
	List<Generator> getChildrenGenerators();

	/**
	 * Returns a list with all the legal moves.
	 * 
	 * @return the legalMoves list
	 */
	List<Move> getLegalMoves();
}
