package chessapi4j.core;

import chessapi4j.Move;
import chessapi4j.Play;
import chessapi4j.Position;

/**
 * Factory class for Play interface.
 * @author lunalobos
 *
 */
public class PlayFactory {
	public static Play instance(Position p, Move move) {
		return new BitPlay(GeneratorFactory.instance(p),move);
	}
}
