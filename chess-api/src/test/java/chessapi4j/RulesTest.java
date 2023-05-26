package chessapi4j;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import chessapi4j.core.MoveFactory;
import chessapi4j.core.PositionFactory;

class RulesTest {

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		try {
			boolean isLegal = Rules.legal(PositionFactory.instance(), MoveFactory.instance("e2e4", false));
			assertEquals(true, isLegal);
		} catch (MovementException e) {
			e.printStackTrace();
		}
	}

}
