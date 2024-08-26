/*
 * Copyright 2024 Miguel Angel Luna Lobos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/lunalobos/chessapi4j/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package chessapi4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class provides methods and tools for manipulating and converting chess
 * moves into PGN format.
 *
 * @author lunalobos
 * @since 1.1.0
 */
public class PGNHandler {
	private static final String[] PIECES = new String[] { "", "", "N", "B", "R", "Q", "K", "", "N", "B", "R", "Q",
			"K" };
	private static final String NAG_REGEX = "[$][1-9][0-9]*";
	private static final String MOVE_REGEX = "(([0-9]+[.]\\s+)?(?<move>(?<regular>(?<piece>[KQBNR])?(?<originCol>[a-h])?(?<originRow>[1-8])?x?(?<targetCol>[a-h])(?<targetRow>[1-8])=?(?<promotion>[QBNR])?)|(?<castle>O-O(-O)?))"
			+ "(?<check>[+#])?" + "(?<nag>\\s*[$][1-9][0-9]*)?" + "(?<comment>\\s*[{].*[}])?"
			+ "(?<rav>\\s*[(].*[)])?\\s*)";
	private static final String TAG_REGEX = "\\[(?<name>[A-Za-z0-9_]+)\\s+\"(?<value>.*)\"\\]";
	private static final String GAME_REGEX = "(?<tags>(\\[[A-Za-z0-9_]+\\s+\".*\"\\]\\s+)+)"
			+ "(?<moves>(([0-9]+[.]\\s+)?(([KQBNR]?[a-h]?[1-8]?x?[a-h][1-8]=?[QBNR]?)|(O-O(-O)?))[+#]?(\\s*[$][1-9][0-9]*)?(\\s*[{].*[}])?(\\s*[(].*[)])?\\s*)+)"
			+ "(?<result>0-1|1-0|1\\/2-1\\/2|\\*)";
	private static final Pattern GAME_PATTERN = Pattern.compile(GAME_REGEX);
	private static final Pattern TAG_PATTERN = Pattern.compile(TAG_REGEX);
	private static final Pattern MOVE_PATTERN = Pattern.compile(MOVE_REGEX);
	private static final Pattern NAG_PATTERN = Pattern.compile(NAG_REGEX);

