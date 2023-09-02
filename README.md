# Overview

ChessAPI4j is a Java library that provides a comprehensive set of classes and utilities for working with chess-related operations. It allows you to create, manipulate, and analyze chess positions, generate legal moves, and execute moves.

## Features

### Position Management
The `Position` interface represents a chess position and provides methods for accessing and modifying the board state. You can create an empty position, initialize a position from a FEN (Forsyth–Edwards Notation) string, or create a position based on a set of bitboards representing the board state. The `Position` interface also allows you to retrieve information about the current player to move, castling rights, en passant square, and more.

### Move Generation
The library includes generator classes that can generate legal moves. You can obtain a generator instance based on a given position using the `GeneratorFactory` class. The generated moves are represented by the `Move` interface.

### Play Execution
The `Play` interface represents a play in chess, which consists of a position and a move. It provides methods for executing the move and obtaining the resulting position. The `PlayFactory` class allows you to create play instances based on a position and a move.

### Factory Classes
To simplify the creation of positions, moves, and plays, the library provides factory classes. The `PositionFactory` class offers convenient methods to create instances of positions, including positions from FEN strings, and positions based on bitboards. The `MoveFactory` class provides methods to create move instances based on different criteria, such as origin and destination squares or UCI (Universal Chess Interface) notation. The `PlayFactory` class creates play instances based on a position and a move.

### Utility Functions
The `Util` class is an utility class that provides various helpful functions for chess operations. It includes methods to calculate visible squares for a given position and set of directions, convert between square indices and algebraic notation, check for coronation (promotion), and more. These utility functions can be used to enhance your chess-related logic and calculations.

### Importing and exporting games in PGN format
By using the Game class, it is possible to represent a game and then export it to PGN format. It is also possible to create a list of Game instances from a PGN file using the PGNHandler class.

## Documentation
The ChessAPI4j library comes with comprehensive [documentation](https://lunalobos.github.io/chessapi4j/chess-api/apidocs/index.html) that provides information about the classes, interfaces, and methods available. The documentation includes explanations of the different features and, in some cases, usage examples. 

## Contributing
Contributions to the ChessAPI4j library are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request on the GitHub repository. You can contribute to the library by adding new features, improving existing functionality, fixing bugs, or adding more documentation.

## License
The ChessAPI4j library is licensed under the Eclipse Public License - v 2.0. You are free to use, modify, and distribute the library according to the terms of this license.

## Getting Started

To use the ChessAPI4j library in your Java project, follow these steps:

1. Download the latest release.
2. Open a kernel and positionate in the chess-api directory.
```console
cd <chessapi4j_download_directory>\chess-api
```
3. Install the library in your local repository with maven. 
```console
mvn clean install
```
4. Include the dependency in your pom.

```
<dependency>
    <groupId>chessapi4j</groupId>
    <artifactId>chessapi4j</artifactId>
    <version>1.1.2-RELEASE</version>
</dependency>
```

Now you can import the classes in your code to start using the API.

Here's an example of how to create a position, generate legal moves, and execute a play:

```java
import chessapi4j.*;
import chessapi4j.core.*;

public class ChessGame {
    public static void main(String[] args) {
        // Create a new position
        Position position = PositionFactory.instance();
        
        // Generate legal moves for the current position
        Generator generator = GeneratorFactory.instance(position);
        List<Move> moves = generator.generateMoves();
        
        // Choose a move to play
        Move move = moves.get(0);
        
        // Create a play and execute the move
        Play play = PlayFactory.instance(generator, move);
        play.executeMove();
        
        // Get the resulting position
        Position newPosition = play.getPosition();
        System.out.println(newPosition.toString());
    }
}
```

## Author

This ChessAPI4j library is developed and maintained by me (https://github.com/lunalobos). Feel free to reach out to me at [lunalobosmiguel@gmail.com](mailto:lunalobosmiguel@gmail.com).
