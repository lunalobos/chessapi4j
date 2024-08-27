# Overview

ChessAPI4j is a Java library that allows representing and performing operations related to chess. With this library, it is possible to create representations of positions, generate legal moves, execute moves, detect moves between different positions, work with PGN files (import and export), use or implement heuristic evaluations of positions, and use or implement algorithms for searching the best move.

## Table of contents
1. [Reference Documentation](#reference-documentation)
2. [Some Notions on Board Representation](#some-notions-on-board-representation)
2. [Features](#features)
    * [Position Representation](#position-representation)
    * [Bitboards](#bitboards)
    * [Move Representation](#move-representation)
    * [Move Generation](#move-generation)
    * [Utility Classes](#utility-classes)
    * [PGN handling](#pgn-handling)
    * [Heuristic Evaluation and Search](#heuristic-evaluation-and-search)
3. [Unit Testing](#unit-testing)
4. [Javadoc](#javadoc)
5. [Contributing](#contributing)
6. [License](#license)
7. [Getting Started with ChessAPI4j](#getting-started-with-chessapi4j)
8. [Author](#author)



## Reference Documentation 

The following links correspond to the historical documentation of several relevant concepts. I recommend at least skimming their content if you are not familiar with the FEN, UCI, and PGN standards.

* [FEN_1999_05_27](https://www.stmintz.com/ccc/index.php?id=53266)
* [PGN_standard_1994-03-12](https://ia902908.us.archive.org/26/items/pgn-standard-1994-03-12/PGN_standard_1994-03-12.txt)
* [UCI_April_2006](https://www.shredderchess.com/download/div/uci.zip)

You can see a copy of these files [here](https://github.com/lunalobos/chessapi4j/ref).

## Some Notions on Board Representation

Internally, ChessAPI4j uses a bitboard representation. A bitboard is a 64-bit long integer (in Java, all long integers have 64 bits) where each bit represents the presence of a piece or an relevant concept. At least 12 bitboards are necessary to represent the 12 different pieces on the board. The order goes from right to left of the integer, and on the board, each position of the integer (i.e., each power of 2) corresponds as follows:
```java
56 57 58 59 60 61 62 63
48 49 50 51 52 53 54 55
40 41 42 43 44 45 46 47
32 33 34 35 36 37 38 39
24 25 26 27 28 29 30 31
16 17 18 19 20 21 22 23
08 09 10 11 12 13 14 15
00 01 02 03 04 05 06 07
```
That is, position 0 (equivalent to saying 0b1L) corresponds to square a1, position 1 (0b10L) corresponds to square b1, and so on up to position 63 (1L << 63) which corresponds to square h8.
The entire ChessAPI4j library provides methods that take this as reference; it is very important to keep this in mind.

## Features

### Position Representation 

The Position class allows representing a position in a very comprehensive way. It provides methods to obtain a FEN representation with the toFen() method, as well as a graphical representation with the toString() method. Its empty constructor generates the initial position, and it also has a constructor that takes the FEN representation as an argument. A factory class is also provided that allows creating a Position object from a string that provides the FEN representation of an initial position followed by the moves token followed by the subsequent moves, analogous to the input requested by the UCI protocol.

Let's see an example:
```java
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
```
<details>

<summary>Output</summary>

```console
rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
----------------

+---+---+---+---+---+---+---+---+
| r | n | b | q | k | b | n | r | 8
+---+---+---+---+---+---+---+---+
| p | p | p | p | p | p | p | p | 7
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 5
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 4
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 3
+---+---+---+---+---+---+---+---+
| P | P | P | P | P | P | P | P | 2
+---+---+---+---+---+---+---+---+
| R | N | B | Q | K | B | N | R | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
```
</details>



### Bitboards
The library also brings a class to better exemplify the concept and use of bitboards. This class may have internal use in the future, but for now, it serves to demonstrate various concepts and exemplify several utilities. Let's see some code about it: 

```java
import chessapi4j.*;

public class BitboardExample {
    public static void main(String[] args) {
        // Create a bitboard with certain squares marked.
        Bitboard bitboard = new Bitboard(Square.B1, Square.C2, Square.E4);
        
        // Let's see this in the console, we should see that the squares specified in the constructor appear as a 1.
        System.out.printf("%s\n", bitboard);

        // Now let's take a real-world example.
        Position position = new Position();
        Bitboard whitePawns = position.getBitboard(Piece.WP);
        
        // We should see the initial positions of the white pawns marked with a 1.
        System.out.printf("%s\n", whitePawns);
    }
}
```

<details>

<summary>Output</summary>

```console
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 1 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 1 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+


+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
```
</details>

### Move Representation
The Move class provides a basic representation of a chess move. Among its properties are the origin and destination squares as well as the piece to promote if any. It does not provide information about the piece being moved or the position from which it starts as it is not necessary and generally leads to the creation of spaghetti code. A factory class is also provided for cases where a move needs to be created from the algebraic notation of the UCI protocol.
The PGNMove class is also provided for cases where classical algebraic notation functionalities and PGN format are required.

Let's see an example:

```java
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
```

<details>

<summary>Output</summary>

```console
Origin square: e2
Target square: e4
Promotion: false
Move bitboard representation:

+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 1 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
```
</details>

### Move Generation
The library includes a move generator. It provides two utility methods for move generation. On one hand, "generateChildren" generates all positions derived from the position given as an argument. On the other hand, if we have already generated the child positions, the "generateMoves" method returns the list of Move objects in the same order as the list of child positions given as an argument. The methods of Generator are instance methods. To use an instance of this class, it is necessary to use the GeneratorFactory factory class.

Let's see this feature in action:
```java
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
        String formattedMoves = moves.stream()
                .map(move -> move.toString() + ",")
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        System.out.printf("The generated moves are: %s\n", formattedMoves.substring(0, formattedMoves.length() - 1)); 
    }
}
```

<details>

<summary>Output</summary>

```console
The number of derived positions is 46:
The generated moves are: b2b3,b2b4,g2g3,h2h3,h2h4,a3a4,d3d4,c3b1,c3d1,c3a2,c3a4,c3b5,c3d5,f3e1,f3d2,f3d4,f3h4,f3e5,c4a2,c4b3,c4b5,c4d5,c4a6,c4e6,c4f7,g5c1,g5d2,g5e3,g5f4,g5h4,g5f6,g5h6,a1b1,a1c1,a1d1,a1e1,a1a2,f1b1,f1c1,f1d1,f1e1,e2d1,e2e1,e2d2,e2e3,g1h1
```
</details>

### Utility Classes
The Util and AdvanceUtil classes provide useful low-level methods for working with custom heuristic functions. Initially, I thought about making these classes not visible since they are not entirely safe, but I thought someone might find them useful.

Let's see some useful things from these classes:

```java
import chessapi4j.*;
import java.util.NoSuchElementException;

public class UtilExample {
    public static void main(String[] args) throws NoSuchElementException {
        // Create a valid position.
        final Position position = new Position("3q1r2/2p3kp/r2p1npQ/p1nPp3/1p2P1P1/5P2/PPP5/1NKR2NR b - - 1 17");

        // Let's use the Util class to check if white, the playing side, is in check (which it is).
        boolean inCheck = Util.isInCheck(position);
        System.out.printf("The position %s is %s check.\n", position.toFen(), inCheck ? "in" : "not in");

        // Let's check the visible squares for the white queen
        long visibleWQSquares = Util.visibleSquares(position, position.getSquares(Piece.WQ).stream().findFirst().orElseThrow());

        // Note that in this case, the visible squares do not include enemy pieces, and that's because white pieces 
        // don't move. This is a behavior of this method; only enemy pieces are visible if it's our turn.
        System.out.printf("Visible squares for the white queen:\n%s\n", new Bitboard(visibleWQSquares));
    }
}
```
<details>

<summary>Output</summary>

```console
The position 3q1r2/2p3kp/r2p1npQ/p1nPp3/1p2P1P1/5P2/PPP5/1NKR2NR b - - 1 17 is in check.

+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 0 | 1 | 1 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 0 | 1 | 0 | 1 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 0 | 1 | 0 | 0 | 1 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 0 | 1 | 0 | 0 | 0 | 1 |
+---+---+---+---+---+---+---+---+
| 0 | 0 | 1 | 0 | 0 | 0 | 0 | 1 |
+---+---+---+---+---+---+---+---+
```
</details>

### PGN handling
A basic representation of a game in PGN format is provided with the Game class, as well as several subclasses to represent the characteristics of a game saved with this notation. The PGNHandler class allows manipulation of PGN databases.

If you need to implement PGN file handling functionality, these classes are suitable.

**PGNMove**: Inherits from Move, adding properties for annotations, comments, and variations. toString() method returns the SAN notation format.

**Game**: Represents a game and contains the data that every game saved in PGN format possesses. Among its properties are the main tags (event, site, date, round, white, black, result) and supplementary tags (all represented with the Tag class), as well as a list of moves represented with the PGNMove class. This class can be used to model a real-time game and then export it to PGN format using the toString method.

**PGNHandler**: The main function of this class is to provide static methods for handling PGN notations and files. Regarding the latter, this class allows obtaining a list of Game objects from a PGN file.

Let's see a few examples:

```java

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

        // Since white has won, we need to override the result
        game.setResult(new Tag("result","1-0"));

        // The toString() method of the Game class is the game in PGN format
        String pgnGame = game.toString();
        System.out.printf("\nPGN format:\n%s\n", pgnGame);
    }
}

```

<details>

<summary>Output</summary>

```console

+---+---+---+---+---+---+---+---+
| r | n | b | q | k | b | n | r | 8
+---+---+---+---+---+---+---+---+
| p | p | p | p | p | p | p | p | 7
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 5
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 4
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 3
+---+---+---+---+---+---+---+---+
| P | P | P | P | P | P | P | P | 2
+---+---+---+---+---+---+---+---+
| R | N | B | Q | K | B | N | R | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1

+---+---+---+---+---+---+---+---+
| r | n | b | q | k | b | n | r | 8
+---+---+---+---+---+---+---+---+
| p | p | p | p | p | p | p | p | 7
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 5
+---+---+---+---+---+---+---+---+
|   |   |   |   | P |   |   |   | 4
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 3
+---+---+---+---+---+---+---+---+
| P | P | P | P |   | P | P | P | 2
+---+---+---+---+---+---+---+---+
| R | N | B | Q | K | B | N | R | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1

+---+---+---+---+---+---+---+---+
| r | n | b | q | k | b | n | r | 8
+---+---+---+---+---+---+---+---+
| p | p | p | p |   | p | p | p | 7
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   | p |   |   |   | 5
+---+---+---+---+---+---+---+---+
|   |   |   |   | P |   |   |   | 4
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 3
+---+---+---+---+---+---+---+---+
| P | P | P | P |   | P | P | P | 2
+---+---+---+---+---+---+---+---+
| R | N | B | Q | K | B | N | R | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2

+---+---+---+---+---+---+---+---+
| r | n | b | q | k | b | n | r | 8
+---+---+---+---+---+---+---+---+
| p | p | p | p |   | p | p | p | 7
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   | p |   |   |   | 5
+---+---+---+---+---+---+---+---+
|   |   | B |   | P |   |   |   | 4
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 3
+---+---+---+---+---+---+---+---+
| P | P | P | P |   | P | P | P | 2
+---+---+---+---+---+---+---+---+
| R | N | B | Q | K |   | N | R | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnbqkbnr/pppp1ppp/8/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR b KQkq - 1 2

+---+---+---+---+---+---+---+---+
| r | n | b | q | k | b | n | r | 8
+---+---+---+---+---+---+---+---+
| p | p | p |   |   | p | p | p | 7
+---+---+---+---+---+---+---+---+
|   |   |   | p |   |   |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   | p |   |   |   | 5
+---+---+---+---+---+---+---+---+
|   |   | B |   | P |   |   |   | 4
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 3
+---+---+---+---+---+---+---+---+
| P | P | P | P |   | P | P | P | 2
+---+---+---+---+---+---+---+---+
| R | N | B | Q | K |   | N | R | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnbqkbnr/ppp2ppp/3p4/4p3/2B1P3/8/PPPP1PPP/RNBQK1NR w KQkq - 0 3

+---+---+---+---+---+---+---+---+
| r | n | b | q | k | b | n | r | 8
+---+---+---+---+---+---+---+---+
| p | p | p |   |   | p | p | p | 7
+---+---+---+---+---+---+---+---+
|   |   |   | p |   |   |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   | p |   |   | Q | 5
+---+---+---+---+---+---+---+---+
|   |   | B |   | P |   |   |   | 4
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 3
+---+---+---+---+---+---+---+---+
| P | P | P | P |   | P | P | P | 2
+---+---+---+---+---+---+---+---+
| R | N | B |   | K |   | N | R | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnbqkbnr/ppp2ppp/3p4/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 1 3

+---+---+---+---+---+---+---+---+
| r | n | b | q | k | b |   | r | 8
+---+---+---+---+---+---+---+---+
| p | p | p |   |   | p | p | p | 7
+---+---+---+---+---+---+---+---+
|   |   |   | p |   | n |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   | p |   |   | Q | 5
+---+---+---+---+---+---+---+---+
|   |   | B |   | P |   |   |   | 4
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 3
+---+---+---+---+---+---+---+---+
| P | P | P | P |   | P | P | P | 2
+---+---+---+---+---+---+---+---+
| R | N | B |   | K |   | N | R | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnbqkb1r/ppp2ppp/3p1n2/4p2Q/2B1P3/8/PPPP1PPP/RNB1K1NR w KQkq - 2 4

+---+---+---+---+---+---+---+---+
| r | n | b | q | k | b |   | r | 8
+---+---+---+---+---+---+---+---+
| p | p | p |   |   | Q | p | p | 7
+---+---+---+---+---+---+---+---+
|   |   |   | p |   | n |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   | p |   |   |   | 5
+---+---+---+---+---+---+---+---+
|   |   | B |   | P |   |   |   | 4
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   |   |   | 3
+---+---+---+---+---+---+---+---+
| P | P | P | P |   | P | P | P | 2
+---+---+---+---+---+---+---+---+
| R | N | B |   | K |   | N | R | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnbqkb1r/ppp2Qpp/3p1n2/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 4

PGN format:

[event "FooTournament"]
[site "BarCity"]
[date "2024.04.17"]
[round "1"]
[white "foo"]
[balck "bar"]
[result "1-0"]

1. e4 e5 2. Bc4 d6 3. Qh5 Nf6 4. Qxf7# 1-0

```

</details>

```java

import chessapi4j.*;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;

public class PGNImportExample {
    public static void main(String[] args) throws IOException, NoSuchElementException {
        // Now let's see the reverse path, from a PGN-formatted game to obtaining a Game object.
        // Then we'll examine this object.

        // Let suppose we have a game in PGN format somewhere on our computer
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
                "h4 Rg8 35. g3 Qf5 36. Qb6 Ng4 37. Rd2 Nxf2 0-1";

        BufferedWriter writer = Files.newBufferedWriter(Paths.get("example.pgn"), StandardOpenOption.CREATE);
        writer.write(pgnGame);

        // Now simply call the parseGames method of the PGNHandler class to import this file
        // Note that this method is for PGN files that can have multiple games, hence it returns
        // a list
        Game game = PGNHandler.parseGames(Paths.get("example.pgn")).stream().findFirst().orElseThrow();

        // Let's see some positions from the game
        System.out.printf("%s\n", game.positionAt(13, Side.WHITE));
        System.out.printf("%s\n", game.positionAt(23, Side.BLACK));
        System.out.printf("%s\n", game.positionAt(31, Side.WHITE));
        System.out.printf("%s\n", game.positionAt(37, Side.BLACK));
    }
}

```

<details>

<summary>Output</summary>

+---+---+---+---+---+---+---+---+
| r |   | b |   |   | r | k |   | 8
+---+---+---+---+---+---+---+---+
|   |   | q | n | b |   | p | p | 7
+---+---+---+---+---+---+---+---+
| p | p |   | p | p | n |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   |   | p |   |   | 5
+---+---+---+---+---+---+---+---+
| N | P | P |   |   |   |   |   | 4
+---+---+---+---+---+---+---+---+
| P |   |   |   | P | N |   |   | 3
+---+---+---+---+---+---+---+---+
|   | B |   |   | B | P | P | P | 2
+---+---+---+---+---+---+---+---+
|   |   | R | Q |   | R | K |   | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: r1b2rk1/2qnb1pp/pp1ppn2/5p2/NPP5/P3PN2/1B2BPPP/2RQ1RK1 b K - 5 13

+---+---+---+---+---+---+---+---+
| r | n | b |   | r | b |   | k | 8
+---+---+---+---+---+---+---+---+
|   |   |   |   |   | Q | p | p | 7
+---+---+---+---+---+---+---+---+
| p | p | q |   |   |   |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   | N |   |   |   |   |   | 5
+---+---+---+---+---+---+---+---+
|   | P |   |   | p | p |   |   | 4
+---+---+---+---+---+---+---+---+
| P |   |   |   | P |   |   |   | 3
+---+---+---+---+---+---+---+---+
|   | B |   |   |   | P | P | P | 2
+---+---+---+---+---+---+---+---+
|   |   | R | R |   |   | K |   | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: rnb1rb1k/5Qpp/ppq5/2N5/1P2pp2/P3P3/1B3PPP/2RR2K1 b - - 0 23

+---+---+---+---+---+---+---+---+
| r |   |   |   | r |   | k |   | 8
+---+---+---+---+---+---+---+---+
|   |   |   | n |   |   |   | p | 7
+---+---+---+---+---+---+---+---+
| p |   |   | R | b |   | q |   | 6
+---+---+---+---+---+---+---+---+
|   |   | P |   | Q |   |   |   | 5
+---+---+---+---+---+---+---+---+
|   |   |   |   | p |   |   |   | 4
+---+---+---+---+---+---+---+---+
| P |   |   |   | P |   |   |   | 3
+---+---+---+---+---+---+---+---+
|   | B |   |   |   | P | P | P | 2
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   | K |   | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: r3r1k1/3n3p/p2Rb1q1/2P1Q3/4p3/P3P3/1B3PPP/6K1 w - - 5 31

+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   | r |   | 8
+---+---+---+---+---+---+---+---+
|   |   |   |   | r | k |   | p | 7
+---+---+---+---+---+---+---+---+
| p | Q | P |   | b |   |   |   | 6
+---+---+---+---+---+---+---+---+
|   |   |   |   |   | q |   |   | 5
+---+---+---+---+---+---+---+---+
|   |   |   |   | p |   |   | P | 4
+---+---+---+---+---+---+---+---+
| P |   |   |   | P |   | P |   | 3
+---+---+---+---+---+---+---+---+
|   | B |   | R |   | n |   |   | 2
+---+---+---+---+---+---+---+---+
|   |   |   |   |   |   | K |   | 1
+---+---+---+---+---+---+---+---+
  a   b   c   d   e   f   g   h
Fen: 6r1/4rk1p/pQP1b3/5q2/4p2P/P3P1P1/1B1R1n2/6K1 w - - 0 38

</details>

### Heuristic Evaluation and Search
This library offers interfaces and implementations for various search and evaluation algorithms. While an implementation is provided, these feature is still experimental.

## Unit Testing
I have created a series of unit tests that ensure the characteristics specified by the library. In particular, the Generator class has been thoroughly tested, and I have tried to optimize it as much as possible to improve its speed. PGNHandler also has been tested.

## Javadoc
ChessAPI4j provides basic [javadoc](https://lunalobos.github.io/chessapi4j/apidocs/chessapi4j/package-summary.html), which will clear up many of the doubts you may have.

## Contributing
I have not yet created rules for contributing. For now, each case will be analyzed individually without any fixed criteria. If you find bugs, please open an issue or write to my email.

## License
ChessAPI4j is licensed under the Apache License, Version 2.0 ("License"). You may use, modify, and distribute this software according to the terms and conditions outlined in the License.

## Getting Started with ChessAPI4j

To use the library you need Java 21 or higher and Maven 3.8 or higher. Start by cloning this repository and then using Maven to install it.

### Step 1: Clone the repository

```console

$ git clone git@github.com:lunalobos/chessapi4j.git

```

### Step 2: Verify your Maven and Java versions

I recommend use [Semeru jDK](https://developer.ibm.com/languages/java/semeru-runtimes/downloads/) and downloading Maven from [here](https://maven.apache.org/download.cgi).

```console

$ mvn -version
Apache Maven 3.9.6 (bc0240f3c744dd6b6ec2920b3cd08dcc295161ae)
Maven home: C:\apache-maven-3.9.6
Java version: 21.0.4, vendor: IBM Corporation, runtime: C:\semeru-jdk-21.0.4+7
Default locale: es_ES, platform encoding: UTF-8
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"

$ java -version
openjdk version "21.0.4" 2024-07-16 LTS
IBM Semeru Runtime Open Edition 21.0.4.0 (build 21.0.4+7-LTS)
Eclipse OpenJ9 VM 21.0.4.0 (build openj9-0.46.0, JRE 21 Windows 10 amd64-64-Bit Compressed References 20240716_229 (JIT enabled, AOT enabled)
OpenJ9   - 1a6f6128aa
OMR      - 840a9adba
JCL      - 7d844187b25 based on jdk-21.0.4+7)

```

### Step 3: Build and install the artifact

Navigate to the cloned repository and install the artifact using Maven.

```console

$ cd chessapi4j
$ mvn clean install

```

### Step 4: Add the dependency to your project

Now, you can import the artifact into your project. Add the following to your dependencies:

```xml

<dependency>
   <groupId>chessapi4j</groupId>
   <artifactId>chessapi4j</artifactId>
   <version>1.2.3-RELEASE</version>
</dependency>

```

I hope I can publish this artifact to the Maven repository soon.

## Author

This library is developed and maintained by me (https://github.com/lunalobos). Feel free to contact me at my email lunalobosmiguel@gmail.com.
