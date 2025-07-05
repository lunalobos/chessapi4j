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
package chessapi4j.functional;

import chessapi4j.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static chessapi4j.Square.*;

/**
 * Main factory class for the functional API. Provides convenience methods for
 * creating {@link Position}, {@link Generator} and {@link Move} instances.
 * Some methods for moves are optimized to avoid creating new instances,
 * resulting
 * in less garbage collection.
 * 
 * @author lunalobos
 * @since 1.2.9
 */
public class Factory {
    private static final Map<String, Logger> LOGGERS = new HashMap<>();
    private static final String DEFAULT_FILTER_LEVEL = "DEBUG";
    static final ZobristHasher zobristHasher = new ZobristHasher();
    static final long initialHash = zobristHasher.computeZobristHash(
            new long[] {
                    new Bitboard(A2, B2, C2, D2, E2, F2, G2, H2).getValue(),
                    new Bitboard(B1, G1).getValue(),
                    new Bitboard(C1, F1).getValue(),
                    new Bitboard(A1, H1).getValue(),
                    new Bitboard(D1).getValue(),
                    new Bitboard(E1).getValue(),
                    new Bitboard(A7, B7, C7, D7, E7, F7, G7, H7).getValue(),
                    new Bitboard(B8, G8).getValue(),
                    new Bitboard(C8, F8).getValue(),
                    new Bitboard(A8, H8).getValue(),
                    new Bitboard(D8).getValue(),
                    new Bitboard(E8).getValue()
            },
            true,
            true,
            true,
            true,
            true,
            -1);

    static final Container container = defaultContainer();
    static final Position startPos = new Position();

    static Logger getLogger(Class<?> clazz) {
        var logger = LOGGERS.computeIfAbsent(clazz.getCanonicalName(), k -> new Logger(clazz));
        logger.setFilterLevel(DEFAULT_FILTER_LEVEL);
        return logger;
    }

    static Container defaultContainer() {
        var random = new Random();
        var matrixUtil = new MatrixUtil();
        var moveFactory = new MoveFactory();
        var internalUtil = new InternalUtil(matrixUtil);
        var visibleMetrics = new VisibleMetrics(matrixUtil, random);
        var lackOfMaterialMetrics = new LackOfMaterialMetrics();
        var checkMetrics = new CheckMetrics(visibleMetrics, internalUtil);
        var checkmateMetrics = new CheckmateMetrics(visibleMetrics, internalUtil);
        var stalemateMetrics = new StalemateMetrics(visibleMetrics, internalUtil);
        var pawnGenerator = new PawnGenerator(visibleMetrics, checkMetrics, matrixUtil, internalUtil, moveFactory);
        var knightGenerator = new KnightGenerator(matrixUtil, moveFactory);
        var bishopGenerator = new BishopGenerator(visibleMetrics, internalUtil, moveFactory);
        var rookGenerator = new RookGenerator(visibleMetrics, internalUtil, moveFactory);
        var queenGenerator = new QueenGenerator(visibleMetrics, internalUtil, moveFactory);
        var kingGenerator = new KingGenerator(visibleMetrics, matrixUtil, moveFactory);
        var bitboardGenerator = new BitboardGenerator(pawnGenerator, knightGenerator, bishopGenerator, rookGenerator,
                queenGenerator, kingGenerator, visibleMetrics, internalUtil, matrixUtil);
        var generator = new Generator(pawnGenerator, kingGenerator, matrixUtil);
        return Container.builder()
                .random(random)
                .matrixUtil(matrixUtil)
                .moveFactory(moveFactory)
                .internalUtil(internalUtil)
                .visibleMetrics(visibleMetrics)
                .lackOfMaterialMetrics(lackOfMaterialMetrics)
                .checkMetrics(checkMetrics)
                .checkmateMetrics(checkmateMetrics)
                .stalemateMetrics(stalemateMetrics)
                .pawnGenerator(pawnGenerator)
                .knightGenerator(knightGenerator)
                .bishopGenerator(bishopGenerator)
                .rookGenerator(rookGenerator)
                .queenGenerator(queenGenerator)
                .kingGenerator(kingGenerator)
                .bitboardGenerator(bitboardGenerator)
                .generator(generator)
                .build();
    }

    /**
     * {@link Generator} class is a singleton class. This method provides the
     * instance.
     * <p>
     * There is no state in this class, so all methods are thread-safe.
     * </p>
     * 
     * @return the {@link Generator} instance
     */
    public static Generator generator() {
        return container.generator;
    }

