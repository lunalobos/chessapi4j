/*
 * Copyright 2024 Miguel Angel Luna Lobos
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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RulesTest {

	@Test
	void legal() {
		try {
			boolean isLegal = Rules.legal(new Position(), MoveFactory.instance("e2e4", false));
			assertTrue(isLegal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void checkFen1() {
		var position = new Position();
		assertTrue(Rules.isValidFen(position.toFen()));
	}

	@Test
	void checkFen2() {
		assertTrue(Rules.isValidFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1"));
	}

	@Test
	void checkFen3() {
		assertTrue(Rules.isValidFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1"));
	}

	@Test
	void checkFen4() {
		assertTrue(Rules.isValidFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"));
	}

	@Test
	void checkFen5() {
		// 8th rank pawn
		assertFalse(Rules.isValidFen("r3k2p/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"));
	}

	@Test
	void checkFen6() {
		// no black king
		assertFalse(Rules.isValidFen("r6r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"));
	}

	@Test
	void checkFen7() {
		// no white king
		assertFalse(Rules.isValidFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1R2 w kq - 0 1"));
	}

	@Test
	void checkFen8() {
		assertTrue(Rules.isValidFen("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"));
	}

	@Test
	void checkFen9() {
		assertTrue(Rules.isValidFen("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"));
	}

	@Test
	void checkFen10() {
		// nocastle
		assertFalse(Rules.isValidFen("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w KQkq - 0 10"));
	}

	@Test
	void checkFen11() {
		// nocastle
		assertFalse(Rules.isValidFen("this is not a fen"));
	}

	

}
