package chessapi4j;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RulesTest {

	@Test
	void test() {
		try {
			boolean isLegal = Rules.legal(new Position(), MoveFactory.instance("e2e4", false));
			assertEquals(true, isLegal);
		} catch (MovementException e) {
			e.printStackTrace();
		}
	}

}