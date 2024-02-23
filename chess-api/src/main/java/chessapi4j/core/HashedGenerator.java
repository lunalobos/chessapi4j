package chessapi4j.core;

import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

class HashedGenerator extends MoveGenerator {

	private static final int HASH_CAPACITY = 2400000;
	private static final Map<VisibleData, Long> VISIBLE = Collections.synchronizedMap(new CustomLinkedHashMap<>(HASH_CAPACITY));

	public HashedGenerator(BitPosition position) {
		super(position);
	}

	@Override
	public void generateLegalMoves() {
		inCheckMask = 0L;
		checkCount = 0;
		int whiteMove = (int) position.getWhiteMoveNumeric();
		int pawnPiece = PAWNS[whiteMove];
		int knightPiece = KNIGHTS[whiteMove];
		int bishopPiece = BISHOPS[whiteMove];
		int rookPiece = ROOKS[whiteMove];
		int queenPiece = QUEENS[whiteMove];
		int kingPiece = KINGS[whiteMove];
		int[][] matrix1 = PAWN_MATRIX1[whiteMove];
		int[][] matrix2 = PAWN_MATRIX2[whiteMove];
		int kingSquare = squaresMap(bits[kingPiece - 1]);
		int[][] pawnsDirectionChoice = new int[][] { BLACK_PAWN_MATRIX_2[kingSquare], WHITE_PAWN_MATRIX_2[kingSquare] };
		int[] pawnsDirections = pawnsDirectionChoice[(int) position.getWhiteMoveNumeric()];
		inCheck = isInCheckWhithMask(kingPiece, position.getBits(), position.getWhiteMoveNumeric(), pawnsDirections);

		createCheckMask(kingSquare);
		long[] choice = new long[] { -1L, inCheckMask, 0L, 0L, 0L, 0L };
		inCheckMask = choice[checkCount];

		PieceProcessor pawnCustomizer = new PawnProcessor(pawnsDirections, pawnPiece, matrix1, matrix2, kingSquare);
		PieceProcessor knightCustomizer = new KnightProcessor(knightPiece, pawnsDirections);
		PieceProcessor bishopCustomizer = new BishopProcessor(bishopPiece, pawnsDirections, kingSquare);
		PieceProcessor rookCustomizer = new RookProcessor(rookPiece, pawnsDirections, kingSquare);
		PieceProcessor queenCustomizer = new QueenProcessor(queenPiece, pawnsDirections, kingSquare);
		PieceProcessor kingCustomizer = new KingProcessor(kingPiece);

		children = Stream
				.of(pawnCustomizer, knightCustomizer, bishopCustomizer, rookCustomizer, queenCustomizer, kingCustomizer)
				.map(processor -> processor.process(bits))
				.collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll);

	}

	@Override
	protected long visibleSquares(long[] bits, int[] directionsIndexs, int square, long whiteMoveNumeric) {

		Optional<Long> visible;
		VisibleData input = new VisibleData(bits, directionsIndexs, square, whiteMoveNumeric);
		if ((visible = Optional
				.ofNullable(VISIBLE.get(new VisibleData(bits, directionsIndexs, square, whiteMoveNumeric))))
				.isPresent())
			return visible.get();

		long calculatedValue = super.visibleSquares(bits, directionsIndexs, square, whiteMoveNumeric);

		VISIBLE.put(input, calculatedValue);

		return calculatedValue;

	}

	protected static interface PieceProcessor {

		List<BitPosition> calculateMoves(int sq);

		int piece();

		default List<BitPosition> process(long[] bitboards) {

			return BitSet.valueOf(new long[] { bitboards[piece() - 1] }).stream().mapToObj(sq -> calculateMoves(sq))
					.collect(LinkedList::new, LinkedList::addAll, LinkedList::addAll);
		}
	}

	@Data
	@AllArgsConstructor
	private class PawnProcessor implements PieceProcessor {

		private int[] pawnsDirections;
		private int pawnPiece;
		private int[][] matrix1;
		private int[][] matrix2;
		private int kingSquare;

		@Override
		public List<BitPosition> calculateMoves(int sq) {
			return pawnMoves(1L << sq, sq, pawnsDirections, pawnPiece, matrix1, matrix2, kingSquare);
		}

		@Override
		public int piece() {
			return pawnPiece;
		}

	}

	@Data
	@AllArgsConstructor
	private class KnightProcessor implements PieceProcessor {

		private int knightPiece;
		private int[] pawnsDirections;

		@Override
		public List<BitPosition> calculateMoves(int sq) {
			return knightMoves(1L << sq, sq, knightPiece, pawnsDirections);
		}

		@Override
		public int piece() {
			return knightPiece;
		}

	}

	@Data
	@AllArgsConstructor
	private class BishopProcessor implements PieceProcessor {

		private int bishopPiece;
		private int[] pawnsDirections;
		private int kingSquare;

		@Override
		public List<BitPosition> calculateMoves(int sq) {
			return bishopMoves(1L << sq, sq, bishopPiece, pawnsDirections, kingSquare);
		}

		@Override
		public int piece() {
			return bishopPiece;
		}

	}

	@Data
	@AllArgsConstructor
	private class RookProcessor implements PieceProcessor {

		private int rookPiece;
		private int[] pawnsDirections;
		private int kingSquare;

		@Override
		public List<BitPosition> calculateMoves(int sq) {
			return rookMoves(1L << sq, sq, rookPiece, pawnsDirections, kingSquare);
		}

		@Override
		public int piece() {
			return rookPiece;
		}

	}

	@Data
	@AllArgsConstructor
	private class QueenProcessor implements PieceProcessor {

		private int queenPiece;
		private int[] pawnsDirections;
		private int kingSquare;

		@Override
		public List<BitPosition> calculateMoves(int sq) {
			return queenMoves(1L << sq, sq, queenPiece, pawnsDirections, kingSquare);
		}

		@Override
		public int piece() {
			return queenPiece;
		}

	}

	@Data
	@AllArgsConstructor
	private class KingProcessor implements PieceProcessor {

		private int kingPiece;

		@Override
		public List<BitPosition> calculateMoves(int sq) {
			return kingMoves(sq, kingPiece);
		}

		@Override
		public int piece() {
			return kingPiece;
		}

	}

}

@Data
class VisibleData implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5591142483533004001L;

	private long friends;

	private long enemys;

	private int[] directionsIndexs;

	private int square;

	private int whiteMoveNumeric;

	public VisibleData(long[] bits, int[] directionsIndexs, int square, long whiteMoveNumeric) {
		int aux = (int) (6L & (whiteMoveNumeric << 1 | whiteMoveNumeric << 2));
		friends = bits[6 - aux] | bits[7 - aux] | bits[8 - aux] | bits[9 - aux] | bits[10 - aux] | bits[11 - aux];
		enemys = bits[0 + aux] | bits[1 + aux] | bits[2 + aux] | bits[3 + aux] | bits[4 + aux] | bits[5 + aux];
		this.directionsIndexs = directionsIndexs;
		this.square = square;
		this.whiteMoveNumeric = (int) whiteMoveNumeric;
	}
}

class CustomLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
	/**
	 *
	 */
	private static final long serialVersionUID = -4695516439558731017L;

	private int capacity;

	public CustomLinkedHashMap(int capacity) {
		super(capacity);
		this.capacity = capacity;
	}

	public CustomLinkedHashMap(Map<? extends K, ? extends V> m) {
		super(m);
		capacity = m.size();
	}

	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return this.size() >= capacity;
	}
}
