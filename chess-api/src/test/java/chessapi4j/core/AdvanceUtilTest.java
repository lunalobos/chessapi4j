package chessapi4j.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AdvanceUtilTest {

	@Test
	void inCheckTest() {
		int inCheck = AdvanceUtil.isInCheck(PositionFactory.instance("rnbqkbnr/ppppp2p/5p2/6pQ/3PP3/8/PPP2PPP/RNB1KBNR b KQkq - 0 3"));
		assertEquals(1, inCheck);
	}
	
	@Test
	void notInCheckTest() {
		int inCheck = AdvanceUtil.isInCheck(PositionFactory.instance("rnbqkbnr/ppppp2p/5p2/6p1/3PP3/8/PPP2PPP/RNBQKBNR w KQkq - 0 3"));
		assertEquals(0, inCheck);
	}
	
	@Test
	void lackOfMaterialTest() {
		int lackOfMaterial = AdvanceUtil.lackOfMaterial(PositionFactory.instance("8/8/4k3/8/2N5/8/4K3/8 w - - 0 1"));
		assertEquals(1, lackOfMaterial);
	}
	
	@Test
	void notLackOfMaterialTest() {
		int lackOfMaterial = AdvanceUtil.lackOfMaterial(PositionFactory.instance("8/8/4k3/8/2N3N1/8/4K3/8 w - - 0 1"));
		assertEquals(0, lackOfMaterial);
	}
	
	@Test
	void isFiftyMovesDraw() {
		int fiftyMovesDraw = AdvanceUtil.fiftyMoves(PositionFactory.instance("8/8/4k3/8/2N3N1/8/4K3/8 w - - 50 1"));
		assertEquals(1, fiftyMovesDraw);
	}
	
	@Test
	void isNotFiftyMovesDraw() {
		int fiftyMovesDraw = AdvanceUtil.fiftyMoves(PositionFactory.instance("8/8/4k3/8/2N3N1/8/4K3/8 w - - 49 1"));
		assertEquals(0, fiftyMovesDraw);
	}

}
