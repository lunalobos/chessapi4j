import chessapi4j.*;
import java.util.List;

public class MoveGenerationExample {
    public static void main(String[] args) {
        // Create a valid position.
        Position position = new Position("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");

        // Obtain an instance of Generator and calculate the positions derived from the position just created.
        List<Position> children = GeneratorFactory.instance().generateChildren(position);

        // If we manually evaluate this position, we can demonstrate that the number of derived positions is 46.
        System.out.printf("The number of derived positions %s 46\n", children.size() == 46 ? "is" : "is not");

        // Now let's generate the derived moves
        List<Move> moves = GeneratorFactory.instance().generateMoves(position, children);
        String formattedMoves = moves.stream().map(move -> move.toString() + ",").collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
        System.out.printf("The generated moves are: %s\n", formattedMoves.substring(0, formattedMoves.length() - 1)); 
    }
}