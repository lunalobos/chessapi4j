package chessapi4j.core;

import chessapi4j.Move;

/**
 * 
 * @author lunalobos
 *
 */
class BitMove implements Move {
	
	private long move;
	private int origin, coronationPiece;
	// null move
	public BitMove() {
		move = -1L;
		origin = -1;
		coronationPiece = -1;
	}
	
	public BitMove(long move, int origin, int coronationPiece) {
		this.move = move;
		this.origin = origin;
		this.coronationPiece = coronationPiece;
	}

	@Override
	public int getOrigin() {

		return origin;
	}
	
	public long getMove() {
		return move;
	}
	
	@Override
	public int getDestiny() {

		return Long.numberOfTrailingZeros(move);
	}

	@Override
	public int getCoronationPiece() {

		return coronationPiece;
	}

	@Override
	public void setOrigin(int origin) {
		this.origin = origin;
		
	}

	@Override
	public void setDestiny(int destiny) {
		move = 1L<<destiny;
		
	}

	@Override
	public void setCoronationPiece(int coronationPiece) {
		this.coronationPiece = coronationPiece;
		
	}
	
	@Override
	public String toString() {
		if (Long.bitCount(move) > 1)
			return "0000";// null move
		else
			return stringRepresentation();
	}
	
	@Override
	public boolean equals(Object o) {
		return areEquals(o);
	}

}
