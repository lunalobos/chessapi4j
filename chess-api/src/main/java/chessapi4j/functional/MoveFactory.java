package chessapi4j.functional;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chessapi4j.MovementException;
import chessapi4j.Piece;
import chessapi4j.Square;
import chessapi4j.Util;

import static chessapi4j.Piece.*;

final class MoveFactory {
    private static final Logger logger = Factory.getLogger(MoveFactory.class);
    private static final Pattern MOVE_PATTERN = Pattern.compile(
            "(?<colOrigin>[a-h])(?<rowOrigin>[1-8])(?<colTarget>[a-h])(?<rowTarget>[1-8])(?<promotion>[nbrq])?");

    private static void checkCollision(Move move, Move[] moves) {
        Move m = moves[move.hashCode()];
        if (m != null && !m.equals(move)) {
            throw new RuntimeException(String.format("hash collision for %s and %s", m, move));
        }
    }

    private static int hash(int origin, int target) {
        return origin | (target << 6);
    }

    private static int hash(int origin, int target, int promotionPiece) {
        return origin | (target << 6) | (promotionPiece << 12);
    }
    /**
     * Array for hashing moves
     */
    private final Move[] moves;

    public MoveFactory() {
        var t1 = OffsetDateTime.now();
        final var moves = new BlockingList<Move>();

        var cols = new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };

        // white promotions
        final var white = new Piece[] { WQ, WR, WN, WB };

        Arrays.stream(cols)
                .map(String::toUpperCase)
                .flatMap(col -> {
                    final var origin = Square.valueOf(col + "7");
                    final var target = Square.valueOf(col + "8");
                    return Arrays.stream(white)
                            .map(piece -> new Move(origin, target, piece));
                }).forEach(moves::add);
        

        Arrays.stream(Arrays.copyOfRange(cols, 0, 7))
                .map(String::toUpperCase)
                .flatMap(col -> {
                    final var origin = Square.valueOf(col + "7");
                    final var target = Square.get(Square.valueOf(col + "8").ordinal() + 1);
                    return Arrays.stream(white)
                            .map(piece -> new Move(origin, target, piece));
                }).forEach(moves::add);

        Arrays.stream(Arrays.copyOfRange(cols, 1, 8))
                .map(String::toUpperCase)
                .flatMap(col -> {
                    final var origin = Square.valueOf(col + "7");
                    final var target = Square.get(Square.valueOf(col + "8").ordinal() -1);
                    return Arrays.stream(white)
                            .map(piece -> new Move(origin, target, piece));
                }).forEach(moves::add);
        
        // black promotions
        final var black = new Piece[] { BQ, BR, BN, BB };

        Arrays.stream(cols)
                .map(String::toUpperCase)
                .flatMap(col -> {
                    final var origin = Square.valueOf(col + "2");
                    final var target = Square.valueOf(col + "1");
                    return Arrays.stream(black)
                            .map(piece -> new Move(origin, target, piece));
                }).forEach(moves::add);
        
        Arrays.stream(Arrays.copyOfRange(cols, 0, 7))
                .map(String::toUpperCase)
                .flatMap(col -> {
                    final var origin = Square.valueOf(col + "2");
                    final var target = Square.get(Square.valueOf(col + "1").ordinal() + 1);
                    return Arrays.stream(black)
                            .map(piece -> new Move(origin, target, piece));
                }).forEach(moves::add);

        Arrays.stream(Arrays.copyOfRange(cols, 1, 8))
                .map(String::toUpperCase)
                .flatMap(col -> {
                    final var origin = Square.valueOf(col + "2");
                    final var target = Square.get(Square.valueOf(col + "1").ordinal() - 1);
                    return Arrays.stream(black)
                            .map(piece -> new Move(origin, target, piece));
                }).forEach(moves::add);

        // regular moves, some are not possible
        for (var i = 0; i < 64; i++) {
            for (var j = 0; j < 64; j++) {
                if (j != i) {
                    moves.add(new Move(Square.get(i), Square.get(j)));
                }

            }
        }
        // 16 bits are enough because we have 65535 hash combinations
        this.moves = new Move[65535];
        moves.forEach(move -> {
            checkCollision(move, this.moves);
            this.moves[move.hashCode()] = move;
        });
        var t2 = OffsetDateTime.now();
        logger.instantiation(t1, t2);
    }

    public Move move(int origin, int target) {
        return this.moves[hash(origin, target)];
    }

    public Move move(int origin, int target, int promotionPiece) {
        return this.moves[hash(origin, target, promotionPiece)];
    }

    public Move move(Square origin, Square target) {
        return this.moves[hash(origin.ordinal(), target.ordinal())];
    }

    public Move move(Square origin, Square target, Piece promotionPiece) {
        return this.moves[hash(origin.ordinal(), target.ordinal(), promotionPiece.ordinal())];
    }

    public Move move(String move, boolean whiteMove) {
        Matcher matcher = MOVE_PATTERN.matcher(move);
        if (matcher.find()) {
            int xOrigin = Util.getColIndex(matcher.group("colOrigin"));
            int yOrigin = Integer.parseInt(matcher.group("rowOrigin")) - 1;
            int xTarget = Util.getColIndex(matcher.group("colTarget"));
            int yTarget = Integer.parseInt(matcher.group("rowTarget")) - 1;
            int origin = Util.getSquareIndex(xOrigin, yOrigin);
            int target = Util.getSquareIndex(xTarget, yTarget);
            String promotionPiece = matcher.group("promotion");
            if (promotionPiece != null && !promotionPiece.isEmpty()) {
                String name = (whiteMove ? "W" : "B") + promotionPiece.toUpperCase();
                int promotion = Piece.valueOf(name).ordinal();
                return this.moves[hash(origin, target, promotion)];
            } else {
                return this.moves[hash(origin, target)];
            }
        } else {
            throw MovementException.invalidString(move);
        }
    };
}
