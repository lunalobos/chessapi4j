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

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import java.util.List;
import java.util.Map;


import org.junit.jupiter.api.Test;

class PositionFactoryTest {
	private static final Logger logger = LoggerFactory.getLogger(PositionFactoryTest.class);

	@Test
	void test1() {
		Position position = new Position();
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(5, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		logger.debug("Test1 -> positions: %d hash entrys: %d superposition rate: %f", 4865609,
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test2() {
		Position position = new Position("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(4, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		logger.debug("Test2 -> positions: %d hash entrys: %d superposition rate: %f", 4085603,
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test3() {
		Position position = new Position("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(5, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		logger.debug("Test3 -> positions: %d hash entrys: %d superposition rate: %f", 674624,
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test4() {
		Position position = new Position("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(4, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		logger.debug("Test4 -> positions: %d hash entrys: %d superposition rate: %f", 422333,
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test5() {
		Position position = new Position("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(4, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		logger.debug("Test5 -> positions: %d hash entrys: %d superposition rate: %f", 2103487,
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test6() {
		Position position = new Position("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(4, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		logger.debug("Test6 -> positions: %d hash entrys: %d superposition rate: %f", 3894594,
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	private void generateNodes(int depth, Position position, Map<Integer, Integer> superpositionMap) {

		int hash = position.hashCode();
		superpositionMap.merge(hash, 1, (i1, i2) -> i1 + i2);
		if (depth == 0) {
			return;
		}
		Generator generator = GeneratorFactory.instance();

		List<Position> children = generator.generateChildren(position);
		for (Position child : children) {
			generateNodes(depth - 1, child, superpositionMap);
		}
	}

	@Test
	void hashTableTest() {
		// 2560 positions per MB
		int nodesPerMB = 2560;
		int hashSizeMB = 256;
		Map<Position, Object> hashTable = new FixedSizeHashMap<>(nodesPerMB * hashSizeMB, 0.5F, true);
		Position position = new Position();
		int size1 = nodesPerMB * hashSizeMB;
		generate(5, position, hashTable);
		int size2 = hashTable.size();
		assertEquals(size1, size2);
	}

	private void generate(int depth, Position position, Map<Position, Object> hashTable) {
		hashTable.put(position, new Object());
		if (depth == 0) {
			return;
		}
		Generator generator = GeneratorFactory.instance();
		List<? extends Position> children = generator.generateChildren(position);
		for (Position child : children) {
			generate(depth - 1, child, hashTable);
		}
	}
}


