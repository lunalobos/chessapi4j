package chessapi4j.functional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FunctionalPGNHandlerTest {
    @Test
	void testParseGame() {
		var games = PGNHandler.parseGames(this.getClass().getClassLoader().getResourceAsStream("example.pgn"));
		assertFalse(games.isEmpty());
	}
}
