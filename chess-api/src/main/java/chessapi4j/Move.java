package chessapi4j;

import chessapi4j.core.Util;

/**
 * Move representation. Instances of this interface are used to storage move
 * information. To string method for standard implementations will throw an long
 * algebraic move notation modified for the UCI protocol.
 * 
 * @author lunalobos
 *
 */
public interface Move {

	int getOrigin();

	int getDestiny();

	int getCoronationPiece();

	void setOrigin(int origin);

	void setDestiny(int destiny);

	void setCoronationPiece(int coronationPiece);

	/**
	 * Default string representation. UCI notation.
	 * @return
	 */
	default String stringRepresentation() {
		int y = Util.getRow(getOrigin()) + 1;
		int y_ = Util.getRow(getDestiny()) + 1;
		String collum = Util.getColLetter(getOrigin());
		String collum_ = Util.getColLetter(getDestiny());

		String piece = "";
		if (getCoronationPiece() < 0)
			return collum + y + collum_ + y_;
		else {
			switch (Piece.values()[getCoronationPiece()]) {
			case WN:
				piece += "n";
				break;
			case WB:
				piece += "b";
				break;
			case WR:
				piece += "r";
				break;
			case WQ:
				piece += "q";
				break;
			case BN:
				piece += "n";
				break;
			case BB:
				piece += "b";
				break;
			case BR:
				piece += "r";
				break;
			case BQ:
				piece += "q";
				break;
			default:
				break;
			}
		}
		return collum + y + collum_ + y_ + piece;// UCI notation;
	}

	default boolean areEquals(Object obj) {
		if (!(obj instanceof Move))
			return false;
		if (obj == this)
			return true;
		Move other = (Move) obj;
		if (getOrigin() == other.getOrigin() && getDestiny() == other.getDestiny()
				&& getCoronationPiece() == other.getCoronationPiece())
			return true;
		else
			return false;
	}
}
