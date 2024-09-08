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

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class GeneratorTest {

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void numberOfPositions1() {
		Position position = new Position();
		Date d1 = new Date();
		assertEquals(20, generationTest(1, position));
		assertEquals(400, generationTest(2, position));
		assertEquals(8902, generationTest(3, position));
		assertEquals(197281, generationTest(4, position));
		assertEquals(4865609, generationTest(5, position));
		Date d2 = new Date();
		System.out.printf("GeneratorTest-Position%d time[ms]: %d\n", 1, d2.getTime() - d1.getTime());
	}

	@Test
	void numberOfPositions2() {
		Position position = new Position("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");

		Date d1 = new Date();
		assertEquals(48, generationTest(1, position));
		assertEquals(2039, generationTest(2, position));
		assertEquals(97862, generationTest(3, position));
		assertEquals(4085603, generationTest(4, position));
		assertEquals(193690690, generationTest(5, position));
		Date d2 = new Date();
		System.out.printf("GeneratorTest-Position%d time[ms]: %d\n", 2, d2.getTime() - d1.getTime());
	}

	@Test
	void numberOfPositions3() {
		Position position = new Position("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
		Date d1 = new Date();
		assertEquals(14, generationTest(1, position));
		assertEquals(191, generationTest(2, position));
		assertEquals(2812, generationTest(3, position));
		assertEquals(43238, generationTest(4, position));
		assertEquals(674624, generationTest(5, position));
		assertEquals(11030083, generationTest(6, position));
		Date d2 = new Date();
		System.out.printf("GeneratorTest-Position%d time[ms]: %d\n", 3, d2.getTime() - d1.getTime());
	}

	@Test
	void numberOfPositions4() {
		Position position = new Position("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");

		Date d1 = new Date();
		assertEquals(6, generationTest(1, position));
		assertEquals(264, generationTest(2, position));
		assertEquals(9467, generationTest(3, position));
		assertEquals(422333, generationTest(4, position));
		assertEquals(15833292, generationTest(5, position));
		Date d2 = new Date();
		System.out.printf("GeneratorTest-Position%d time[ms]: %d\n", 4, d2.getTime() - d1.getTime());
	}

	@Test
	void numberOfPositions5() {
		Position position = new Position("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
		Date d1 = new Date();
		assertEquals(44, generationTest(1, position));
		assertEquals(1486, generationTest(2, position));
		assertEquals(62379, generationTest(3, position));
		assertEquals(2103487, generationTest(4, position));
		assertEquals(89941194, generationTest(5, position));
		Date d2 = new Date();
		System.out.printf("GeneratorTest-Position%d time[ms]: %d\n", 5, d2.getTime() - d1.getTime());
	}

	@Test
	void numberOfPositions6() {
		Position position = new Position("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");
		Date d1 = new Date();
		assertEquals(46, generationTest(1, position));
		assertEquals(2079, generationTest(2, position));
		assertEquals(89890, generationTest(3, position));
		assertEquals(3894594, generationTest(4, position));
		assertEquals(164075551, generationTest(5, position));
		Date d2 = new Date();
		System.out.printf("GeneratorTest-Position%d time[ms]: %d\n", 6, d2.getTime() - d1.getTime());
	}

	private int generationTest(int depth, Position position) {
		if (depth == 0)
			return 1;

		List<Position> children = GeneratorFactory.instance().generateChildren(position);
		return children.parallelStream().map(c -> singleGenerationTest(depth - 1, c)).reduce((a, b) -> a + b).orElse(0);
	}

	private int singleGenerationTest(int depth, Position position) {
		if (depth == 0)
			return 1;

		List<Position> children = GeneratorFactory.instance().generateChildren(position);

		int numPositions = 0;
		for (Position child : children) {
			numPositions += generationTest(depth - 1, child);
		}
		return numPositions;
	}
}
