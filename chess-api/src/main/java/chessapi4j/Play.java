package chessapi4j;

/**
 * Interface for execute moves. A Play object takes for input move and position,
 * then executes the move on a deep copy of the original position.
 * 
 * @author lunalobos
 *
 */
public interface Play {
	/**
	 * Transform the position executing the move.
	 * 
	 * @throws MovementException
	 */
	void executeMove() throws MovementException;

	/**
	 * Gives the position of this move executor.
	 * 
	 * @return
	 */
	Position getPosition();

	/**
	 * Gives the move object to execute.
	 * 
	 * @return
	 */
	Move getMove();
}