    /**
     * Takes a FEN string and returns a {@link Position} instance if the FEN is
     * valid.
     * 
     * @param fen the FEN string
     * @return a {@link Position} instance wrapped in an {@link Optional}
     */
    public static Optional<Position> safePosition(String fen) {
        return Optional.ofNullable(Rules.isValidFen(fen) ? new Position(fen) : null);
    }

    /**
     * Takes a FEN string and returns a {@link Position} instance
     * 
     * @param fen the FEN string
     * @return a {@link Position} instance
     */
    public static Position position(String fen) {
        return new Position(fen);
    }

    /**
     * Starter position.
     * 
     * @return the starter position
     */
    public static Position startPos() {
        return startPos;
    }

    /**
     * Creates a move from the origin and target square
     * 
     * @param origin the origin square, given as an integer in the 0-63 range
     * @param target the target square, given as an integer in the 0-63 range
     * @return a Move instance
     * @since 1.2.10
     */
    public static Move move(int origin, int target) {
        return container.moveFactory.move(origin, target);
    }

    /**
     * Creates a move from a bitboard representation and origin square.
     * 
     * @param move   the bitboard representation of the move
     * @param origin the origin square, given as an integer in the 0-63 range
     * @return a Move instance
     */
    public static Move move(long move, int origin) {
        return container.moveFactory.move(origin, Long.numberOfTrailingZeros(move));
    }

    /**
     * Creates a move from a bitboard representation, origin square, and promotion
     * piece.
     * 
     * @param move           the bitboard representation of the move
     * @param origin         the origin square, given as an integer in the 0-63
     *                       range
     * @param promotionPiece the promotion piece, given as the ordinal value of the
     *                       equivalent {@link Piece} or -1 for no promotion
     * @return a Move instance
     */

    public static Move move(long move, int origin, int promotionPiece) {
        return promotionPiece == -1 ? container.moveFactory.move(origin, Long.numberOfTrailingZeros(move))
                : container.moveFactory.move(origin, Long.numberOfTrailingZeros(move), promotionPiece);
    }

    /**
     * Creates a move from the origin and target square, and a promotion piece
     * 
     * @param origin         the origin square, given as an integer in the 0-63
     *                       range
     * @param target         the target square, given as an integer in the 0-63
     *                       range
     * @param promotionPiece the promotion piece, given as the ordinal value of the
     *                       equivalent {@link Piece} or -1 for no promotion
     * @return a Move instance
     * @since 1.2.10
     */
    public static Move move(int origin, int target, int promotionPiece) {
        return promotionPiece == -1 ? container.moveFactory.move(origin, target) : 
                container.moveFactory.move(origin, target, promotionPiece);
    }

    /**
     * Takes a move string (UCI notation) and a boolean indicating the player who
     * moves and return the move representation.
     *
     * @param move      the move string in UCI notation
     * @param whiteMove the player who moves
     * @return a Move instance
     */
    public static Move move(String move, boolean whiteMove) {
        return container.moveFactory.move(move, whiteMove);
    };

    /**
     * Creates a move from the origin and target square
     * 
     * @param origin the origin square
     * @param target the target square
     * @return a Move instance
     */
    public static Move move(Square origin, Square target) {
        return container.moveFactory.move(origin, target);
    }

    /**
     * Creates a move from the origin and target square
     * 
     * @param origin         the origin square
     * @param target         the target square
     * @param promotionPiece the promotion piece
     * @return a Move instance
     */
    public static Move move(Square origin, Square target, Piece promotionPiece) {
        return promotionPiece == Piece.EMPTY ? container.moveFactory.move(origin, target) : container.moveFactory.move(origin, target, promotionPiece);
    }

    /**
     * Takes a move string (SAN notation) and the position from which the move came
     * and returns
     * a {@link PGNMove} instance.
     * 
     * @param move     the move in SAN notation
     * @param position the position before the move
     * @return a PGNMove instance
     */
    public static PGNMove pgnMove(String move, Position position) {
        var moveObj = PGNHandler.toUCI(position, move).orElseThrow(
                () -> new MovementException(
                        String.format("move %s is not valid for position %s", move, position.fen())));
        return new PGNMove(
                moveObj.getOrigin(),
                moveObj.getTarget(),
                moveObj.getPromotionPiece(),
                position);
    }

    private Factory() {
    }

}
