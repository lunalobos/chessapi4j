package chessapi4j.core;

import chessapi4j.Move;
import chessapi4j.MovementException;
import chessapi4j.Piece;

/**
 * Factory class for Move interface.
 * @author Migue
 *
 */
public class MoveFactory {
	public static Move instance(int origin, int destiny) {
		return new BitMove(1L << destiny, origin, -1);
	}

	public static Move instance(int origin, int destiny, int coronationPiece) {
		return new BitMove(1L << destiny, origin, coronationPiece);
	}

	public static Move instance(String move, boolean whiteMove) throws MovementException {
		char[] chars = move.toCharArray();// a1b2q
		if(chars.length < 4)
			throw new MovementException("Invalid UCI string.");
		int xOrigin = Util.getColIndex("" + chars[0]);
		int yOrigin = Integer.parseInt("" + chars[1]) - 1;
		int xDestiny = Util.getColIndex("" + chars[2]);
		int yDestiny = Integer.parseInt("" + chars[3]) - 1;
		int origin = Util.getSquareIndex(xOrigin, yOrigin);
		int destiny = Util.getSquareIndex(xDestiny, yDestiny);
		if(chars.length == 5) {
			String pieceLetter = "" + chars[4];
			int piece = -1;
			if (whiteMove) {
				switch(pieceLetter) {
					case "n": piece = Piece.WN.ordinal();break;
					case "b": piece = Piece.WB.ordinal();break;
					case "r": piece = Piece.WR.ordinal();break;
					case "q": piece = Piece.WQ.ordinal();break;
				}
			} else {
				switch(pieceLetter) {
					case "n": piece = Piece.BK.ordinal();break;
					case "b": piece = Piece.BB.ordinal();break;
					case "r": piece = Piece.BR.ordinal();break;
					case "q": piece = Piece.BQ.ordinal();break;
				}
			}
			if (piece == -1)
				throw new MovementException("Invalid coronation piece letter.");
			return instance(origin, destiny, piece);
		} else {
			return instance(origin, destiny);
		}
	};
}
