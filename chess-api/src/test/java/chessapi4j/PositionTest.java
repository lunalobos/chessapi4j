package chessapi4j;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PositionTest {

	@Test
	void pawnOnE4() {
		Position position = new Position();

		Piece piece = position.getPiece(Square.E2);

		assertEquals(Piece.WP, piece);
	}

}