	/**
	 * Numeric Annotation Glyphs mapping according to
	 * https://www.thechessdrum.net/PGN_Reference.txt
	 */
	public static final Map<Integer, String> NAGS = Map.ofEntries(Map.entry(0, ""), Map.entry(1, "!"),
			Map.entry(2, "?"), Map.entry(3, "!!"), Map.entry(4, "??"), Map.entry(5, "!?"), Map.entry(6, "?!"),
			Map.entry(7, "forced move (all others lose quickly)"),
			Map.entry(8, "singular move (no reasonable alternatives)"), Map.entry(9, "worst move"),
			Map.entry(10, "drawish position"), Map.entry(11, "equal chances, quiet position"),
			Map.entry(12, "equal chances, active position"), Map.entry(13, "unclear position"),
			Map.entry(14, "White has a slight advantage"), Map.entry(15, "Black has a slight advantage"),
			Map.entry(16, "White has a moderate advantage"), Map.entry(17, "Black has a moderate advantage"),
			Map.entry(18, "White has a decisive advantage"), Map.entry(19, "Black has a decisive advantage"),
			Map.entry(20, "White has a crushing advantage (Black should resign)"),
			Map.entry(21, "Black has a crushing advantage (White should resign)"),
			Map.entry(22, "White is in zugzwang"), Map.entry(23, "Black is in zugzwang"),
			Map.entry(24, "White has a slight space advantage"), Map.entry(25, "Black has a slight space advantage"),
			Map.entry(26, "White has a moderate space advantage"),
			Map.entry(27, "Black has a moderate space advantage"),
			Map.entry(28, "White has a decisive space advantage"),
			Map.entry(29, "Black has a decisive space advantage"),
			Map.entry(30, "White has a slight time (development) advantage"),
			Map.entry(31, "Black has a slight time (development) advantage"),
			Map.entry(32, "White has a moderate time (development) advantage"),
			Map.entry(33, "Black has a moderate time (development) advantage"),
			Map.entry(34, "White has a decisive time (development) advantage"),
			Map.entry(35, "Black has a decisive time (development) advantage"),
			Map.entry(36, "White has the initiative"), Map.entry(37, "Black has the initiative"),
			Map.entry(38, "White has a lasting initiative"), Map.entry(39, "Black has a lasting initiative"),
			Map.entry(40, "White has the attack"), Map.entry(41, "Black has the attack"),
			Map.entry(42, "White has insufficient compensation for material deficit"),
			Map.entry(43, "Black has insufficient compensation for material deficit"),
			Map.entry(44, "White has sufficient compensation for material deficit"),
			Map.entry(45, "Black has sufficient compensation for material deficit"),
			Map.entry(46, "White has more than adequate compensation for material deficit"),
			Map.entry(47, "Black has more than adequate compensation for material deficit"),
			Map.entry(48, "White has a slight center control advantage"),
			Map.entry(49, "Black has a slight center control advantage"),
			Map.entry(50, "White has a moderate center control advantage"),
			Map.entry(51, "Black has a moderate center control advantage"),
			Map.entry(52, "White has a decisive center control advantage"),
			Map.entry(53, "Black has a decisive center control advantage"),
			Map.entry(54, "White has a slight kingside control advantage"),
			Map.entry(55, "Black has a slight kingside control advantage"),
			Map.entry(56, "White has a moderate kingside control advantage"),
			Map.entry(57, "Black has a moderate kingside control advantage"),
			Map.entry(58, "White has a decisive kingside control advantage"),
			Map.entry(59, "Black has a decisive kingside control advantage"),
			Map.entry(60, "White has a slight queenside control advantage"),
			Map.entry(61, "Black has a slight queenside control advantage"),
			Map.entry(62, "White has a moderate queenside control advantage"),
			Map.entry(63, "Black has a moderate queenside control advantage"),
			Map.entry(64, "White has a decisive queenside control advantage"),
			Map.entry(65, "Black has a decisive queenside control advantage"),
			Map.entry(66, "White has a vulnerable first rank"), Map.entry(67, "Black has a vulnerable first rank"),
			Map.entry(68, "White has a well protected first rank"),
			Map.entry(69, "Black has a well protected first rank"), Map.entry(70, "White has a poorly protected king"),
			Map.entry(71, "Black has a poorly protected king"), Map.entry(72, "White has a well protected king"),
			Map.entry(73, "Black has a well protected king"), Map.entry(74, "White has a poorly placed king"),
			Map.entry(75, "Black has a poorly placed king"), Map.entry(76, "White has a well placed king"),
			Map.entry(77, "Black has a well placed king"), Map.entry(78, "White has a very weak pawn structure"),
			Map.entry(79, "Black has a very weak pawn structure"),
			Map.entry(80, "White has a moderately weak pawn structure"),
			Map.entry(81, "Black has a moderately weak pawn structure"),
			Map.entry(82, "White has a moderately strong pawn structure"),
			Map.entry(83, "Black has a moderately strong pawn structure"),
			Map.entry(84, "White has a very strong pawn structure"),
			Map.entry(85, "Black has a very strong pawn structure"), Map.entry(86, "White has poor knight placement"),
			Map.entry(87, "Black has poor knight placement"), Map.entry(88, "White has good knight placement"),
			Map.entry(89, "Black has good knight placement"), Map.entry(90, "White has poor bishop placement"),
			Map.entry(91, "Black has poor bishop placement"), Map.entry(92, "White has good bishop placement"),
			Map.entry(93, "Black has good bishop placement"), Map.entry(94, "White has poor rook placement"),
			Map.entry(95, "Black has poor rook placement"), Map.entry(96, "White has good rook placement"),
			Map.entry(97, "Black has good rook placement"), Map.entry(98, "White has poor queen placement"),
			Map.entry(99, "Black has poor queen placement"), Map.entry(100, "White has good queen placement"),
			Map.entry(101, "Black has good queen placement"), Map.entry(102, "White has poor piece coordination"),
			Map.entry(103, "Black has poor piece coordination"), Map.entry(104, "White has good piece coordination"),
			Map.entry(105, "Black has good piece coordination"),
			Map.entry(106, "White has played the opening very poorly"),
			Map.entry(107, "Black has played the opening very poorly"),
			Map.entry(108, "White has played the opening poorly"),
			Map.entry(109, "Black has played the opening poorly"), Map.entry(110, "White has played the opening well"),
			Map.entry(111, "Black has played the opening well"),
			Map.entry(112, "White has played the opening very well"),
			Map.entry(113, "Black has played the opening very well"),
			Map.entry(114, "White has played the middlegame very poorly"),
			Map.entry(115, "Black has played the middlegame very poorly"),
			Map.entry(116, "White has played the middlegame poorly"),
			Map.entry(117, "Black has played the middlegame poorly"),
			Map.entry(118, "White has played the middlegame well"),
			Map.entry(119, "Black has played the middlegame well"),
			Map.entry(120, "White has played the middlegame very well"),
			Map.entry(121, "Black has played the middlegame very well"),
			Map.entry(122, "White has played the ending very poorly"),
			Map.entry(123, "Black has played the ending very poorly"),
			Map.entry(124, "White has played the ending poorly"), Map.entry(125, "Black has played the ending poorly"),
			Map.entry(126, "White has played the ending well"), Map.entry(127, "Black has played the ending well"),
			Map.entry(128, "White has played the ending very well"),
			Map.entry(129, "Black has played the ending very well"), Map.entry(130, "White has slight counterplay"),
			Map.entry(131, "Black has slight counterplay"), Map.entry(132, "White has moderate counterplay"),
			Map.entry(133, "Black has moderate counterplay"), Map.entry(134, "White has decisive counterplay"),
			Map.entry(135, "Black has decisive counterplay"),
			Map.entry(136, "White has moderate time control pressure"),
			Map.entry(137, "Black has moderate time control pressure"),
			Map.entry(138, "White has severe time control pressure"),
			Map.entry(139, "Black has severe time control pressure"));

