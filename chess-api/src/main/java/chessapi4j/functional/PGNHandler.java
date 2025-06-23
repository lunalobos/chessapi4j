/*
 * Copyright 2025 Miguel Angel Luna Lobos
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
package chessapi4j.functional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import chessapi4j.MovementException;
import chessapi4j.Piece;
import chessapi4j.Square;
import chessapi4j.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
/**
 * This class is used to handle PGN files. It provides methods to read and parse PGN files.
 * 
 * @author lunalobos
 * @since 1.2.9
 */
public class PGNHandler {
    private static final String[] PIECES = new String[] { "", "", "N", "B", "R", "Q", "K", "", "N", "B", "R", "Q",
			"K" };

	private static final String NAG_REGEX = "[$][1-9][0-9]*";
	private static final String MOVE_REGEX = "(([0-9]+[.]\\s+)?(?<move>(?<regular>(?<piece>[KQBNR])?(?<originCol>[a-h])?(?<originRow>[1-8])?x?(?<targetCol>[a-h])(?<targetRow>[1-8])=?(?<promotion>[QBNR])?)|(?<castle>O-O(-O)?))"
			+ "(?<check>[+#])?" + "(?<nag>\\s*[$][1-9][0-9]*)?" + "(?<comment>\\s*[{].*[}])?"
			+ "(?<rav>\\s*[(].*[)])?\\s*)";
	private static final String TAG_REGEX = "\\[(?<name>[A-Za-z0-9_]+)\\s+\"(?<value>.*)\"]";
	private static final String GAME_REGEX = "(?<tags>(\\[[A-Za-z0-9_]+\\s+\".*\"]\\s+)+)"
			+ "(?<moves>(([0-9]+[.]\\s+)?(([KQBNR]?[a-h]?[1-8]?x?[a-h][1-8]=?[QBNR]?)|(O-O(-O)?))[+#]?(\\s*[$][1-9][0-9]*)?(\\s*[{].*[}])?(\\s*[(].*[)])?\\s*)+)"
			+ "(?<result>0-1|1-0|1/2-1/2|\\*)";
	private static final Pattern GAME_PATTERN = Pattern.compile(GAME_REGEX);
	private static final Pattern TAG_PATTERN = Pattern.compile(TAG_REGEX);
	private static final Pattern MOVE_PATTERN = Pattern.compile(MOVE_REGEX);
	private static final Pattern NAG_PATTERN = Pattern.compile(NAG_REGEX);

