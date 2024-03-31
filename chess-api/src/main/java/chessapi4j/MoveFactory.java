package chessapi4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory class for {@code Move} instances.
 *
 * @author lunalobos
 *
 */
public class MoveFactory {
	/**
	 * New instance.
	 *
	 * @param origin
	 * @param destiny
	 * @return a move representation instance
	 */
	public static Move instance(int origin, int destiny) {
		return new Move(1L << destiny, origin, -1);
	}

	/**
	 * New coronation instance.
	 *
	 * @param origin
	 * @param destiny
	 * @param coronationPiece
	 * @return a move representation instance
	 */
	public static Move instance(int origin, int destiny, int coronationPiece) {
		return new Move(1L << destiny, origin, coronationPiece);
	}

	/**
	 * Takes a move string (UCI notation) and a boolean indicating the player who
	 * moves and return the move representation.
	 *
	 * @param move
	 * @param whiteMove
	 * @return a move representation instance
	 */
	public static Move instance(String move, boolean whiteMove) throws MovementException {
		String regex = "(?<colOrigin>[a-h])(?<rowOrigin>[1-8])(?<colTarget>[a-h])(?<rowTarget>[1-8])(?<promotion>[nbrq])?";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(move);

		while (matcher.find()) {
			int xOrigin = Util.getColIndex(matcher.group("colOrigin"));
			int yOrigin = Integer.parseInt(matcher.group("rowOrigin")) - 1;
			int xTarget = Util.getColIndex(matcher.group("colTarget"));
			int yTarget = Integer.parseInt(matcher.group("rowTarget")) - 1;
			int origin = Util.getSquareIndex(xOrigin, yOrigin);
			int target = Util.getSquareIndex(xTarget, yTarget);
			String promotionPiece;
			if ((promotionPiece = matcher.group("promotion")) != null && !promotionPiece.equals("")) {
				try {
					int promotion = promotionPiece.equals("") ? -1
							: Piece.valueOf((whiteMove ? "W" : "B") + promotionPiece.toUpperCase()).ordinal();

					return instance(origin, target, promotion);
				} catch(Exception e) {
					System.out.println(String.format("Error with promotion. Move: %s, WhiteMove: %b, Piece: %s",
							move, whiteMove, Piece.valueOf((whiteMove ? "W" : "B") + promotionPiece.toUpperCase())));

				}

			} else
				return instance(origin, target);
		}

		throw new MovementException(String.format("Invalid move string: %s", move));
	};
}
