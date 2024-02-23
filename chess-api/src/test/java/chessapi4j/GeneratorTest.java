package chessapi4j;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import chessapi4j.core.GeneratorFactory;
import chessapi4j.core.PositionFactory;

public class GeneratorTest {

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void numberOfPositions1() {
		Position position = PositionFactory.instance();
		Generator generator = GeneratorFactory.instance(position);
		assertEquals(20, generationTest(1, generator));
		assertEquals(400, generationTest(2, generator));
		assertEquals(8902, generationTest(3, generator));
		assertEquals(197281, generationTest(4, generator));
		assertEquals(4865609, generationTest(5, generator));
	}

	@Test
	void numberOfPositions2() {
		Position position = PositionFactory
				.instance("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
		Generator generator = GeneratorFactory.instance(position);
		assertEquals(48, generationTest(1, generator));
		assertEquals(2039, generationTest(2, generator));
		assertEquals(97862, generationTest(3, generator));
		assertEquals(4085603, generationTest(4, generator));
		assertEquals(193690690, generationTest(5, generator));
	}

	@Test
	void numberOfPositions3() {
		Position position = PositionFactory.instance("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
		Generator generator = GeneratorFactory.instance(position);
		assertEquals(14, generationTest(1, generator));
		assertEquals(191, generationTest(2, generator));
		assertEquals(2812, generationTest(3, generator));
		assertEquals(43238, generationTest(4, generator));
		assertEquals(674624, generationTest(5, generator));
		assertEquals(11030083, generationTest(6, generator));
	}

	@Test
	void numberOfPositions4() {
		Position position = PositionFactory
				.instance("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
		Generator generator = GeneratorFactory.instance(position);
		assertEquals(6, generationTest(1, generator));
		assertEquals(264, generationTest(2, generator));
		assertEquals(9467, generationTest(3, generator));
		assertEquals(422333, generationTest(4, generator));
		assertEquals(15833292, generationTest(5, generator));
	}

	@Test
	void numberOfPositions5() {
		Position position = PositionFactory.instance("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
		Generator generator = GeneratorFactory.instance(position);
		assertEquals(44, generationTest(1, generator));
		assertEquals(1486, generationTest(2, generator));
		assertEquals(62379, generationTest(3, generator));
		assertEquals(2103487, generationTest(4, generator));
		assertEquals(89941194, generationTest(5, generator));
	}

	@Test
	void numberOfPositions6() {
		Position position = PositionFactory
				.instance("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");
		Generator generator = GeneratorFactory.instance(position);
		assertEquals(46, generationTest(1, generator));
		assertEquals(2079, generationTest(2, generator));
		assertEquals(89890, generationTest(3, generator));
		assertEquals(3894594, generationTest(4, generator));
		assertEquals(164075551, generationTest(5, generator));
	}

	private int generationTest(int depth, Generator generator) {
		if (depth == 0)
			return 1;
		generator.generateLegalMoves();
		List<Generator> children = generator.getChildrenGenerators();
		return children.parallelStream().map(c -> singleGenerationTest(depth - 1, c)).reduce((a, b) -> a + b).orElse(0);
	}

	private int singleGenerationTest(int depth, Generator generator) {
		if (depth == 0)
			return 1;
		generator.generateLegalMoves();
		List<Generator> children = generator.getChildrenGenerators();

		int numPositions = 0;
		for (Generator child : children) {
			numPositions += generationTest(depth - 1, child);
		}
		return numPositions;
	}
}