	private static GameBuilder parseTags(String tags) {
		Matcher matcher = TAG_PATTERN.matcher(tags);
		GameBuilder builder = new GameBuilder();
		while (matcher.find()) {
			String name = matcher.group("name");
			String value = matcher.group("value");
			switch (name) {
				case "Event":
					builder.event(new Tag(name, value));
					break;
				case "Site":
					builder.site(new Tag(name, value));
					break;
				case "Date":
					builder.date(new Tag(name, value));
					break;
				case "Round":
					builder.round(new Tag(name, value));
					break;
				case "White":
					builder.white(new Tag(name, value));
					break;
				case "Black":
					builder.black(new Tag(name, value));
					break;
				case "Result":
					builder.result(new Tag(name, value));
					break;
				default:
					builder.suplementalTag(new Tag(name, value));
			}
		}
		return builder;
	}

	/**
	 * Converts a chess move given in Universal Chess Interface (UCI) format (Pure
	 * Coordinate Notation) to Standard Algebraic Notation (SAN) format.
	 *
	 * @param position The current position of the chess game.
	 * @param move     The move in Universal Chess Interface (UCI) format to be
	 *                 converted.
	 * @return The move converted to Standard Algebraic Notation (SAN) format.
	 * @throws IllegalArgumentException if the move is illegal or cannot be
	 *                                  converted to SAN format.
	 */
	public static String toSAN(Position position, Move move) {
		if (!Rules.legal(position, move))
			throw new IllegalArgumentException(String.format("Illegal move %s for position\n%s", move, position));
		StringBuilder sbSAN = new StringBuilder();

		// Identify the piece being moved.
		Piece piece = Piece.values()[position.getSquares()[move.getOrigin()]];
		sbSAN.append(PIECES[piece.ordinal()]);

		// Determine if there are one or more pieces of the same type that can move to
		// the same destination.

		GeneratorFactory.instance().generateMoves(position, GeneratorFactory.instance().generateChildren(position));
		List<Move> moves = GeneratorFactory.instance()
				.generateMoves(position, GeneratorFactory.instance().generateChildren(position)).stream()
				.filter(m -> m.getTarget() == move.getTarget())
				.filter(m -> Piece.values()[position.getSquares()[m.getOrigin()]] == Piece
						.values()[position.getSquares()[move.getOrigin()]])
				.filter(m -> m.getOrigin() != move.getOrigin()).collect(Collectors.toCollection(LinkedList::new));

		// If such pieces exist, determine if they are in the same column.
		boolean sameColumn = !moves.stream().filter(m -> Util.getCol(m.getOrigin()) == Util.getCol(move.getOrigin()))
				.collect(Collectors.toCollection(LinkedList::new)).isEmpty();

		// If such pieces exist, determine if they are in the same row.
		boolean sameRow = !moves.stream().filter(m -> Util.getRow(m.getOrigin()) == Util.getRow(move.getOrigin()))
				.collect(Collectors.toCollection(LinkedList::new)).isEmpty();

		// If they are in the same row but not in the same column, prepend the column
		// letter to the destination square.
		if (sameRow && !sameColumn)
			sbSAN.append(Util.getColLetter(move.getOrigin()));

		// If they are in the same column but not in the same row, prepend the row
		// number to the destination square.
		if (!sameRow && sameColumn)
			sbSAN.append(Util.getRow(move.getOrigin()) + 1);

		// If they are in the same column and row, prepend the origin square to the
		// destination square.
		if (sameRow && sameColumn)
			sbSAN.append(Util.getColLetter(move.getOrigin()) + (Util.getRow(move.getOrigin()) + 1));

		// If they are not in the same row nor the same column, prepend the column
		// letter to the destination square.
		if (!sameRow && !sameColumn && !moves.isEmpty())
			sbSAN.append(Util.getColLetter(move.getOrigin()));

		// Determining if a piece is captured
		boolean capture = position.getSquares()[move.getTarget()] != Piece.EMPTY.ordinal();
		if (capture)
			sbSAN.append("x");

		// Append destiny square
		sbSAN.append(Util.getColLetter(move.getTarget()) + (Util.getRow(move.getTarget()) + 1));

		// Determine if it is a promotion. If so, append "=" + promotedPiece to the
		// destination square.
		boolean isPromotion = move.getPromotionPiece() != -1 && Util.isPromotion(move.getTarget());
		if (isPromotion)
			sbSAN.append("=" + PIECES[move.getPromotionPiece()]);

		// Check if the move is a castling move
		boolean isCastle = (piece == Piece.WK | piece == Piece.BK)
				&& (new LinkedList<>(Arrays.asList("e1g1", "e1c1", "e8g8", "e8c8")).contains(move.toString()));
		if (isCastle) {
			sbSAN = new StringBuilder();
			sbSAN.append(
					move.toString().substring(2, 4).equals("g1") | move.toString().substring(2, 4).equals("g8") ? "O-O"
							: "O-O-O");
		}

		// Determine if it is a check or checkmate
		Position p = position.childFromMove(move).orElseThrow(() -> new IllegalArgumentException("Illegal move"));
		Rules.setStatus(p);
		if (p.isCheckmate())
			sbSAN.append("#");
		else if (Util.isInCheck(p))
			sbSAN.append("+");

		return sbSAN.toString();
	}

