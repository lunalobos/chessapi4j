package chessapi4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		if (getCoronationPiece() < 0)
			return collum + y + collum_ + y_;
		else {
			String regex = "(?<color>[BW])(?<piece>[NBRQ])";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(Piece.values()[getCoronationPiece()].toString());
			boolean finded = matcher.find();
			if(!finded)
				throw new IllegalArgumentException("Invalid move.");
			String promotion = matcher.group("piece").toLowerCase();
			return collum + y + collum_ + y_ + promotion;// UCI notation;
		}
		
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