	/**
     * Numeric Annotation Glyphs mapping according to
     * <a href="https://www.thechessdrum.net/PGN_Reference.txt">PGN_Reference</a>
     */
	public static final Map<Integer, String> NAGS = Map.<Integer, String>ofEntries(Map.entry(0, ""), Map.entry(1, "!"),
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
		if (!position.isLegal(move))
			throw new IllegalArgumentException(String.format("Illegal move %s for position\n%s", move, position));
		StringBuilder sbSAN = new StringBuilder();

		// Identify the piece being moved.
		Piece piece = Piece.values()[position.getSquares()[move.getOrigin()]];
		sbSAN.append(PIECES[piece.ordinal()]);

		// Determinate if the piece is a pawn.
		var isPawn = piece == Piece.WP || piece == Piece.BP;

		// Determine if there are one or more pieces of the same type that can move to
		// the same destination.

		List<Move> moves = position.children().stream()
                .map(Tuple::getV2)
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

		// If they are in the same row but not in the same column, append the column
		// letter.
		if (sameRow && !sameColumn && !isPawn)
			sbSAN.append(Util.getColLetter(move.getOrigin()));

		// If they are in the same column but not in the same row, append the row
		// number.
		if (!sameRow && sameColumn)
			sbSAN.append(Util.getRow(move.getOrigin()) + 1);

		// If they are in the same column and row, append the origin square.
		if (sameRow && sameColumn)
			sbSAN.append(Util.getColLetter(move.getOrigin())).append(Util.getRow(move.getOrigin()) + 1);

		// If they are not in the same row nor the same column, append the column
		// letter.
		if (!sameRow && !sameColumn && !moves.isEmpty() && !isPawn)
			sbSAN.append(Util.getColLetter(move.getOrigin()));

		// Determining if a piece is captured
		boolean capture = position.getSquares()[move.getTarget()] != Piece.EMPTY.ordinal();
		if (capture) {
			if (isPawn)
				sbSAN.append(Util.getColLetter(move.getOrigin()));
			sbSAN.append("x");
		}

		// Append target square
		sbSAN.append(Util.getColLetter(move.getTarget())).append(Util.getRow(move.getTarget()) + 1);

		// Determine if it is a promotion. If so, append "=" + promotedPiece to the
		// destination square.
		boolean isPromotion = move.getPromotionPiece() != -1 && Util.isPromotion(move.getTarget());
		if (isPromotion)
			sbSAN.append("=").append(PIECES[move.getPromotionPiece()]);

		// Check if the move is a castling move
		boolean isCastle = (piece == Piece.WK | piece == Piece.BK)
				&& (new LinkedList<>(Arrays.asList("e1g1", "e1c1", "e8g8", "e8c8")).contains(move.toString()));
		if (isCastle) {
			sbSAN = new StringBuilder();
			sbSAN.append(
					move.toString().startsWith("g1", 2) | move.toString().startsWith("g8", 2) ? "O-O"
							: "O-O-O");
		}

		// Determine if it is a check or checkmate
		Position p = position.move(move);
	
		if (p.checkmate())
			sbSAN.append("#");
		else if (p.check())
			sbSAN.append("+");

		return sbSAN.toString();
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
	public static Optional<Move> toUCI(Position position, String sanMove) {
		Matcher matcher = MOVE_PATTERN.matcher(sanMove.trim());
		boolean found = matcher.find();
		if (!found)
			throw new IllegalArgumentException("The given expression is not in the standard algebraic notation format.");
		
		// Check if the move is a castling move
		boolean castleFound = matcher.group("castle") != null;
		if (castleFound) {
			boolean longCastle = matcher.group("castle").equals("O-O-O");
			if (!longCastle) {
				return Optional
						.of(position.whiteMove() ? new Move(Square.E1, Square.G1) : new Move(Square.E8, Square.G8));
			} else {
				return Optional
						.of(position.whiteMove() ? new Move(Square.E1, Square.C1) : new Move(Square.E8, Square.C8));
			}
		}

		// Process the regular move in SAN format
		String origin = (matcher.group("originCol") == null ? "" : matcher.group("originCol"))
				+ (matcher.group("originRow") == null ? "" : matcher.group("originRow"));

		final String target = matcher.group("targetCol") + matcher.group("targetRow");

		final String promotionPiece = matcher.group("promotion") != null ? matcher.group("promotion")
				.toLowerCase() : "";

		if (origin.length() == 2) {

			return Optional.of(new Move(Util.getSquare(origin), Util.getSquare(target), Piece.valueOf(
					position.whiteMove() ? ("W" + promotionPiece.toUpperCase())
							: ("B" + promotionPiece.toUpperCase()))));
		}

		final Piece piece = Piece.valueOf((position.whiteMove() ? "W" : "B")
				+ ((matcher.group("piece") == null || matcher.group("piece").isEmpty()) ? "P"
						: matcher.group("piece")));

		return position.children().stream()
				.map(Tuple::getV2)
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
				}).findFirst();
	}

	private static Tags parseTags(String tags) {
		Matcher matcher = TAG_PATTERN.matcher(tags);
		Tag event = new Tag("Event", "unknown");
		Tag site = new Tag("Site", "unknown");
		Tag date = new Tag("Date", "unknown");
		Tag round = new Tag("Round", "unknown");
		Tag white = new Tag("White", "unknown");
		Tag black = new Tag("Black", "unknown");
		Tag result = new Tag("Result", "unknown");
		var supplementalTags = new ArrayDeque<Tag>();
		while (matcher.find()) {
			String name = matcher.group("name");
			String value = matcher.group("value");
			switch (name) {
				case "Event":
					event = new Tag(name, value);
					break;
				case "Site":
					site = new Tag(name, value);
					break;
				case "Date":
					date = new Tag(name, value);
					break;
				case "Round":
					round = new Tag(name, value);
					break;
				case "White":
					white = new Tag(name, value);
					break;
				case "Black":
					black = new Tag(name, value);
					break;
				case "Result":
					result = new Tag(name, value);
					break;
				default:
					supplementalTags.add(new Tag(name, value));
			}
		}
		return new Tags(event, site, date, round, white, black, result, supplementalTags);
	}

	private static Optional<String> readMoreLines(BufferedReader reader) {
		StringBuilder builder = new StringBuilder();
		String line;
		int i = 0;
		while (i < 25) {
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

		if (builder.length() == 0)
			return Optional.empty();
		else
			return Optional.of(builder.toString());
	}

	static Deque<PGNMove> captureMoves(Position startpos, String line) {
		// A RegEx is used to match and capture the moves from the PGN string
		var matcher = MOVE_PATTERN.matcher(line);
		var moves = new BlockingList<PGNMove>();
		var position = startpos;
		while (matcher.find()) {
			// toUCI is used to convert the captured move to the Universal Chess Interface
			// (UCI) notation
			final Position pos = position;
			var move = toUCI(position, matcher.group("move"))
					.orElseThrow(() -> new MovementException(
									String.format("group: \n%s\nfen: %s", matcher.group(), pos.fen())));

			// captured Numeric Annotation Glyphs (NAGs) and Recursive Annotation Variations
			// (RAVs) are also extracted
			String nags = matcher.group("nag");
			String rav = matcher.group("rav");
			String commentContent = matcher.group("comment");		
			
			// captured RAVs are recursively processed by calling the captureMoves method
			var ravMoves = new BlockingList<PGNMove>();
			if (rav != null)
				ravMoves.addAll(captureMoves(position, rav));

			// captured NAGs are parsed and stored in a list of integers
			var nagsList = new BlockingList<Integer>();
			if (nags != null) {
				Matcher nagMatcher = NAG_PATTERN.matcher(nags);
				while (nagMatcher.find()) {
					Integer nag = Integer.parseInt(nagMatcher.group().substring(1));
					nagsList.add(nag);
				}
			}
			// PGNMove object is configured with the captured RAVs, NAGs, and comments
			var m = new PGNMove(move.getOrigin(), move.getTarget(), move.getPromotionPiece(), position,
					nagsList, ravMoves, commentContent);
			// move is executed and the resulting position is updated
			position = position.move(m);
			// this PGNMove object is added to a list of moves
			moves.add(m);
		}
		return moves;
	}

	private static void parseGames(BufferedReader reader, List<Game> games) {
		StringBuilder sb = new StringBuilder();
		Optional<String> moreLines = readMoreLines(reader);
		while (moreLines.isPresent()) {
			sb.append(moreLines.get());
			Matcher matcher = GAME_PATTERN.matcher(sb);
			if (matcher.find()) {
				var tags = matcher.group("tags");
				var moves = matcher.group("moves");
				var tagsObj = parseTags(tags);
				var position = tagsObj.getFen().map(Position::new).orElse(Factory.startPos());
				var movesObj = captureMoves(position, moves);
				var game = new Game(
					tagsObj.getEvent(),
					tagsObj.getSite(),
					tagsObj.getDate(),
					tagsObj.getRound(),
					tagsObj.getWhite(),
					tagsObj.getBlack(),
					tagsObj.getResult(),
					new HashSet<>(tagsObj.getSupplementalTags()),
					movesObj
				);
				games.add(game);
				int index = matcher.end();
				sb.delete(0, index);
			}
			moreLines = readMoreLines(reader);
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
		var games = new BlockingList<Game>();
		parseGames(reader, games);
		return games.block();
	}

	/**
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
			// TODO log this error
			return games;
		}
	}

	private PGNHandler() {}

}

@Getter
@AllArgsConstructor
class Tags {
	@NonNull
	private Tag event, site, date, round, white, black, result;
	@NonNull
	Deque<Tag> supplementalTags;

	public Optional<String> getFen(){
		return supplementalTags.stream()
			.filter(tag -> tag.getName().equalsIgnoreCase("fen"))
			.map(Tag::getValue)
			.findFirst();
	}
}
