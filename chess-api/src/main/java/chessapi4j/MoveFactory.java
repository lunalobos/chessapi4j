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

package chessapi4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory class for {@code Move} instances.
 *
 * @author lunalobos
 * @since 1.0.0
 */
public class MoveFactory {
	/**
	 * New instance.
	 *
	 * @param origin the origin square
	 * @param target the target square
	 * @return a move representation instance
	 */
	public static Move instance(int origin, int target) {
		return new Move(1L << target, origin, -1);
	}

	/**
	 * New coronation instance.
	 *
	 * @param origin the origin square
	 * @param target the target square
	 * @param coronationPiece the coronation piece
	 * @return a move representation instance
	 */
	public static Move instance(int origin, int target, int coronationPiece) {
		return new Move(1L << target, origin, coronationPiece);
	}

	/**
	 * New instance.
	 *
	 * @param origin the origin square
	 * @param target the target square
	 * @return a move representation instance
	 *
	 * @since 1.2.3
	 */
	public static Move instance(Square origin, Square target) {
		return new Move(1L << target.ordinal(), origin.ordinal(), -1);
	}

	/**
	 * New coronation instance.
	 *
	 * @param origin the origin square
	 * @param target the target square
	 * @param coronationPiece the coronation piece
	 * @return a move representation instance
	 *
	 * @since 1.2.3
	 */
	public static Move instance(Square origin, Square target, Piece coronationPiece) {
		return new Move(1L << target.ordinal(), origin.ordinal(), coronationPiece.ordinal());
	}


	/**
	 * New instance.
	 *
	 * @param move the move representation
	 *
	 * @since 1.2.3
	 */
	public static Move instance(Move move) {
		return instance(move.getOrigin(), move.getTarget(), move.getPromotionPiece());
	}

	/**
	 * Takes a move string (UCI notation) and a boolean indicating the player who
	 * moves and return the move representation.
	 *
	 * @param move the move string in UCI notation
	 * @param whiteMove the player who moves
	 * @return a move representation instance
	 * @throws MovementException if the move is illegal or the string is invalid
	 */
	public static Move instance(String move, boolean whiteMove) throws MovementException {
		String regex = "(?<colOrigin>[a-h])(?<rowOrigin>[1-8])(?<colTarget>[a-h])(?<rowTarget>[1-8])(?<promotion>[nbrq])?";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(move);
		if (matcher.find()) {
			int xOrigin = Util.getColIndex(matcher.group("colOrigin"));
			int yOrigin = Integer.parseInt(matcher.group("rowOrigin")) - 1;
			int xTarget = Util.getColIndex(matcher.group("colTarget"));
			int yTarget = Integer.parseInt(matcher.group("rowTarget")) - 1;
			int origin = Util.getSquareIndex(xOrigin, yOrigin);
			int target = Util.getSquareIndex(xTarget, yTarget);
			String promotionPiece  = matcher.group("promotion");
			if (promotionPiece != null && !promotionPiece.isEmpty()) {
				String name = (whiteMove ? "W" : "B") + promotionPiece.toUpperCase();
				int promotion = Piece.valueOf(name).ordinal();
				return instance(origin, target, promotion);
			} else {
				return instance(origin, target);
			}
		} else {
			throw MovementException.invalidString(move);
		}
	}
}