	/**
	 * Converts a chess move given in Universal Chess Interface (UCI) format (Pure
	 * Coordinate Notation) to Standard Algebraic Notation (SAN) format.
	 *
	 * @param position The current position of the chess game.
	 * @param uciMove  The move in Universal Chess Interface (UCI) format to be
	 *                 converted.
	 * @return The move converted to Standard Algebraic Notation (SAN) format.
	 * @throws IllegalArgumentException if the move is illegal or cannot be
	 *                                  converted to SAN format.
	 */
	public static String toSAN(Position position, String uciMove) {
		Move move;
		try {
			move = MoveFactory.instance(uciMove, position.isWhiteMove());
		} catch (MovementException e1) {
			throw new IllegalArgumentException("Not valid uci move string.");
		}
		return toSAN(position, move);
	}

	/**
	 * Converts a chess move given in Standard Algebraic Notation (SAN) to Universal
	 * Chess Interface (UCI) format (Pure Coordinate Notation).
	 *
	 * @param position The current position of the chess game.
	 * @param sanMove  The move in Standard Algebraic Notation (SAN) format to be
	 *                 converted.
	 * @return The move converted to Universal Chess Interface (UCI) format.
	 * @throws IllegalArgumentException if the given expression is not in the
	 *                                  standard algebraic notation format.
	 */
	public static Optional<String> toUCI(Position position, String sanMove) {
		StringBuilder sbUCI = new StringBuilder();
		Matcher matcher = MOVE_PATTERN.matcher(sanMove);
		boolean finded = matcher.find();
		if (!finded)
			throw new IllegalArgumentException(
					"The given expression is not in the standard algebraic notation format.");
		// Check if the move is a castling move

		boolean castleFinded = matcher.group("castle") != null;
		if (castleFinded) {
			boolean longCastle = matcher.group("castle").equals("O-O-O");
			if (!longCastle) {
				sbUCI.append(position.isWhiteMove() ? "e1g1" : "e8g8");
			} else {
				sbUCI.append(position.isWhiteMove() ? "e1c1" : "e8c8");
			}
			return Optional.of(sbUCI.toString());
		}

		// Process the regular move in SAN format
		String origin = (matcher.group("originCol") == null ? "" : matcher.group("originCol"))
				+ (matcher.group("originRow") == null ? "" : matcher.group("originRow"));
		
		final String target = matcher.group("targetCol") + matcher.group("targetRow");
		
		final String promotionPiece = matcher.group("promotion") != null ? matcher.group("promotion").toLowerCase()
				: "";
		
		if (origin.length() == 2) {
			// Append origin, target, and promotion piece if present
			sbUCI.append(origin);
			sbUCI.append(target);
			sbUCI.append(promotionPiece);
			return Optional.of(sbUCI.toString());
		}

		final Piece piece = Piece.valueOf((position.isWhiteMove() ? "W" : "B")
				+ ((matcher.group("piece") == null || matcher.group("piece").equals("")) ? "P"
						: matcher.group("piece")));

		return GeneratorFactory.instance()
				.generateMoves(position, GeneratorFactory.instance().generateChildren(position)).stream()
				.filter(m -> {
					char[] chars = target.toCharArray();
					int xDestiny = Util.getColIndex("" + chars[0]);
					int yDestiny = Integer.parseInt("" + chars[1]) - 1;
					int destiny = Util.getSquareIndex(xDestiny, yDestiny);
					
					return m.getTarget() == destiny;
				}).filter(m -> {
					Piece movePiece = Piece.values()[position.getSquares()[m.getOrigin()]];
					
					return movePiece == piece;
				}).filter(m -> Piece.values()[position.getSquares()[m.getOrigin()]] == piece).filter(m -> {
					int row = -1;
					int col = -1;
					try {
						if (origin.length() == 1)
							row = Integer.parseInt(origin) - 1;
					} catch (Exception e) {

					}
					if (row == -1 && origin.length() == 1) {
						col = Util.getColIndex(origin);
					}
					if (col != -1) {
						int column = Util.getCol(m.getOrigin());
						
						return column == col;
					} else if (row != -1) {
						int r = Util.getRow(m.getOrigin());
						
						return r == row;
					} else
						return true;
				}).filter(m -> {
					if (m.getPromotionPiece() != -1) {
						String promoted = Piece.values()[m.getPromotionPiece()].toString().substring(1, 2)
								.toLowerCase();
						
						return promoted.equals(promotionPiece);
					} else {
						
						return true;
					}	
				}).map(m -> m.toString()).findFirst();
	}

