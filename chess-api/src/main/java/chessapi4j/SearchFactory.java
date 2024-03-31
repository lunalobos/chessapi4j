package chessapi4j;

/**
 * Factory class for {@code Search} implementations.
 *
 * @author lunalobos
 *
 */
public class SearchFactory {
	public static Search searchImpl() {
		return new MontecarloSearch();
	}
}
