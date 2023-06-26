package chessapi4j;

/**
 * It's thrown when move inconsistencies happens.
 * @author lunalobos
 *
 */
public class MovementException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4964697290998435420L;

	public MovementException(String msg) {
		super(msg);
	}
	
}
