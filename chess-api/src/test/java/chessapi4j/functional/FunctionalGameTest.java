package chessapi4j.functional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FunctionalGameTest {

    @Test
    void eco() {
        var is = this.getClass().getClassLoader().getResourceAsStream("example.pgn");
        var games = PGNHandler.parseGames(is);

        var coincidenceCount = games.stream().filter(game -> {
            return game.getEcoDescriptor().getEco().equals(game.getTagValue("ECO").orElse(""));
        }).count();
        assertTrue(((double) coincidenceCount) / ((double) games.size()) > 0.74);
    }

}
