/*
 * Copyright 2025 Miguel Angel Luna Lobos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/lunalobos/chessapi4j/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chessapi4j;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToUci {
    private static final Logger logger = LoggerFactory.getLogger(ToUci.class);
    @Test
    void e4() {
        var sanMove = "e4";
        var position = new Position();
        var uciMove = PGNHandler.toUCI(position, sanMove).orElseThrow();
        var expectedUCIMove = "e2e4";
        logger.debug(String.format("expectedUCIMove = %s, uciMove = %s", expectedUCIMove, uciMove));
        assertEquals(expectedUCIMove, uciMove);
    }

    @Test
    void castleKingside() {
        var sanMove = "O-O";
        var position = new Position("rnbqkb1r/ppp2ppp/3p1n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 4");
        var uciMove = PGNHandler.toUCI(position, sanMove).orElseThrow();
        var expectedUCIMove = "e1g1";
        logger.debug(String.format("expectedUCIMove = %s, uciMove = %s", expectedUCIMove, uciMove));
        assertEquals(expectedUCIMove, uciMove);
    }

    @Test
    void castleQueenside() {
        var sanMove = "O-O-O";
		var position = new Position("r2q1rk1/pppbbppp/2np1n2/4p3/2B1P3/2NPBN2/PPPQ1PPP/R3K2R w KQ - 0 8");
		var uciMove = PGNHandler.toUCI(position, sanMove).orElseThrow();
		var expectedUCIMove = "e1c1";
        logger.debug(String.format("expectedUCIMove = %s, uciMove = %s", expectedUCIMove, uciMove));
		assertEquals(expectedUCIMove, uciMove);
    }

    @Test
    void e8q() {
        var sanMove = "e8=Q";
        var position = new Position("8/1k1KP3/8/8/8/8/8/8 w - - 0 0");
        var uciMove = PGNHandler.toUCI(position, sanMove).orElseThrow();
        var expectedUCIMove = "e7e8q";
        logger.debug(String.format("expectedUCIMove = %s, uciMove = %s", expectedUCIMove, uciMove));
        assertEquals(expectedUCIMove, uciMove);
    }

    @Test
    void bxc5() {
        var sanMove = "bxc5";
        var position = new Position("rn2r1k1/5b1p/p1q5/2b5/1P2pQ2/P3P3/1B3PPP/3R2K1 w - - 0 28");
        var uciMove = PGNHandler.toUCI(position, sanMove).orElseThrow();
        var expectedUCIMove = "b6c5";
        logger.debug(String.format("expectedUCIMove = %s, uciMove = %s", expectedUCIMove, uciMove));
        assertEquals(expectedUCIMove, uciMove);
    }
}
