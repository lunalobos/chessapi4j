package chessapi4j.functional;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionalPositionTest {

    @Test
    void sanMove(){
        var pos = Factory.startPos()
            .sanMove("Nf3")
            .sanMove("d5")
            .sanMove("g3")
            .sanMove("c5")
            .sanMove("Bg2");
        assertEquals("rnbqkbnr/pp2pppp/8/2pp4/8/5NP1/PPPPPPBP/RNBQK2R b KQkq - 1 3", pos.fen());
    }
}
