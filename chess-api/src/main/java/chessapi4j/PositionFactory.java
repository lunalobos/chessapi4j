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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;

/**
 * Utility factory class for {@code Position} instances.
 *
 * @author lunalobos
 * @since 1.0.0
 */
public class PositionFactory {

	/**
	 * Custom new instance. Argument is a fen string with the next moves separated
	 * by spaces. Returns the position after play all the moves. The form of the
	 * string is such as the used in the UCI protocol: positionFen + moves(token) + move1 + move2 +
	 * ... + moveN
	 *
	 * @param fenPlusMoves
	 * @return a new instance
	 */
	public static Position fromMoves(String fenPlusMoves) throws MovementException {
		Scanner input = new Scanner(fenPlusMoves);
		List<String> list = new LinkedList<>();
		while (input.hasNext()) {
			list.add(input.next());
		}
		input.close();
		if (list.size() < 6)
			throw new IllegalArgumentException("Invalid fen string.");
		String fen = list.get(0) + " " + list.get(1) + " " + list.get(2) + " " + list.get(3) + " " + list.get(4) + " "
				+ list.get(5);
		Position position = new Position(fen);

		if (list.size() > 6 && !list.get(6).equals("moves"))
			throw new IllegalArgumentException("Invalid toquen.");
		List<Move> moves = new ArrayList<>();
		if (list.size() > 7) {
			boolean whiteMove = position.isWhiteMove();
			for (int i = 7; i < list.size(); i++) {
				Move move = MoveFactory.instance(list.get(i), whiteMove);
				whiteMove = whiteMove ? false : true;
				moves.add(move);
			}
		}
		return fromMoves(position, moves);

	}

	/**
	 * Custom new instance. Arguments are the initial position and a list of the
	 * next moves. Returns the position after play all moves.
	 *
	 * @param p, moves
	 * @return a new instance
	 */
	public static Position fromMoves(Position p, List<Move> moves) throws MovementException {
		Position fp = p.makeClone();
		for (Move move : moves) {
			final var copy = fp;
			fp = fp.childFromMove(move)
					.orElseThrow(() -> new MovementException(move, copy));
		}
		return fp;
	}

	/**
	 * If the provided FEN is valid, it returns an {@code Optional} containing the position; otherwise, 
	 * it returns an empty {@code Optional}.
	 * 
	 * @param fen the fen string
	 * @return an optional containing the position if the fen is valid
	 * @since 1.2.4
	 */
	public Optional<Position> secureInstance(String fen){
		return Rules.isValidFen(fen) ? Optional.of(new Position(fen)) : Optional.empty();
	}	
}
