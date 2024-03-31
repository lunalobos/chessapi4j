package chessapi4j;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MoveTest {

	@Test
	void test() {
		 try {
			Move move = MoveFactory.instance("e2e4", false);
			assertEquals("e2e4", move.toString());
		} catch (MovementException e) {

			fail();
		}
	}

}
