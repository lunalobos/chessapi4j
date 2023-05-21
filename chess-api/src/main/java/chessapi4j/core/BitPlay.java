package chessapi4j.core;

import java.util.List;

import chessapi4j.Generator;
import chessapi4j.Move;
import chessapi4j.MovementException;
import chessapi4j.Play;
import chessapi4j.Position;


/**
 * 
 * @author lunalobos
 *
 */
class BitPlay implements Play {
	
	Generator g;
	Move m;

	public BitPlay(Generator g, Move m) {
		super();
		Position np = (Position)g.getPosition().makeClone();
		g.setPosition(np);
		this.g = g;
		this.m = m;

	}

	@Override
	public void executeMove() throws MovementException {
		MoveDetector d = new MoveDetector(g, null);
		List<? extends Position> childs = d.getChilds();
		for(Position child : childs) {
			d.setChild(child);
			Move move = d.getMove();
			
			if(!(child instanceof BitPosition)) {
				child = new BitPosition(child.toFen());
			}
			if (move.equals(m))
				g.setPosition(child);
		}
		if(!childs.contains(g.getPosition()))
			throw new MovementException("Illegal move.");
	}

	@Override
	public Position getPosition() {
		return g.getPosition();
	}

	@Override
	public Move getMove() {
		return m;
	}

}