	/**
	 * Captures a tag bloc from a line of text using a regular expression.
	 *
	 * @param line the line of text that contains the tag in the format "[name
	 *             "value"]".
	 * @return the captured Tag object.
	 * @throws IllegalArgumentException if a valid tag is not found in the line of
	 *                                  text.
	 */
	protected static Tag captureTag(String line) {
		Pattern pattern = Pattern.compile(TAG_REGEX);
		Matcher matcher = pattern.matcher(line);
		boolean finded = matcher.find();
		if (!finded)
			throw new IllegalArgumentException("A valid tag was not found in the line of text.");
		String name = matcher.group("name");
		String value = matcher.group("value");
		return new Tag(name, value);
	}

	/**
	 * Captures the moves from a PGN string and converts them into a list of PGNMove
	 * objects.
	 *
	 * @param startpos the starting position of the chess game.
	 * @param line     the PGN string containing the moves.
	 * @return a list of PGNMove objects representing the captured moves.
	 * @throws IllegalArgumentException if an invalid move or illegal move is
	 *                                  encountered.
	 */
	protected static List<PGNMove> captureMoves(Position startpos, String line) {
		// A RegEx is used to match and capture the moves from the PGN string
		Matcher matcher = MOVE_PATTERN.matcher(line);
		List<PGNMove> moves = new LinkedList<>();
		Position position = startpos;
		while (matcher.find()) {
			// toUCI is used to convert the captured move to the Universal Chess Interface
			// (UCI) notation
			final Position pos = position;
			String move = toUCI(position, matcher.group("move"))
					.orElseThrow(
							() -> new IllegalArgumentException("group: \n%s\nfen: %s".formatted(matcher.group(), pos.toFen())));
			
			// captured Numeric Annotation Glyphs (NAGs) and Recursive Annotation Variations
			// (RAVs) are also extracted
			String nags = matcher.group("nag");
			String rav = matcher.group("rav");
			String commentContent = matcher.group("comment");
			PGNMove m;

			// PGNMove objects are created based on the captured moves using the
			// MoveFactory.instance method
			try {
				m = new PGNMove(MoveFactory.instance(move, position.isWhiteMove()), position);
			} catch (MovementException e) {
				throw new IllegalArgumentException(e.getMessage());
			}

			// captured RAVs are recursively processed by calling the captureMoves method
			List<PGNMove> ravMoves = null;
			if (rav != null)
				ravMoves = captureMoves(position, rav);

			// captured NAGs are parsed and stored in a list of integers
			List<Integer> nagsList = null;
			if (nags != null) {
				Matcher nagMatcher = NAG_PATTERN.matcher(nags);
				nagsList = new LinkedList<>();
				while (nagMatcher.find()) {
					Integer nag = Integer.parseInt(nagMatcher.group().substring(1));
					nagsList.add(nag);
				}
			}
			final Position parent = position.makeClone();
			// moves are executed and the resulting position is updated
			position = position.childFromMove(m).orElseThrow(() -> new IllegalArgumentException(
					String.format("Illegal move. Line: %s, Move:%s, Moves:%s Position:\n%s", line, m, moves, parent)));

			// PGNMove object is configured with the captured RAVs, NAGs, and comments
			m.setRav(ravMoves);
			m.setSuffixAnnotations(nagsList);
			m.setComment(commentContent);

			// this PGNMove object is added to a list of moves
			moves.add(m);

		}
		return moves;
	}

