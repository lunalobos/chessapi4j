package chessapi4j;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class SearchTest {

	@Test
	void test() {
		Search search = SearchFactory.searchImpl();
		Position p = new Position();
		Optional<Move> move = search.seekBestMove(p, () -> EvaluatorFactory.getImpl(), 5, 3);
		assertTrue(move.isPresent());
	}

}
