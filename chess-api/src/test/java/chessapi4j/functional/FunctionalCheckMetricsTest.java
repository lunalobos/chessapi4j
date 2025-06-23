package chessapi4j.functional;

import chessapi4j.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalCheckMetricsTest {

    @Test
    void inCheck() {
        var fen = "r3k2r/p2p1pb1/bn1qpnp1/2p1N3/1p2P3/2N2Q1p/PPP1BPPP/R1BK3R w kq - 0 3";
        var position = new Position(fen);
        var container = Factory.container;
        var checkMetrics = container.checkMetrics;
        var bitboards = position.bitboards();
        var move = new Bitboard(Square.G3).getValue();
        var pieceType = Piece.WK.ordinal();
        var from = Square.F2.ordinal();
        for (var index = 0; index < 12; index++) {
            bitboards[index] = bitboards[index] & (~move);
        }
        bitboards[pieceType - 1] = (bitboards[pieceType - 1] & (~(1L << from))) | move;
        var inCheck = checkMetrics.inCheck(bitboards, position.whiteMove() ? 1L : 0L);
        assertEquals(1L, inCheck);
    }
}