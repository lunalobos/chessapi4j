package chessapi4j.functional;

import chessapi4j.MovementException;
import chessapi4j.Piece;
import chessapi4j.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalMoveFactoryTest {

    @Test
    void testGetMoveWithoutPromotion() {
        Move move = Factory.container.moveFactory.move(Square.E2, Square.E4);
        assertNotNull(move);
        assertEquals(Square.E2, move.origin());
        assertEquals(Square.E4, move.target());
        assertEquals(Piece.EMPTY, move.promotionPiece());
    }

    @Test
    void testGetMoveWithPromotion() {
        Move move = Factory.container.moveFactory.move(Square.A7, Square.A8, Piece.WQ);
        assertNotNull(move);
        assertEquals(Square.A7, move.origin());
        assertEquals(Square.A8, move.target());
        assertEquals(Piece.WQ, move.promotionPiece());
    }

    @Test
    void testMoveUciWithoutPromotion() {
        Move move = Factory.container.moveFactory.move("e2e4", true);
        assertNotNull(move);
        assertEquals(Square.E2, move.origin());
        assertEquals(Square.E4, move.target());
        assertEquals(Piece.EMPTY, move.promotionPiece());
    }

    @Test
    void testMoveUciWithPromotionWhite() {
        Move move = Factory.container.moveFactory.move("a7a8q", true);
        assertNotNull(move);
        assertEquals(Square.A7, move.origin());
        assertEquals(Square.A8, move.target());
        assertEquals(Piece.WQ, move.promotionPiece());
    }

    @Test
    void testMoveUciWithPromotionBlack() {
        Move move = Factory.container.moveFactory.move("h2h1n", false);
        assertNotNull(move);
        assertEquals(Square.H2, move.origin());
        assertEquals(Square.H1, move.target());
        assertEquals(Piece.BN, move.promotionPiece());
    }

    @Test
    void testMoveUciInvalidString() {
        assertThrows(MovementException.class, () -> Factory.container.moveFactory.move("invalid", true));
    }
}