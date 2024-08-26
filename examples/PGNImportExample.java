import chessapi4j.*;

import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;

public class PGNImportExample {
    public static void main(String[] args) throws IOException, NoSuchElementException {
        // Now let's see the reverse path, from a PGN-formatted game to obtaining a Game
        // object.
        // Then we'll examine this object.

        // Suppose we have a game in PGN format somewhere on our computer
        // In this example, we'll generate this file manually

        String pgnGame = "[Event \"All Russian-ch06 Amateur\"]\n" +
                "[Site \"St Petersburg\"]\n" +
                "[Date \"1909.03.09\"]\n" +
                "[Round \"17\"]\n" +
                "[White \"Rotlewi, Georg A\"]\n" +
                "[Black \"Alekhine, Alexander\"]\n" +
                "[Result \"0-1\"]\n" +
                "[ECO \"A40\"]\n" +
                "[PlyCount \"74\"]\n" +
                "[EventDate \"1909.02.15\"]\n" +
                "[EventType \"tourn\"]\n" +
                "[EventRounds \"19\"]\n" +
                "[EventCountry \"RUS\"]\n" +
                "[SourceTitle \"HCL\"]\n" +
                "[Source \"ChessBase\"]\n" +
                "[SourceDate \"1999.07.01\"]\n" +
                "[SourceVersion \"2\"]\n" +
                "[SourceVersionDate \"1999.07.01\"]\n" +
                "[SourceQuality \"1\"]\n" +
                "\n" +
                "1. d4 e6 2. c4 c5 3. e3 f5 4. Nc3 Nf6 5. Nf3 a6 6. a3 Qc7 7. dxc5 Bxc5 8. b4\n" +
                "Be7 9. Bb2 b6 10. Na4 d6 11. Rc1 O-O 12. Be2 Nbd7 13. O-O e5 14. Qb3 Kh8 15.\n" +
                "Ng5 Qc6 16. Ne6 Re8 17. Bf3 Ne4 18. Bxe4 fxe4 19. Rfd1 Nb8 20. c5 dxc5 21. Nf4\n" +
                "exf4 22. Qf7 Bf8 23. Nxc5 bxc5 24. Rxc5 Be6 25. Qxf4 Bxc5 26. Bxg7+ Kg8 27. Bb2\n" +
                "Bf7 28. bxc5 Qg6 29. Rd6 Be6 30. Qe5 Nd7 31. Qd4 Kf7 32. c6 Nf6 33. h3 Re7 34.\n" +
                "h4 Rg8 35. g3 Qf5 36. Qb6 Ng4 37. Rd2 Nxf2 0-1\n";

        BufferedWriter writer = Files.newBufferedWriter(Paths.get("example.pgn"), StandardOpenOption.CREATE);
        writer.write(pgnGame);
        writer.flush();
        writer.close();

        // Now simply call the parseGames method of the PGNHandler class to import this
        // file Note that this method is for PGN files that can have multiple games, hence it
        // returns a list

        Game game = PGNHandler.parseGames(Paths.get("example.pgn")).stream().findFirst().orElseThrow();

        // Let's see some positions from the game
        System.out.printf("%s\n", game.positionAt(13, Side.WHITE, true));
        System.out.printf("%s\n", game.positionAt(23, Side.BLACK, false));
        System.out.printf("%s\n", game.positionAt(31, Side.WHITE, false));
        System.out.printf("%s\n", game.positionAt(37, Side.BLACK, true));
    }
}