package chessapi4j.core;

import chessapi4j.Position;

/**
 * 
 * @author lunalobos
 *
 */
class BitPosition extends AbstractPosition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 853958284834069674L;
	private long[] bits;
	private long whiteMoveNumeric, shortCastleWhiteNumeric, longCastleWhiteNumeric, shortCastleBlackNumeric,
			longCastleBlackNumeric;

	public BitPosition() {
		super();
		bits = new long[12];
		bits[0] = 0b0000000000000000000000000000000000000000000000001111111100000000L;
		bits[1] = 0b0000000000000000000000000000000000000000000000000000000001000010L;
		bits[2] = 0b0000000000000000000000000000000000000000000000000000000000100100L;
		bits[3] = 0b0000000000000000000000000000000000000000000000000000000010000001L;
		bits[4] = 0b0000000000000000000000000000000000000000000000000000000000001000L;
		bits[5] = 0b0000000000000000000000000000000000000000000000000000000000010000L;
		bits[6] = 0b0000000011111111000000000000000000000000000000000000000000000000L;
		bits[7] = 0b0100001000000000000000000000000000000000000000000000000000000000L;
		bits[8] = 0b0010010000000000000000000000000000000000000000000000000000000000L;
		bits[9] = 0b1000000100000000000000000000000000000000000000000000000000000000L;
		bits[10] = 0b0000100000000000000000000000000000000000000000000000000000000000L;
		bits[11] = 0b0001000000000000000000000000000000000000000000000000000000000000L;
		whiteMoveNumeric = 1L;
		shortCastleWhiteNumeric = 1L;
		longCastleWhiteNumeric = 1L;
		shortCastleBlackNumeric = 1L;
		longCastleBlackNumeric = 1L;
		setMovesCounter(1);
	}

	public BitPosition(long[] bits, int enPassant, long whiteMoveNumeric, long shortCastleWhiteNumeric,
			long shortCastleBlackNumeric, long longCastleWhiteNumeric, long longCastleBlackNumeric, int movesCounter,
			int halfMovesCounter) {
		super(enPassant, false, false, false, false, false, movesCounter, halfMovesCounter, false, false, false, false,
				false);
		this.bits = bits;
		this.whiteMoveNumeric = whiteMoveNumeric;
		this.shortCastleWhiteNumeric = shortCastleWhiteNumeric;
		this.longCastleWhiteNumeric = longCastleWhiteNumeric;
		this.shortCastleBlackNumeric = shortCastleBlackNumeric;
		this.longCastleBlackNumeric = longCastleBlackNumeric;
	}

	public BitPosition(String fen) {
		fromFen(fen);
	}

	public int[] getSquares() {
		int[] squares = new int[64];
		for (int i = 0; i < 64; i++) {
			int piece = 0;
			for (int j = 1; j < 13; j++) {
				piece += j * (int) (((1L << i) & bits[j - 1]) >>> i);
			}
			squares[i] = piece;
		}
		return squares;
	}

	@Override
	public long[] getBits() {
		return bits;
	}

	@Override
	public void setSquares(int[] squares) {
		bits = new long[12];
		for (int i = 0; i < 64; i++) {
			if (squares[i] > 0) {
				bits[squares[i] - 1] = bits[squares[i] - 1] | (1L << i);
			}
		}
	}

	@Override
	public Position makeClone() {
		return new BitPosition(bits.clone(), getEnPassant(), whiteMoveNumeric, shortCastleWhiteNumeric,
				shortCastleBlackNumeric, longCastleWhiteNumeric, longCastleBlackNumeric, getMovesCounter(),
				getHalfMovesCounter());
	}

	@Override
	public int hashCode() {
		final int prime = 103963;
		int hash = 1;
		for (long bitBoard : getBits()) {
			hash = hash * prime + (int) (bitBoard ^ (bitBoard >>> 32));
		}
		hash = hash * prime + (int) whiteMoveNumeric;
		hash = hash * prime + (int) shortCastleWhiteNumeric;
		hash = hash * prime + (int) longCastleWhiteNumeric;
		hash = hash * prime + (int) shortCastleBlackNumeric;
		hash = hash * prime + (int) longCastleBlackNumeric;
		hash = hash * prime + getEnPassant();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BitPosition))
			return false;
		if (obj == this)
			return true;
		BitPosition o = (BitPosition) obj;
		long op = 0L;
		for (int i = 0; i < 12; i++) {
			op = op | (bits[i] ^ o.getBits()[i]);
		}
		op = op | (whiteMoveNumeric ^ o.getWhiteMoveNumeric());
		op = op | (shortCastleWhiteNumeric ^ o.getShortCastleWhiteNumeric());
		op = op | (longCastleWhiteNumeric ^ o.getLongCastleWhiteNumeric());
		op = op | (shortCastleBlackNumeric ^ o.getShortCastleBlackNumeric());
		op = op | (longCastleBlackNumeric ^ o.getLongCastleBlackNumeric());
		op = op | (long) (getEnPassant() ^ o.getEnPassant());
		return op == 0L;
	}

	@Override
	public String toString() {
		return stringRepresentation();
	}

	@Override
	public void setBits(long[] bits) {
		this.bits = bits;

	}

	public long getWhiteMoveNumeric() {
		return whiteMoveNumeric;
	}

	public long getShortCastleWhiteNumeric() {
		return shortCastleWhiteNumeric;
	}

	public long getLongCastleWhiteNumeric() {
		return longCastleWhiteNumeric;
	}

	public long getShortCastleBlackNumeric() {
		return shortCastleBlackNumeric;
	}

	public long getLongCastleBlackNumeric() {
		return longCastleBlackNumeric;
	}

	public void setWhiteMoveNumeric(long whiteMoveNumeric) {
		this.whiteMoveNumeric = whiteMoveNumeric;
	}

	public void setShortCastleWhiteNumeric(long shortCastleWhiteNumeric) {
		this.shortCastleWhiteNumeric = shortCastleWhiteNumeric;
	}

	public void setLongCastleWhiteNumeric(long longCastleWhiteNumeric) {
		this.longCastleWhiteNumeric = longCastleWhiteNumeric;
	}

	public void setShortCastleBlackNumeric(long shortCastleBlackNumeric) {
		this.shortCastleBlackNumeric = shortCastleBlackNumeric;
	}

	public void setLongCastleBlackNumeric(long longCastleBlackNumeric) {
		this.longCastleBlackNumeric = longCastleBlackNumeric;
	}

	@Override
	public void setShortCastleWhite(boolean shortCastleWhite) {
		shortCastleWhiteNumeric = shortCastleWhite ? 1L : 0L;
	}

	@Override
	public void setLongCastleWhite(boolean longCastleWhite) {
		longCastleWhiteNumeric = longCastleWhite ? 1L : 0L;
	}

	@Override
	public void setShortCastleBlack(boolean shortCastleBlack) {
		shortCastleBlackNumeric = shortCastleBlack ? 1L : 0L;
	}

	@Override
	public void setLongCastleBlack(boolean longCastleBlack) {
		longCastleBlackNumeric = longCastleBlack ? 1L : 0L;
	}

	@Override
	public boolean isWhiteMove() {
		if (getWhiteMoveNumeric() == 1L)
			return true;
		else
			return false;
	}

	@Override
	public void setWhiteMove(boolean whiteMove) {
		setWhiteMoveNumeric(whiteMove ? 1L : 0L);
	}

	@Override
	public boolean isShortCastleWhite() {
		return getShortCastleWhiteNumeric() == 1L;

	}

	@Override
	public boolean isShortCastleBlack() {
		return getShortCastleBlackNumeric() == 1L;

	}

	@Override
	public boolean isLongCastleWhite() {
		return getLongCastleWhiteNumeric() == 1L;

	}

	@Override
	public boolean isLongCastleBlack() {
		return getLongCastleBlackNumeric() == 1L;

	}

	@Override
	public void increaseMovesCounter() {
		setMovesCounter(getMovesCounter() + (int) (1L & getWhiteMoveNumeric()));
	}

	@Override
	public void changeColorToMove() {
		whiteMoveNumeric = ~whiteMoveNumeric & 1L;
	}

	@Override
	public void changePieceBit(long bitRepresentation, int piece) {
		getBits()[piece - 1] = bitRepresentation;
	}

}