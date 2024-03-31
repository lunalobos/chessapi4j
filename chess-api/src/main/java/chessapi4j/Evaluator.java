package chessapi4j;
/**
 * Interface for heuristics position evaluation.
 *
 * @author lunalobos
 */
public interface Evaluator {
	/**
     * Calculates the evaluation score for the given chess position using various evaluation heuristics.
     *
     * @param position The chess position for which to calculate the evaluation score.
     * @return The numerical evaluation score for the position.
     */
	int evaluate(Position position);
}
