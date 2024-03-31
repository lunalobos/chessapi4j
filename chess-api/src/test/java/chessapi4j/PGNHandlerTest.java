package chessapi4j;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class PGNHandlerTest {

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testParseGame() {
		String path = "example.pgn";
		List<Game> games = PGNHandler.parseGames(path);
		assertEquals(true, !games.isEmpty());
	}

}
