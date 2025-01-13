package chessapi4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PGNMoveTest {

    @Test
    void test() {
        var fen = "rnbqkbnr/pp3ppp/4p3/2pp4/2PP4/5N2/PP2PPPP/RNBQKB1R w KQkq - 0 4";
        var position = new Position(fen);
        var move = new PGNMove(new Move(Square.C4, Square.D5), position);
        assertEquals("cxd5", move.toString());
    }
}
