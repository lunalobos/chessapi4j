package chessapi4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import chessapi4j.core.GeneratorFactory;
import chessapi4j.core.MoveDetector;
import chessapi4j.core.Util;

/**
 * Utility class for game's rules.
 * 
 * @author maluna
 *
 */
public class Rules {

	private static final List<List<Integer>> LACK_OF_MATERIAL_MATRIX = new LinkedList<>();
	private static final List<Integer> MATERIAL_PIECES = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 6, 7, 8, 9, 10));
	static {
		Integer[][] matrix = new Integer[][] { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // K k
				{ 0, 1, 0, 0, 0, 0, 1, 0, 0, 0 }, // KN kn
				{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, // KN k
				{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, // K kn
				{ 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 }, // KN kb
				{ 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 }, // KB kn
				{ 0, 0, 1, 0, 0, 0, 0, 1, 0, 0 }, // KB kb
				{ 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, // KB k
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 } // K kb
		};
		for (Integer[] array : matrix) {
			LACK_OF_MATERIAL_MATRIX.add(new LinkedList<>(Arrays.asList(array)));
		}
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	public static boolean isInCheck(Position position) {
		return Util.isInCheck(position);
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	public static boolean lackOfMaterial(Position position) {
		List<Integer> pieceCounterList = MATERIAL_PIECES.stream().map(p -> Long.bitCount(position.getBits()[p]))
				.collect(Collectors.toCollection(LinkedList::new));

		return LACK_OF_MATERIAL_MATRIX.contains(pieceCounterList);
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	private static int movesCounter(Position position) {
		Generator g = GeneratorFactory.instance(position);
		g.generateLegalMoves();

		return g.getChildren().size();
	}

	/**
	 * 
	 * @param position
	 */
	public static void setStatus(Position position) {
		if (movesCounter(position) == 0) {
			if (isInCheck(position))
				position.setCheckmate(true);
			else
				position.setStalemate(true);
		}
		if (position.getHalfMovesCounter() == 50)
			position.setFiftyMoves(true);
		if (lackOfMaterial(position))
			position.setLackOfMaterial(true);
	}

	/**
	 * Allows determining if a specific move is legal for a given position.
	 * 
	 * @param position
	 * @param move
	 */
	public static boolean legal(Position position, Move move) {
		Generator generator = GeneratorFactory.instance(position);
		generator.generateLegalMoves();
		List<String> moves = generator.getLegalMoves().stream().map(m -> m.toString())
				.collect(Collectors.toCollection(LinkedList::new));
		return moves.contains(move.toString());
	}

}