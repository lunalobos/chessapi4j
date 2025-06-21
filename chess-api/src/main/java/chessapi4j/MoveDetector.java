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

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
//bean
/**
 * Class for move detection between two positions. It can be useful for performance reasons.
 *
 * @author lunalobos
 *
 */
@Getter
public final class MoveDetector {
    /**
     * -- GETTER --
     *  Returns the parent
     *
     * @return the parent
     */
    private Position parent;
	/**
     * -- GETTER --
     *  Returns the child
     *
     * @return the child
     */
	private Position child;

	/**
	 * Creates a new instance of MoveDetector.
	 * @param parent the parent position
	 * @param child the child position
	 */
	public MoveDetector(Position parent, Position child) {
		this.parent = parent;
		this.child = child;
	}

	/**
	 * Returns the move between the positions. The answer can be empty if the
	 * positions are not really related.
	 *
	 * @return the move
	 * @since 1.2.6
	 */
	public Optional<Move> getMove() {
		return Optional.ofNullable(getUnsafeMove());
	}

	/**
	 * Returns the move between the positions. The answer can be null if the
	 * positions are not really related.
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
	 * Sets the parent.
	 * @param parent the parent to set
	 */
	public void setParent(Position parent) {
		this.parent = parent;
	}

	/**
	 * Sets the child.
	 * @param child the child to set
	 */
	public void setChild(Position child) {
		this.child = child;
	}

}
