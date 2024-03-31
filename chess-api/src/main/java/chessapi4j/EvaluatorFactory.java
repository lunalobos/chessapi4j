package chessapi4j;

/**
 * Provides {@code Evaluator} implementations.
 *
 * @author lunalobos
 */
public class EvaluatorFactory {
	public static Evaluator getImpl() {
		return new EvaluatorImp();
	}

}
