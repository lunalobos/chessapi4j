![Java](https://img.shields.io/badge/Java-11%2B-green)
[![Static Badge](https://img.shields.io/badge/javadoc-1.2.10-brightgreen)](https://lunalobos.github.io/chessapi4j/apidocs/chessapi4j/package-summary.html)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lunalobos/chessapi4j)](https://central.sonatype.com/artifact/io.github.lunalobos/chessapi4j)
![License](https://img.shields.io/github/license/lunalobos/chessapi4j)

![chessapi4j](https://chessapi4j.my.canva.site/media/7547b1586c5d91c735c3b3a67838eb13.png)

# Overview

This library aims to provide a solid foundation for handling abstractions of chess concepts such as board positions, determination of the state of a position (checkmate, stalemate, insufficient material, etc.), games between players (either in progress or already played), legal move generation and validation, FEN validation, ECO code determination, and conversion to and from PGN format.


> **⚠️ Java 11 Compatibility Note:**  
> Full compatibility with Java 11 is ensured starting from version **1.2.10**. However, it is **strongly recommended** to use the `functional` package when working with Java 11, as the original package is significantly slower in this environment (the cause is currently unknown).


> **⚠️ Java 17 Compatibility Note:** 
> There do not appear to be performance issues with the original package when running on Java 17. Nevertheless, it is still recommended to use the `functional` package, as it is generally more robust and safer for concurrent or complex applications.

## Table of contents

1. [Getting Started with ChessAPI4j](#getting-started-with-chessapi4j)
2. [Reference Documentation](#reference-documentation)
3. [Position Representation](#position-representation)
4. [Move representation and move generation](#move-representation-and-move-generation)
5. [PGN handling](#pgn-handling)
6. [Other Features](#other-features)
   - [Utility Classes](#utility-classes)
   - [Heuristic Evaluation and Search](#heuristic-evaluation-and-search)
7. [Functional Package (since 1.2.9)](#sparkles-functional-package)
    - [Position](#position)
    - [Generator](#generator)
    - [Game](#game)
    - [PGNHandler](#pgnhandler)
    - [Move and PGNMove](#move-and-pgnmove)
7. [Unit Testing](#unit-testing)
8. [Javadoc](#javadoc)
9. [Contributing](#contributing)
10. [License](#license)
11. [Author](#author)

## :rocket: Getting Started with ChessAPI4j

To use the library you need Java 11 or higher and Maven 3.8 or higher. Start by cloning this repository and then using Maven to install it.

### Step 1: Verify your Maven and Java versions

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

### Step 2: Add the dependency to your project

Add the following to your dependencies:

```xml

<dependency>
    <groupId>io.github.lunalobos</groupId>
    <artifactId>chessapi4j</artifactId>
    <version>1.2.10</version>
</dependency>

```

## :books: Reference Documentation

The following links correspond to the historical documentation of several relevant concepts. I recommend at least skimming their content if you are not familiar with the FEN, UCI, and PGN standards.

- [FEN_1999_05_27](https://www.stmintz.com/ccc/index.php?id=53266)
- [PGN_standard_1994-03-12](https://ia902908.us.archive.org/26/items/pgn-standard-1994-03-12/PGN_standard_1994-03-12.txt)
- [UCI_April_2006](https://www.shredderchess.com/download/div/uci.zip)

You can see a copy of these files [here](https://github.com/lunalobos/chessapi4j/tree/master/ref).

## :chess_pawn: Position Representation

The Position class allows representing a position in a very comprehensive way. Its empty constructor generates the initial position, and it also has a constructor that takes the FEN representation as an argument.

```java

    // start position
    var startpos = new Position();

    // custom position
    var customPos = new Position("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");

```

Methods to obtain a FEN representation as well as a graphical representation are providesd by this class.

```java
    var position = new Position();
    var fen  = position.toFen(); // fen string: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
    var positionRep = position.toString(); // graphical representation
```

A factory class is also provided that allows creating a Position object from a string that provides the FEN representation of an initial position followed by the moves token followed by the subsequent moves, analogous to the input requested by the UCI protocol.

```java
    // a position after the initial moves of petrov's defense
    var petrov = PositionFactory.fromMoves("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1 moves e2e4 e7e5 g1f3 g8f6");
```

The secureInstance method belongs to the PositionFactory class and acts as a safe factory method for creating Position instances from a FEN string. If the FEN string is valid, it returns an Optional containing the Position object. Otherwise, it returns an empty Optional.

```java
// startpos fen
var fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
var positionOpt = PositionFactory.secureInstance(fen);

positionOpt.ifPresentOrElse(
    position -> System.out.println("Valid position created!"),
    () -> System.out.println("Invalid FEN string, no position created.")
);

```

The isValidFen method is part of the Rules class and is responsible for checking whether a given FEN (Forsyth-Edwards Notation) string represents a legal chess position. This method doesn't verify if the position could be reached through valid moves in a real game; it only ensures that the basic rules are followed.

```java

var fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
var isLegalPosition = Rules.isValidFen(fen);

if (isLegalPosition) {
    System.out.println("The FEN represents a legal position.");
} else {
    System.out.println("The FEN does not represent a legal position.");
}

```

## :chess_pawn: Move representation and move generation

### Move class

The Move class provides a basic representation of a chess move. Among its properties are the origin and destination squares as well as the piece to promote if any. It does not provide information about the piece being moved or the position from which it starts.

```java
    // move object
    var move = new Move(Square.G1, Square.G2);
```

A factory class is also provided for cases where a move needs to be created from the algebraic notation of the UCI protocol.

```java
    try {
      var isWhiteMove = true; // necesary for promotions
      var moveUciNotation = "e2e4";
      var move = MoveFactory.instance(moveUciNotation, isWhiteMove);
    } catch (MovementException e){
      e.printStackTarce();
    }

```

The PGNMove class extends Move and is provided for cases where classical algebraic notation functionalities and PGN format are required. The toString method change between Move and PGNMove.

```java
    var move = new Move(Square.G1, Square.F3);
    var startpos = new Position();
    // a pgn move needs to know the position previous to the move
    var pgnMove = new PGNMove(move, startpos);
    var moveUCINotation = move.toString(); // g1f3
    var moveStandardNotation = pgnMove.toString(); // Nf3
```

### Move Generation

The library includes a move generator. It provides two utility methods for move generation. On one hand, "generateChildren" generates all positions derived from the position given as an argument. On the other hand, if we have already generated the child positions, the "generateMoves" method returns the list of Move objects in the same order as the list of child positions given as an argument. The methods of Generator are instance methods. To use an instance of this class, it is necessary to use the GeneratorFactory factory class.

Let's see this feature in action:

```java

    // creates a position
    var position = PositionFactory.secureInstance("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10").orElseThrow();
    // calculate the positions derived from the position just created.
    var children = GeneratorFactory.instance().generateChildren(position);

    var childrenCount = children.size(); // 46

    // creates the list of object moves
    var moves = GeneratorFactory.instance().generateMoves(position, children);
    // moves: b2b3, b2b4, g2g3, h2h3, h2h4, a3a4, d3d4, c3b1,..., g1h1


```

## :chess_pawn: PGN handling

A basic representation of a game in PGN format is provided with the Game class, as well as several subclasses to represent the characteristics of a game saved with this notation. The PGNHandler class allows manipulation of PGN databases.

If you need to implement PGN file handling functionality, these classes are suitable.

**PGNMove**: Inherits from Move, adding properties for annotations, comments, and variations. toString() method returns the SAN notation format.

**Game**: Represents a game and contains the data that every game saved in PGN format possesses. Among its properties are the main tags (event, site, date, round, white, black, result) and supplementary tags (all represented with the Tag class), as well as a list of moves represented with the PGNMove class. This class can be used to model a real-time game and then export it to PGN format using the toString method.

```java

    // create the tags and other parameters
    var event = new Tag("event", "FooTournament");
    var site = new Tag("site", "BarCity");
    var date = new Tag("date", "2001.12.20");
    var round = new Tag("round", "1");
    var white = new Tag("white", "foo");
    var black = new Tag("black", "bar");
    var result = new Tag("result", ""); // it is unknown yet
    Set<Tag> supplementalTags = new HashSet<>();
    List<PGNMove> moves = new ArrayList<>();

    // game object
    final var game = new Game(event, site, date, round, white, black, result, supplementalTags, moves);

    // We can see that the initial position of this object is the initial position of the game
    var initial = game.positionAt(1, Side.WHITE);
    System.out.printf("%s\n", initial);

    // add moves
    Stream.of(
      new Move(Square.E2, Square.E4), // 1. e4
      new Move(Square.E7, Square.E5), // 1... e5
      new Move(Square.F1, Square.C4), // 2. Bc4
      new Move(Square.D7, Square.D6), // 2... d6
      new Move(Square.D1, Square.H5), // 3. Qh5
      new Move(Square.G8, Square.F6), // 3... Nf6??
      new Move(Square.H5, Square.F7)  // 4. Qxf7#
      ).forEach(game::addMove);

    var isCheckmate = game.currentPosition().isCheckmate(); // true
    var result = game.getTagValue("RESULT").orElseThrow(); // "1-0"
    // The toString() method of the Game class is the game in PGN format
    var pgnGame = game.toString();
    /*
    * [event "FooTournament"]
    * [site "BarCity"]
    * [date "2024.04.17"]
    * [round "1"]
    * [white "foo"]
    * [balck "bar"]
    * [result "1-0"]
    * 1. e4 e5 2. Bc4 d6 3. Qh5 Nf6 4. Qxf7# 1-0
    */

```

From an instance of the game class you can obtain the ECO code.

```java
    var eco = game.getEcoDescriptor().getEco(); // ECO code
    var opening = game.getEcoDescriptor().getName(); // opening name
```

**PGNHandler**: The main function of this class is to provide static methods for handling PGN notations and files. Regarding the latter, this class allows obtaining a list of Game objects from a PGN file.

```java

    // supose you've got a pgn file with several games
    var path = "pgnGames.pgn";
    // you can just import then like this
    var games = PGNHandler.parseGames(Paths.get(path));// a list of game objects

```

## :chess_pawn: Other Features

### Utility Classes

The Util and AdvanceUtil classes provide useful methods for working with custom heuristic functions. Initially, I thought about making these classes not visible since they are not entirely safe, but I thought someone might find them useful.

### Heuristic Evaluation and Search

This library offers interfaces and implementations for various search and evaluation algorithms. While an implementation is provided, these feature is still experimental.

## :sparkles: Functional Package

Introduced in version 1.2.9, the functional package presents a new generation of classes for working with chess. It is focused on immutability, safety, and robustness. All main classes in this package provide unmodifiable (immutable) instances by default, making them thread-safe and especially suitable for concurrent or multithreaded environments.

### Position

The Position class in the functional package allows for representing chess positions immutably. Once an instance is created, its state cannot be modified, ensuring that the position data remains consistent and safe in any context—even when shared across multiple threads. This class provides a zobristHash() method that returns the corresponding Zobrist hash value as a long (i.e., 64 bits), which is the minimum acceptable length for a hash of this kind. However, Java has a limitation in that it does not allow arrays with lengths over 32 bits, so implementing hash tables for positions becomes a bit more complex.

```java
import chessapi4j.functional.Factory;
...
// You can always use the same startpos instance because it is immutable,
// and therefore thread-safe.
var startpos = Factory.startpos();

// If you need a new instance of this class, you can call the children method
// to get its derived positions from legal moves.
var children = startpos.children();

// You can create new positions just by using the move method
// in one of its available versions.
var position = startpos.move("g1f3"); // King's Knight Opening

// You can also use standard algebraic notation.
var nextPosition = startpos.sanMove("d5"); // King's Knight Opening again

// It is also suitable for constructing a position from several moves.
var finalPosition = startpos
        .sanMove("e4")
        .sanMove("e5")
        .sanMove("Nf3")
        .sanMove("Nc6")
        .sanMove("Bb5"); // Ruy Lopez Opening

// You may want to store this position after a heuristic evaluation in
// your hash table implementation. You can use the zobristHash() method
// to fulfill the hashing contract of your implementation.
var hash = finalPosition.zobristHash(); // this is a long value

```

### Generator

The Generator class for move generation offers the same functionality as in the main package. Although it is currently about 30% slower than the original version due to the creation of move objects (the computational cost of instantiating more objects), the advantage of this package does not lie in performance yet, but rather in a more solid programmatic flow, which leads to simpler system design.

Since the Position class provides the children method, it's not necessary to use this class directly, although it is available through the generator method in Factory.

### Game

The Game class in the functional package enables the creation of chess game representations that can be either immutable (when the result is defined) or mutable (when the game is ongoing). Its setter methods are synchronized, allowing game modifications in multithreaded environments to be handled much more reliably than with the original Game class. This makes it an ideal choice for servers, concurrent analysis, or any application where data safety is a top priority. This class can detect when the game is over and set the result tag to the corresponding value.

```java

import chessapi4j.functional.Game;
...
// lets create a open to modification instance
var game = Game.builder()
        .event("DSB Kongress-16 Hauptturnier A")
        .site("Duesseldorf")
        .date("1908.08.14")
        .round("11")
        .white("Lasker, Edward")
        .black("Alekhine, Alexander")
        .repetitionsMode(Game.RepetitionsMode.AWARE)
        .build();
// you can add the game moves in a simple way
game.sanMove("e4")
    .sanMove("e5")
    .sanMove("Nf3")
    .sanMove("d6")
    ... // until the game is over
// you can see de eco code like in the original class
var ecoCode = game.getEcoDescriptor().getEco();

```

### PGNHandler

The PGNHandler class has the same functionality as in the original version, but it's implemented for the new classes in the package.
```java
import chessapi4j.functional.PGNHandler;
...
// you need to provide a input stream with the pgn database
var is = ... // some input stream instatiation
// this will convert the original database to a game list, a classic representation
var games = PGNHandler.parseGames(is);

```

### Move and PGNMove
The Move and PGNMove classes are almost identical to their original versions, but they are immutable. This is important to keep in mind for PGNMove, which must be instantiated all at once rather than in parts. In general, developers don't construct it manually, but it's also possible to instantiate a PGNMove using the basic parameters: target square, origin square, promotion piece, and origin position.

## :white_check_mark: Unit Testing

I have created a series of unit tests that ensure the characteristics specified by the library. In particular, the Generator class has been thoroughly tested, and I have tried to optimize it as much as possible to improve its speed. PGNHandler also has been tested.

## :books: Javadoc

ChessAPI4j provides basic [javadoc](https://lunalobos.github.io/chessapi4j/apidocs/chessapi4j/package-summary.html), which will clear up many of the doubts you may have.

## :raising_hand: Contributing

I have not yet created rules for contributing. For now, each case will be analyzed individually without any fixed criteria. If you find bugs, please open an issue or write to my email.

## :page_with_curl: License

ChessAPI4j is licensed under the Apache License, Version 2.0 ("License"). You may use, modify, and distribute this software according to the terms and conditions outlined in the License.

## :mailbox_closed: Author

This library is developed and maintained by me (https://github.com/lunalobos). Feel free to contact me at my email lunalobosmiguel@gmail.com.
