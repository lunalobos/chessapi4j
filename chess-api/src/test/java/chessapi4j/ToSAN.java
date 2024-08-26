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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ToSAN {

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() {
		// startpos e4
		try {
			Move e4 = MoveFactory.instance("e2e4", true);
			assertEquals("e4", PGNHandler.toSAN(new Position(), e4.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// r1bqkb1r/ppp2ppp/3p1n2/4p3/3nP3/3P1N2/PPP1NPPP/R1BQKB1R w KQkq - 0 6 Nfxd4
		try {
			Move _Nfxd4 = MoveFactory.instance("f3d4", true);
			assertEquals("Nfxd4",
					PGNHandler.toSAN(
							new Position("r1bqkb1r/ppp2ppp/3p1n2/4p3/3nP3/3P1N2/PPP1NPPP/R1BQKB1R w KQkq - 0 6"),
							_Nfxd4.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// r1bqkb1r/ppp2ppp/3p1n2/4p3/3nP3/1N1P1N2/PPP2PPP/R1BQKB1R w KQkq - 0 6 Nfxd4
		try {
			Move _Nfxd4 = MoveFactory.instance("f3d4", true);
			assertEquals("Nfxd4",
					PGNHandler.toSAN(
							new Position("r1bqkb1r/ppp2ppp/3p1n2/4p3/3nP3/1N1P1N2/PPP2PPP/R1BQKB1R w KQkq - 0 6"),
							_Nfxd4.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// r2q1rk1/ppp1bppp/3pbn2/4pN2/3nP3/3P1N2/PPP1BPPP/R1BQK2R w KQ - 0 9 N3xd4

		try {
			Move _N3xd4 = MoveFactory.instance("f3d4", true);
			assertEquals("N3xd4",
					PGNHandler.toSAN(new Position("r2q1rk1/ppp1bppp/3pbn2/4pN2/3nP3/3P1N2/PPP1BPPP/R1BQK2R w KQ - 0 9"),
							_N3xd4.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// 8/8/8/1k6/4Q2Q/8/8/1K5Q w - - 0 1 Q1e1
		try {
			Move _Q1e1 = MoveFactory.instance("h1e1", true);
			assertEquals("Q1e1", PGNHandler.toSAN(new Position("8/8/8/1k6/4Q2Q/8/8/1K5Q w - - 0 1"), _Q1e1.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// 8/8/8/1k6/4Q2Q/8/8/1K5Q w - - 0 1 Qh4e1
		try {
			Move _Qh4e1 = MoveFactory.instance("h4e1", true);
			assertEquals("Qh4e1",
					PGNHandler.toSAN(new Position("8/8/8/1k6/4Q2Q/8/8/1K5Q w - - 0 1"), _Qh4e1.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// 8/8/8/1k6/4Q2Q/8/8/1K5Q w - - 0 1 Qee1
		try {
			Move _Qee1 = MoveFactory.instance("e4e1", true);
			assertEquals("Qee1", PGNHandler.toSAN(new Position("8/8/8/1k6/4Q2Q/8/8/1K5Q w - - 0 1"), _Qee1.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// 8/8/2k5/8/8/2N3N1/8/1K4N1 w - - 0 1 N1e2
		try {
			Move _N1e2 = MoveFactory.instance("g1e2", true);
			assertEquals("N1e2",
					PGNHandler.toSAN(new Position("8/8/2k5/8/8/2N3N1/8/1K4N1 w - - 0 1"), _N1e2.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// 8/8/2k5/8/8/2N3N1/8/1K4N1 w - - 0 1 Ng3e2
		try {
			Move _Ng3e2 = MoveFactory.instance("g3e2", true);
			assertEquals("Ng3e2",
					PGNHandler.toSAN(new Position("8/8/2k5/8/8/2N3N1/8/1K4N1 w - - 0 1"), _Ng3e2.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// 8/8/2k5/8/8/2N3N1/8/1K4N1 w - - 0 1 Nce2
		try {
			Move _Nce2 = MoveFactory.instance("c3e2", true);
			assertEquals("Nce2",
					PGNHandler.toSAN(new Position("8/8/2k5/8/8/2N3N1/8/1K4N1 w - - 0 1"), _Nce2.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// rnbqkb1r/ppp2ppp/3p1n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 0 4 Qxf7#
		try {
			Move _Qxf7Mate = MoveFactory.instance("h5f7", true);
			assertEquals("Qxf7#",
					PGNHandler.toSAN(new Position("rnbqkb1r/ppp2ppp/3p1n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 0 4"),
							_Qxf7Mate.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// rnb1kb1r/ppp2ppp/4pn2/q7/2B5/2N2N2/PPPP1PPP/R1BQK2R w KQkq - 0 6 0-0
		try {
			Move _OO = MoveFactory.instance("e1g1", true);
			assertEquals("O-O", PGNHandler.toSAN(
					new Position("rnb1kb1r/ppp2ppp/4pn2/q7/2B5/2N2N2/PPPP1PPP/R1BQK2R w KQkq - 0 6"), _OO.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}

		// 2k5/4KP2/8/8/8/8/8/8 w - - 0 1 f8=D+
		try {
			Move _f8Qcheck = MoveFactory.instance("f7f8q", true);
			assertEquals("f8=Q+",
					PGNHandler.toSAN(new Position("2k5/4KP2/8/8/8/8/8/8 w - - 0 1"), _f8Qcheck.toString()));
		} catch (MovementException e) {
			fail(e.getMessage());
		}
	}
}
