package chessapi4j.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import chessapi4j.Generator;
import chessapi4j.Move;
import chessapi4j.MovementException;
import chessapi4j.Play;
import chessapi4j.Position;

/**
 * Factory class for Position interface.
 * @author lunalobos
 *
 */

public class PositionFactory {
	
	/**
	 * Startpos new instance.
	 * @return a new instance
	 */
	public static Position instance() {
		return new BitPosition();
	}
	
	/**
	 * Custom new instance.
	 * @param fen
	 * @return a new instance
	 */
	public static Position instance(String fen) {
		return new BitPosition(fen);
	}
	
	/**
	 * Custom new instance.
	 * @param bits
	 * @param enPassant
	 * @param whiteMoveNumeric
	 * @param shortCastleWhiteNumeric
	 * @param shortCastleBlackNumeric
	 * @param longCastleWhiteNumeric
	 * @param longCastleBlackNumeric
	 * @param movesCounter
	 * @param halfMovesCounter
	 * @return a new instance
	 */
	public static Position instance(long[] bits, int enPassant, long whiteMoveNumeric, long shortCastleWhiteNumeric, 
			long shortCastleBlackNumeric, long longCastleWhiteNumeric, long longCastleBlackNumeric, int movesCounter, 
			int halfMovesCounter) {
		return new BitPosition(bits.clone(), enPassant, whiteMoveNumeric, shortCastleWhiteNumeric, shortCastleBlackNumeric, 
				longCastleWhiteNumeric, longCastleBlackNumeric, movesCounter, halfMovesCounter);
	}
	
	/**
	 * Custom new instance. Argument is a fen string with the next moves separated by spaces.
	 * Returns the position after play all the moves.
	 * @param fenPlusMoves
	 * @return a new instance
	 */
	public static Position fromMoves(String fenPlusMoves) throws MovementException {
		Scanner input = new Scanner(fenPlusMoves);
		List<String> list = new LinkedList<>();
		while(input.hasNext()) {
			list.add(input.next());
		}
		input.close();
		if(list.size() < 6)
			throw new IllegalArgumentException("Invalid fen string.");
		String fen = list.get(0) + " " + list.get(1) + " " + list.get(2) + " " + list.get(3) + " " + list.get(4) 
						+ " " + list.get(5);
		Position position = new BitPosition(fen);

		if(list.size() > 6 && !list.get(6).equals("moves"))
			throw new IllegalArgumentException("Invalid toquen.");
		List<Move> moves = new ArrayList<>();
		if (list.size() > 7) {
			boolean whiteMove = position.isWhiteMove();
			for(int i = 7; i < list.size(); i++) {
				Move move =  MoveFactory.instance(list.get(i), whiteMove);
				whiteMove = whiteMove ? false : true;
				moves.add(move);
			}
		}
		return fromMoves(position, moves);
		
	}
	
	/**
	 * Custom new instance. Arguments are the initial position and a list of the next moves. 
	 * Returns the position after play all the moves.
	 * @param p, moves
	 * @return a new instance
	 */
	public static Position fromMoves(Position p, List<Move> moves) throws MovementException {
		Position fp = (Position)p.makeClone();
		for (Move move : moves) {
			Generator g;
			if (fp instanceof BitPosition)
				g = GeneratorFactory.instance((BitPosition) fp);
			else
				g = GeneratorFactory.instance(new BitPosition(fp.toFen()));
			Play play = new BitPlay(g, move);
			play.executeMove();
			fp = play.getPosition();
		}
		return fp;
	}
}
