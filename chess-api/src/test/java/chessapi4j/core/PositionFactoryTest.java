package chessapi4j.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import chessapi4j.Generator;
import chessapi4j.Position;

class PositionFactoryTest {

	@Test
	void test1() {
		Position position = PositionFactory.instance();
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(5, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		System.out.printf("Test1 -> positions: %d hash entrys: %d superposition rate: %f\n",4865609, 
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test2() {
		Position position = PositionFactory
				.instance("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(4, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		System.out.printf("Test2 -> positions: %d hash entrys: %d superposition rate: %f\n", 4085603, 
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test3() {
		Position position = PositionFactory.instance("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(5, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		System.out.printf("Test3 -> positions: %d hash entrys: %d superposition rate: %f\n", 674624, 
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test4() {
		Position position = PositionFactory
				.instance("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(4, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		System.out.printf("Test4 -> positions: %d hash entrys: %d superposition rate: %f\n", 422333, 
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test5() {
		Position position = PositionFactory.instance("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(4, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		System.out.printf("Test5 -> positions: %d hash entrys: %d superposition rate: %f\n", 2103487, 
				superpositionMap.size(),
				count / ((double) superpositionMap.size()));
		assertEquals(true, count / ((double) superpositionMap.size()) <= 0.9);
	}

	@Test
	void test6() {
		Position position = PositionFactory
				.instance("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");
		Map<Integer, Integer> superpositionMap = new HashMap<>();
		generateNodes(4, position, superpositionMap);
		double count = (double) superpositionMap.entrySet().stream().filter(entry -> entry.getValue() > 1)
				.mapToInt(entry -> 1).count();
		System.out.printf("Test6 -> positions: %d hash entrys: %d superposition rate: %f\n", 3894594, 
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
		Generator generator = GeneratorFactory.instance(position);

		generator.generateLegalMoves();
		List<? extends Position> children = generator.getChildren();
		for (Position child : children) {
			generateNodes(depth - 1, child, superpositionMap);
		}
	}
	
	@Test
	void hashTableTest() {
		// 2560 positions per MB
		int nodesPerMB = 2560;
		int hashSizeMB = 256;
		Map<Position, Object> hashTable = new FixedSizeHashMap<>(nodesPerMB*hashSizeMB, 0.5F, true);
		Position position = PositionFactory.instance();
		int size1 = nodesPerMB*hashSizeMB;
		generate(5, position, hashTable);
		int size2 = hashTable.size();
		assertEquals(size1, size2);
	}
	
	private void generate(int depth, Position position, Map<Position, Object> hashTable) {
		hashTable.put(position, new Object());
		if (depth == 0) {
			return;
		}
		Generator generator = GeneratorFactory.instance(position);
		generator.generateLegalMoves();
		List<? extends Position> children = generator.getChildren();
		for (Position child : children) {
			generate(depth - 1, child, hashTable);
		}
	}
}

class FixedSizeHashMap<K,V>  extends LinkedHashMap<K,V>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8627211931738255730L;
	private int capacity;

	public FixedSizeHashMap(int capacity, float loadFactor, boolean accessOrder) {
		super(capacity, loadFactor, accessOrder);
		this.capacity = capacity;
	}

	public FixedSizeHashMap(int capacity, float loadFactor) {
		super(capacity, loadFactor);
		this.capacity = capacity;
	}

	public FixedSizeHashMap(int capacity) {
		super(capacity);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return size() > capacity;
	}
	
}
