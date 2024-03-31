# Overview

ChessAPI4j is a Java library that allows representing and performing operations related to chess. With this library, it is possible to create representations of positions, generate legal moves, execute moves, detect moves between different positions, work with PGN files, use or implement heuristic evaluations of positions, and use or implement algorithms for searching the best move.

## Features

### Position Representation 
The Position class allows representing a position in a very comprehensive way. It provides methods to obtain a FEN representation with the toFen() method, as well as a graphical representation with the toString() method. Its empty constructor generates the initial position, and it also has a constructor that takes the FEN representation as an argument. A factory class is also provided that allows creating a Position object from a string that provides the FEN representation of an initial position followed by the moves token followed by the subsequent moves, analogous to the input requested by the UCI protocol.

Let's see an example:
```java
import chessapi4j.*;

public class Example {
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

Output:
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

### Some Notions on Board Representation
Internally, ChessAPI4j uses a bitboard representation. A bitboard is a 64-bit long integer (in Java, all long integers have 64 bits) where each bit represents the presence of a piece. At least 12 bitboards are necessary to represent the 12 different pieces on the board. The order goes from right to left of the integer, and on the board, each position of the integer (i.e., each power of 2) corresponds as follows:
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
The entire Chessapi4j library provides methods that take this as reference; it is very important to keep this in mind.

### Move Representation
The Move class provides a basic representation of a chess move. Among its properties are the origin and destination squares as well as the piece to promote if any. It does not provide information about the piece being moved or the position from which it starts as it is not necessary and generally leads to the creation of spaghetti code. A factory class is also provided for cases where a move needs to be created from the algebraic notation of the UCI protocol.
The PGNMove class is also provided for cases where classical algebraic notation functionalities and PGN format are required.

### Move Generation
The library includes a move generator called Generator. It provides two utility methods for move generation. On one hand, generateChildren(Position position) generates all positions derived from the position given as an argument. On the other hand, if we have already generated the child positions, the generateMoves(Position parent, List<Position> children) method returns the list of Move objects in the same order as the list of child positions. The methods of Generator are instance methods. To use an instance of this class, it is necessary to use the GeneratorFactory factory class. It is not possible to generate instances of Generator directly because this would result in unnecessary memory and CPU usage.

### Utility Classes
The Util and AdvanceUtil classes provide useful low-level methods for working with custom heuristic functions. Initially, I thought about making these classes not visible since they are not entirely safe, but I thought someone might find them useful.

### Working with PGN Base
A basic representation of a game in PGN format is provided with the Game class, as well as several subclasses to represent the characteristics of a game saved with this notation. The PGNHandler class allows manipulation of PGN databases.

### Heuristic Evaluation and Search
Interfaces and implementations of search and evaluation algorithms are also provided.

### Unit Testing
I have created a series of unit tests that ensure the characteristics specified by the library. In particular, the Generator class has been thoroughly tested, and I have tried to optimize it as much as possible to improve its speed.

## Documentation
ChessAPI4j provides basic [documentation](https://lunalobos.github.io/chessapi4j/chess-api/apidocs/index.html), which will clear up many of the doubts you may have.

## Contributing
I have not yet created rules for contributing. For now, each case will be analyzed individually without any fixed criteria. If you find bugs, please open an issue or write to my email.

## License
ChessAPI4j refers to the Apache-2.0 license for all legal aspects.

## Getting Started with ChessAPI4j

To use the library, you must have Java 8 or higher and Maven 3.8 or higher. You must clone this repository and then use Maven to install it.

## Author

This library is developed and maintained by me (https://github.com/lunalobos). Feel free to contact me at my email lunalobosmiguel@gmail.com.