	/**
	 *
	 * Parses a file containing chess game data in PGN format and returns a list of
	 * Game objects.
	 *
	 * @param path the path to the file to be parsed
	 *
	 * @return a list of Game objects parsed from the file
	 *
	 * @throws IllegalArgumentException if the path is invalid or inaccessible
	 */

	public static List<Game> parseGames(Path path) {
		List<Game> games = new LinkedList<>();
		try (BufferedReader reader = Files.newBufferedReader(path)) {
			parseGames(reader, games);
			return games;
		} catch (IOException e) {
			return games;
		}
	}

	/**
	 * Parses an input stream containing chess game data in PGN format and returns a
	 * list of Game objects.
	 * 
	 * @param in the input stream to be parsed
	 * @return a list of Game objects parsed from the input stream
	 * @throws IllegalArgumentException if the input stream is invalid or
	 *                                  inaccessible
	 */
	public static List<Game> parseGames(InputStream in) {
		InputStreamReader inr = new InputStreamReader(in);
		BufferedReader reader = new BufferedReader(inr);
		List<Game> games = new LinkedList<>();
		parseGames(reader, games);
		return games;
	}

	private static void parseGames(BufferedReader reader, List<Game> games) {
		StringBuilder sb = new StringBuilder();
		Optional<String> moreLines = readMoreLines(reader);

		while (moreLines.isPresent()) {
			sb.append(moreLines.get());
			Matcher matcher = GAME_PATTERN.matcher(sb);
			if (matcher.find()) {
				String tags = matcher.group("tags");
				String moves = matcher.group("moves");
				GameBuilder builder = parseTags(tags);
				Position position = new Position(
						builder.getFen().orElse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
				builder.moves(PGNHandler.captureMoves(position, moves));
				games.add(builder.build());
				int index = matcher.end();
				sb.delete(0, index);
			}
			moreLines = readMoreLines(reader);
		}
	}

	/**
	 * Parses a string containing chess game data in PGN format and returns a list
	 * of
	 * Game objects.
	 * 
	 * @param pgnBase the base to be parsed already as a string
	 * @return a list of Game objects parsed from the string
	 * @throws IllegalArgumentException if the string is invalid or inaccessible
	 */
	public static List<Game> parseGames(String pgnBase) {
		List<Game> games = new LinkedList<>();
		Matcher matcher = GAME_PATTERN.matcher(pgnBase);
		if (matcher.find()) {
			String tags = matcher.group("tags");
			String moves = matcher.group("moves");
			GameBuilder builder = parseTags(tags);
			Position position = new Position(
					builder.getFen().orElse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
			builder.moves(PGNHandler.captureMoves(position, moves));
			games.add(builder.build());
		}
		return games;
	}

	private static Optional<String> readMoreLines(BufferedReader reader) {
		StringBuilder builder = new StringBuilder();
		String line = "";
		int i = 0;
		while (i < 25 && line != null) {
			try {
				line = reader.readLine();
			} catch (IOException e) {
				line = null;
			}
			if (line == null)
				break;
			builder.append(line).append("\n");
			i++;
		}

		if (builder.isEmpty())
			return Optional.empty();
		else
			return Optional.of(builder.toString());
	}
}

class GameBuilder {
	private Tag event, site, date, round, white, black, result;
	private Set<Tag> suplementalTags;
	private List<PGNMove> moves;

	public Tag getEvent() {
		return event;
	}

	public void setEvent(Tag event) {
		this.event = event;
	}

	public Tag getSite() {
		return site;
	}

	public void setSite(Tag site) {
		this.site = site;
	}

	public Tag getDate() {
		return date;
	}

	public void setDate(Tag date) {
		this.date = date;
	}

	public Tag getRound() {
		return round;
	}

	public void setRound(Tag round) {
		this.round = round;
	}

	public Tag getWhite() {
		return white;
	}

	public void setWhite(Tag white) {
		this.white = white;
	}

	public Tag getBlack() {
		return black;
	}

	public void setBlack(Tag black) {
		this.black = black;
	}

	public Tag getResult() {
		return result;
	}

	public void setResult(Tag result) {
		this.result = result;
	}

	public Set<Tag> getSuplementalTags() {
		return suplementalTags;
	}

	public void setSuplementalTags(Set<Tag> suplementalTags) {
		this.suplementalTags = suplementalTags;
	}

	public List<PGNMove> getMoves() {
		return moves;
	}

	public void setMoves(List<PGNMove> moves) {
		this.moves = moves;
	}

	public GameBuilder() {
		suplementalTags = new HashSet<>();
	}

	public GameBuilder event(Tag event) {
		this.event = event;
		return this;
	}

	public GameBuilder site(Tag site) {
		this.site = site;
		return this;
	}

	public GameBuilder date(Tag date) {
		this.date = date;
		return this;
	}

	public GameBuilder round(Tag round) {
		this.round = round;
		return this;
	}

	public GameBuilder white(Tag white) {
		this.white = white;
		return this;
	}

	public GameBuilder black(Tag black) {
		this.black = black;
		return this;
	}

	public GameBuilder result(Tag result) {
		this.result = result;
		return this;
	}

	public GameBuilder suplementalTag(Tag tag) {
		suplementalTags.add(tag);
		return this;
	}

	public GameBuilder suplementalTags(Set<Tag> suplementalTags) {
		this.suplementalTags = suplementalTags;
		return this;
	}

	public GameBuilder moves(List<PGNMove> moves) {
		this.moves = moves;
		return this;
	}

	public Optional<String> getFen() {
		return suplementalTags.stream().filter(
				tag -> tag.getName().equals("FEN") || tag.getName().equals("Fen") || tag.getName().equals("fen"))
				.map(tag -> tag.getValue()).findFirst();
	}

	public Game build() {

		event = Optional.ofNullable(event).orElse(new Tag("Event", "Unknown"));
		site = Optional.ofNullable(site).orElse(new Tag("Site", "Unknown"));
		date = Optional.ofNullable(date).orElse(new Tag("Date", "Unknown"));
		round = Optional.ofNullable(round).orElse(new Tag("Round", "Unknown"));
		white = Optional.ofNullable(white).orElse(new Tag("White", "Unknown"));
		black = Optional.ofNullable(black).orElse(new Tag("Black", "Unknown"));
		result = Optional.ofNullable(result).orElse(new Tag("Result", "Unknown"));
		moves = Optional.ofNullable(moves).orElse(new LinkedList<>());

		return new Game(event, site, date, round, white, black, result, suplementalTags, moves);
	}

	@Override
	public String toString() {
		return "GameBuilder [event=" + event + ", site=" + site + ", date=" + date + ", round=" + round + ", white="
				+ white + ", black=" + black + ", result=" + result + ", suplementalTags=" + suplementalTags
				+ ", moves=" + moves + "]";
	}

}
