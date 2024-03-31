package chessapi4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for game rules.
 *
 * @author lunalobos
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
		return GeneratorFactory.instance().generateChildren(position).size();
	}

	/**
	 * Set the internal variables checkmate, stalemate, fiftyMoves, lackOfMaterial
	 * of the given position object
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
	 * Allows determinate if an specific move is legal for a given position.
	 *
	 * @param position
	 * @param move
	 *
	 * @return {@code true} if the move is legal, {@code false} otherwise
	 */
	public static boolean legal(Position position, Move move) {
//		System.out.println(GeneratorFactory.instance().generateMoves(position,
//				GeneratorFactory.instance().generateChildren(position)));
		return GeneratorFactory.instance()
				.generateMoves(position, GeneratorFactory.instance().generateChildren(position)).stream()
				.anyMatch(m -> m.equals(move));
	}

}