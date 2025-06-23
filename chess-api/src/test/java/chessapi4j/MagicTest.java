package chessapi4j;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class MagicTest {
    private static final Logger logger = LoggerFactory.getLogger(MagicTest.class);

    @Test
    void bishop() {
        var magic = GeneratorFactory.container.visibleMagic;
        var visibleMetrics = GeneratorFactory.container.visibleMetrics;
        var randowm = new Random();

        var counter = 0;
        var failCounter = 0;
        while (counter < 9999999) {
            counter++;
            var enemies = randowm.nextLong();
            var friends = randowm.nextLong();
            friends = friends & ~enemies;
            var square = randowm.nextInt(64);
            var hashed = magic.visibleBishop(square, friends, enemies);
            var expected = visibleMetrics.visibleSquaresBishop(square, friends, enemies);
            if (expected != hashed) {
                logger.error("\nMagic error:\nsquare:%s\nfriends:%s\nenemies:%s\nexpected:%s\nhashed:%s\n",
                        Square.values()[square], new Bitboard(friends), new Bitboard(enemies), new Bitboard(expected),
                        new Bitboard(hashed));
                failCounter++;
                if (failCounter > 10) {
                    fail();
                }
            }
        }

    }
}
