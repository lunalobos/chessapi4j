import chessapi4j.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class PGNInputExample {
    public static void main(String[] args) throws MovementException {
        // Let's demonstrate how, from input parameters, for example from a game in progress,
        // it's possible to obtain a PGN file.

        // Let's create the tags and other parameters for this example
        // Remember that all of these tags will come from some real input; there is no sense in manually creating them
        // except for demonstration purposes
        Tag event = new Tag("event", "FooTournament");
        Tag site = new Tag("site", "BarCity");
        Tag date = new Tag("date", "2024.04.17");
        Tag round = new Tag("round", "1");
        Tag white = new Tag("white", "foo");
        Tag black = new Tag("black", "bar");
        Tag result = new Tag("result", ""); // it is unknown yet
        Set<Tag> supplementalTags = new HashSet<>();
        List<PGNMove> moves = new ArrayList<>();

        // Let's create the game object
        Game game = new Game(event, site, date, round, white, black, result, supplementalTags, moves);
        
        // We can see that the initial position of this object is the initial position of the game
        Position initial = game.positionAt(1, Side.WHITE);
        System.out.printf("%s\n", initial);

        // Now let's add moves; please ignore the fact that we are playing the fool's mate,
        // the idea is to bring a quick result to export to PGN.
        Position current = game.addMove(MoveFactory.instance("e2e4", true));
        System.out.printf("%s\n", current);
        current = game.addMove(MoveFactory.instance("e7e5", false));
        System.out.printf("%s\n", current);
        current = game.addMove(MoveFactory.instance("f1c4", true));
        System.out.printf("%s\n", current);
        current = game.addMove(MoveFactory.instance("d7d6", false));
        System.out.printf("%s\n", current);
        current = game.addMove(MoveFactory.instance("d1h5", true));
        System.out.printf("%s\n", current);
        current = game.addMove(MoveFactory.instance("g8f6", false));
        System.out.printf("%s\n", current);
        current = game.addMove(MoveFactory.instance("h5f7", true)); // checkmate
        System.out.printf("%s\n", current);

        // Since white has won, we need to complete the result
        game.setResult(new Tag("result","1-0"));

        // The toString() method of the Game class is the game in PGN format
        String pgnGame = game.toString();
        System.out.printf("\nPGN format:\n%s\n", pgnGame);
    }
}