package chessapi4j.core;

import chessapi4j.Generator;
import chessapi4j.Position;

/**
 * Factory class for Generator interface.
 *
 * @author lunalobos
 *
 */
public class GeneratorFactory {

	public static Generator instance(Position pos) {
		BitPosition p;
		if(pos instanceof BitPosition)
			p = (BitPosition) pos;
		else
			p = new BitPosition(pos.toFen());
		return new MoveGenerator(p);
	}

	public static Generator instance(Position pos, String mode) {
		BitPosition p;
		if(pos instanceof BitPosition)
			p = (BitPosition) pos;
		else
			p = new BitPosition(pos.toFen());

		switch (mode) {
		case "hashed":
			return new HashedGenerator(p);
		case "normal":
			return new MoveGenerator(p);
		default:
			return new MoveGenerator(p);
		}
	}

}
