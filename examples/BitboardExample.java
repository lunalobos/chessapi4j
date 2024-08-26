import chessapi4j.*;

public class BitboardExample {
    public static void main(String[] args) {
        // Create a bitboard with certain squares marked.
        Bitboard bitboard = new Bitboard(Square.B1, Square.C2, Square.E4);
        
        // Let's see this in the console, we should see that the squares specified in the constructor appear as an X.
        System.out.printf("%s\n", bitboard);

        // Now let's take a real-world example.
        Position position = new Position();
        Bitboard whitePawns = position.getBitboard(Piece.WP);
        
        // We should see the initial positions of the white pawns marked with an X.
        System.out.printf("%s\n", whitePawns);
    }
}