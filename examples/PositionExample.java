import chessapi4j.*;

public class PositionExample {
    public static void main(String[] args) {
        // Create a new initial position
        Position position = new Position();
        
        // See what the toFen() method returns
        System.out.println(position.toFen());
        
        System.out.println("----------------");

        // See what the toString() method returns
        System.out.println(position.toString());
    }
}