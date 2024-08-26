import chessapi4j.*;

public class MoveExample {
    public static void main(String[] args) throws MovementException{
        // Creating a Move object from UCI notation
        // This factory method requires the string with the move in UCI notation
        // and a boolean parameter that is true when the move was made by white
        // and false when it was made by black.
        Move move = MoveFactory.instance("e2e4", true);
        
        // Let's see the properties of this object
        System.out.printf("Origin square: %s\nTarget square: %s\nPromotion: %b\n", 
            move.origin().getName(), move.target().getName(), Util.isPromotion(move.target()));
        
        // Move instances also provide a bitboard representing the target square
        System.out.printf("Move bitboard representation:\n%s\n", move.bitboardMove());
    }
}