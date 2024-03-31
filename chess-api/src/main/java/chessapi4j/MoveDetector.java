package chessapi4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;




/**
 * Class for move detection between two positions.
 *
 * @author lunalobos
 *
 */

public final class MoveDetector {
	private Position parent, child;

	public MoveDetector(Position parent, Position child) {
		this.parent = parent;
		this.child = child;
	}

	/**
	 * Returns the move with no check for exceptions.
	 *
	 * @return the move
	 */
	public Move getUnsafeMove() {
		// pieces
		int aux = parent.isWhiteMove() ? 6 : 0;
		int p = Piece.BP.ordinal() - aux;
		int n = Piece.BN.ordinal() - aux;
		int b = Piece.BB.ordinal() - aux;
		int r = Piece.BR.ordinal() - aux;
		int k = Piece.BK.ordinal() - aux;
		int q = Piece.BQ.ordinal() - aux;

		return regularMove(new ArrayList<>(List.of(k, p, n, b, r, q)).iterator()).orElse(coronationMove(p, n, b, r, q));
	}

	private Optional<Move> regularMove(Iterator<Integer> i) {
		int p = i.next();
		long parentP = parent.getBits()[p - 1];
		long childP = child.getBits()[p - 1];
		long pChanges = parentP ^ childP;
		List<Long> pChangesList = Util.longToList(pChanges);
		if (pChangesList.size() == 2) {
			List<Long> parentPList = Util.longToList(parentP);
			List<Long> childPList = Util.longToList(childP);
			int ips = -1;
			int fps = -1;
			for (Long s : pChangesList) {
				if (parentPList.contains(s))
					ips = Long.numberOfTrailingZeros(s);
				if (childPList.contains(s))
					fps = Long.numberOfTrailingZeros(s);
			}
			if (ips != -1 && fps != -1)
				return Optional.of(new Move(1L << fps, ips, -1));
		}
		if (i.hasNext())
			return regularMove(i);
		else
			return Optional.empty();
	}

	private Move coronationMove(int p, int n, int b, int r, int q) {
		long parentP = parent.getBits()[p - 1];
		long childP = child.getBits()[p - 1];
		long pChanges = parentP ^ childP;
		int[] coronationP = new int[] { n, b, r, q };
		List<Long> pChangesList = Util.longToList(pChanges);
		if (pChangesList.size() == 1) {
			int ips = Long.numberOfTrailingZeros(pChangesList.get(0));
			for (int c : coronationP) {
				long parentC = parent.getBits()[c - 1];
				long childC = child.getBits()[c - 1];
				long cChanges = parentC ^ childC;
				List<Long> cChangesList = Util.longToList(cChanges);
				if (cChangesList.size() == 1) {
					long fcs = cChangesList.get(0);
					return new Move(fcs, ips, c);
				}
			}
		}
		return null;
	}

	/**
	 * @return the parent
	 */
	public Position getParent() {
		return parent;
	}

	/**
	 * @return the child
	 */
	public Position getChild() {
		return child;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Position parent) {
		this.parent = parent;
	}

	/**
	 * @param child the child to set
	 */
	public void setChild(Position child) {
		this.child = child;
	}

}
