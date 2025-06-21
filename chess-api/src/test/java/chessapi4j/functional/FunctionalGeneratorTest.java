package chessapi4j.functional;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FunctionalGeneratorTest {
    private static final Logger logger = Factory.getLogger(FunctionalGeneratorTest.class);
    @Test
    void numberOfPositions1() {
        var position = Factory.startPos();
        var d1 = OffsetDateTime.now();
        assertEquals(20, generationTest(1, position));
        assertEquals(400, generationTest(2, position));
        assertEquals(8902, generationTest(3, position));
        assertEquals(197281, generationTest(4, position));
        assertEquals(4865609, generationTest(5, position));
        var d2 = OffsetDateTime.now();
        logger.debug("GeneratorTest-Position%d time[ms]: %d", 1,
                d2.toInstant().toEpochMilli() - d1.toInstant().toEpochMilli());
    }

    @Test
    void numberOfPositions2() {
        var position = Factory.position("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        var d1 = OffsetDateTime.now();
        assertEquals(48, generationTest(1, position));
        assertEquals(2039, generationTest(2, position));
        assertEquals(97862, generationTest(3, position));
        assertEquals(4085603, generationTest(4, position));
        assertEquals(193690690, generationTest(5, position));
        var d2 = OffsetDateTime.now();
        logger.debug("GeneratorTest-Position%d time[ms]: %d", 1,
                d2.toInstant().toEpochMilli() - d1.toInstant().toEpochMilli());
    }

    @Test
    void numberOfPositions3() {
        var position = Factory.position("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
        var d1 = OffsetDateTime.now();
        assertEquals(14, generationTest(1, position));
        assertEquals(191, generationTest(2, position));
        assertEquals(2812, generationTest(3, position));
        assertEquals(43238, generationTest(4, position));
        assertEquals(674624, generationTest(5, position));
        assertEquals(11030083, generationTest(6, position));
        var d2 = OffsetDateTime.now();
        logger.debug("GeneratorTest-Position%d time[ms]: %d", 3,
                d2.toInstant().toEpochMilli() - d1.toInstant().toEpochMilli());
    }

    @Test
    void numberOfPositions4() {
        var position = Factory.position("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");

        var d1 = OffsetDateTime.now();
        assertEquals(6, generationTest(1, position));
        assertEquals(264, generationTest(2, position));
        assertEquals(9467, generationTest(3, position));
        assertEquals(422333, generationTest(4, position));
        assertEquals(15833292, generationTest(5, position));
        var d2 = OffsetDateTime.now();
        logger.debug("GeneratorTest-Position%d time[ms]: %d", 4,
                d2.toInstant().toEpochMilli() - d1.toInstant().toEpochMilli());
    }

    @Test
    void numberOfPositions5() {
        var position = Factory.position("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        var d1 = OffsetDateTime.now();
        assertEquals(44, generationTest(1, position));
        assertEquals(1486, generationTest(2, position));
        assertEquals(62379, generationTest(3, position));
        assertEquals(2103487, generationTest(4, position));
        assertEquals(89941194, generationTest(5, position));
        var d2 = OffsetDateTime.now();
        logger.debug("GeneratorTest-Position%d time[ms]: %d", 5,
                d2.toInstant().toEpochMilli() - d1.toInstant().toEpochMilli());
    }

    @Test
    void numberOfPositions6() {
        var position = Factory.position("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");
        var d1 = OffsetDateTime.now();
        assertEquals(46, generationTest(1, position));
        assertEquals(2079, generationTest(2, position));
        assertEquals(89890, generationTest(3, position));
        assertEquals(3894594, generationTest(4, position));
        assertEquals(164075551, generationTest(5, position));
        var d2 = OffsetDateTime.now();
        logger.debug("GeneratorTest-Position%d time[ms]: %d", 6,
                d2.toInstant().toEpochMilli() - d1.toInstant().toEpochMilli());
    }

    void customPerf(){
        var fen = "r3k2r/p1ppqNb1/bn2p1p1/3P4/1p2n3/P1N2Q1p/1PPBBPPP/R3K2R b KQkq - 0 2";
        var depth = 1;
        perf(new Position(fen), depth);
    }


    void coronation(){
        var pos = Factory.position("r3k2r/p1ppqpb1/Bn2pnp1/3PN3/1p2P3/P1N2Q2/1PPB1PpP/R3K2R b KQkq - 0 2");
        var children = Factory.generator().legalMoves(pos);
        children.stream().filter(t -> t.getV2().toString().equals("g2h1b"))
                .forEach(t -> {
                    logger.debug("%s", t.getV2());
                    logger.debug("%s", t.getV1());
                    assertEquals(
                            "r3k2r/p1ppqpb1/Bn2pnp1/3PN3/1p2P3/P1N2Q2/1PPB1P1P/R3K2b w Qkq - 0 3",
                            t.getV1().fen()
                    );
                });
    }

    private int generationTest(int depth, Position position) {
        if (depth == 0)
            return 1;
        var children = Factory.generator().legalMoves(position);
        return children.stream().parallel().map(c -> singleGenerationTest(depth - 1, c))
                .reduce(0, Integer::sum);
    }

    private int singleGenerationTest(int depth, Tuple<Position,Move> tuple) {
        if (depth == 0)
            return 1;
        return Factory.generator().legalMoves(tuple.getV1())
                .stream()
                .map(t -> singleGenerationTest(depth - 1, t))
                .reduce(0, Integer::sum);
    }

    private void perf(Position position, int depth){
        Factory.generator().legalMoves(position)
                .forEach(t -> {
                    var n = singleGenerationTest(depth - 1, t);
                    logger.debug("%s - %s: %d",  t.getV1().fen(), t.getV2(), n);
                });
    }
}