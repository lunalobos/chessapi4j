package chessapi4j.core;

import java.util.List;

import chessapi4j.Generator;
import chessapi4j.Move;
import chessapi4j.MovementException;
import chessapi4j.Piece;
import chessapi4j.Position;

/**
 * Class for move detection between two positions.
 * @author lunalobos
 *
 */

public class MoveDetector {
	private Position parent, child;
	private Generator generator;
	private List<? extends Position> children;
	
	public MoveDetector(Generator generator, Position child) {
		this.generator = generator;
		this.parent = generator.getPosition();
		this.child = child;
		if(this.generator.getChildren() == null)
			this.generator.generateLegalMoves();
		children = this.generator.getChildren();
	}
	
	/**
	 * Returns the move with no check for exceptions.
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

		// king moves
		Move krm = regularMove(k);
		if (krm != null)
			return krm;

		// pawn moves
		Move prm = regularMove(p);
		if (prm != null)
			return prm;

		// knight moves
		Move nrm = regularMove(n);
		if (nrm != null)
			return nrm;

		// bishop moves
		Move brm = regularMove(b);
		if (brm != null)
			return brm;

		// rook moves
		Move rrm = regularMove(r);
		if (rrm != null)
			return rrm;

		// queen moves
		Move qrm = regularMove(q);
		if (qrm != null)
			return qrm;

		// coronation moves
		Move cm = coronationMove(p, n, b, r, q);
		if (cm != null)
			return cm;
		return null;
	}
	public Move getMove() throws MovementException {
		if (!children.contains(child))
			throw new MovementException("Positions not related.");
		// pieces
		int aux = parent.isWhiteMove() ? 6 : 0;
		int p = Piece.BP.ordinal() - aux;
		int n = Piece.BN.ordinal() - aux;
		int b = Piece.BB.ordinal() - aux;
		int r = Piece.BR.ordinal() - aux;
		int k = Piece.BK.ordinal() - aux;
		int q = Piece.BQ.ordinal() - aux;

		// king moves
		Move krm = regularMove(k);
		if (krm != null)
			return krm;

		// pawn moves
		Move prm = regularMove(p);
		if (prm != null)
			return prm;

		// knight moves
		Move nrm = regularMove(n);
		if (nrm != null)
			return nrm;

		// bishop moves
		Move brm = regularMove(b);
		if (brm != null)
			return brm;

		// rook moves
		Move rrm = regularMove(r);
		if (rrm != null)
			return rrm;

		// queen moves
		Move qrm = regularMove(q);
		if (qrm != null)
			return qrm;

		// coronation moves
		Move cm = coronationMove(p, n, b, r, q);
		if (cm != null)
			return cm;

		throw new MovementException("Positions not related.");
	}

	private Move regularMove(int p) {
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
				return new BitMove(1L << fps, ips, -1);
		}
		return null;
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
					return new BitMove(fcs, ips, c);
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
	 * @return the generator
	 */
	public Generator getGenerator() {
		return generator;
	}

	/**
	 * @return the childs
	 */
	public List<? extends Position> getChilds() {
		return children;
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

	/**
	 * @param generator the generator to set
	 */
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}

	/**
	 * @param childs the childs to set
	 */
	public void setChilds(List<? extends Position> childs) {
		this.children = childs;
	}
}