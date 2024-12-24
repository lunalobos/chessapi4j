/*
 * Copyright 2024 Miguel Angel Luna Lobos
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Position representation
 *
 * @author lunalobos
 * @since 1.0.0
 */
public final class Position implements Serializable {
	private static final long serialVersionUID = -3129022190813874561L;

	private long[] bits;
	private long whiteMoveNumeric, shortCastleWhiteNumeric, longCastleWhiteNumeric, shortCastleBlackNumeric,
			longCastleBlackNumeric;
	private int enPassant;

	private int movesCounter;// moves counter
	private int halfMovesCounter;// for 50 move draw rule

	private transient boolean checkmate;
	private transient boolean stalemate;
	private transient boolean fiftyMoves;
	private transient boolean repetitions;
	private transient boolean lackOfMaterial;

	/**
	 * Creates a new position with the started position.
	 */
	public Position() {
		enPassant = -1;
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

	protected Position(long[] bits, int enPassant, long whiteMoveNumeric, long shortCastleWhiteNumeric,
			long shortCastleBlackNumeric, long longCastleWhiteNumeric, long longCastleBlackNumeric, int movesCounter,
			int halfMovesCounter) {
		this.enPassant = enPassant;

		this.movesCounter = movesCounter;
		this.halfMovesCounter = halfMovesCounter;
		this.checkmate = false;
		this.stalemate = false;
		this.fiftyMoves = false;
		this.repetitions = false;
		this.lackOfMaterial = false;
		this.bits = bits;
		this.whiteMoveNumeric = whiteMoveNumeric;
		this.shortCastleWhiteNumeric = shortCastleWhiteNumeric;
		this.longCastleWhiteNumeric = longCastleWhiteNumeric;
		this.shortCastleBlackNumeric = shortCastleBlackNumeric;
		this.longCastleBlackNumeric = longCastleBlackNumeric;
	}

	/**
	 * Constructs a Position from a FEN string.
	 * 
	 * <p>
	 * The FEN string must be valid.
	 * </p>
	 * 
	 * @param fen
	 */
	public Position(String fen) {
		fromFen(fen);
	}

	/**
	 * Returns the black short castle rights as a long.
	 * 
	 * @return the black short castle rights as a long
	 */
	public final long bk() {
		return shortCastleBlackNumeric;
	}

	/**
	 * Returns the black long castle rights as a long.
	 * 
	 * @return the black long castle rights as a long
	 */
	public final long bq() {
		return longCastleBlackNumeric;
	}

	/**
	 * Interchange players turn to move.
	 */
	public final void changeColorToMove() {
		whiteMoveNumeric = ~whiteMoveNumeric & 1L;
	}

	/**
	 * Sets a particular bitboard according to the pieceOrdinal parameter.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param bitRepresentation, pieceOrdinal
	 */
	public final void changePieceBit(long bitRepresentation, int piece) {
		getBits()[piece - 1] = bitRepresentation;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Position))
			return false;
		if (obj == this)
			return true;
		Position o = (Position) obj;
		long op = 0L;
		for (int i = 0; i < 12; i++) {
			op = op | (bits[i] ^ o.getBits()[i]);
		}
		op = op | (whiteMoveNumeric ^ o.wm());
		op = op | (shortCastleWhiteNumeric ^ o.wk());
		op = op | (longCastleWhiteNumeric ^ o.wq());
		op = op | (shortCastleBlackNumeric ^ o.bk());
		op = op | (longCastleBlackNumeric ^ o.bq());
		op = op | (long) (enPassant ^ o.enPassant);
		return op == 0L;
	}

	private void fromFen(String fen) {
		int[] squares = new int[64];

		String[] parts = fen.split(" ");

		String[] rows = parts[0].split("/");
		List<Character> emptyPossibilities = new LinkedList<>(
				Arrays.asList(new Character[] { '1', '2', '3', '4', '5', '6', '7', '8' }));
		for (int h = 7; h >= 0; h--) {
			char[] chars = rows[7 - h].toCharArray();

			int i = 0;// colum counter

			for (char character : chars) {

				if (emptyPossibilities.contains(character)) {
					for (int j = 0; j < Integer.parseInt(new StringBuilder().append(character).toString()); j++) {
						i++;
					}
				} else {
					switch (character) {
						case 'P':
							squares[h * 8 + i] = Piece.WP.ordinal();
							break;
						case 'N':
							squares[h * 8 + i] = Piece.WN.ordinal();
							break;
						case 'B':
							squares[h * 8 + i] = Piece.WB.ordinal();
							break;
						case 'R':
							squares[h * 8 + i] = Piece.WR.ordinal();
							break;
						case 'Q':
							squares[h * 8 + i] = Piece.WQ.ordinal();
							break;
						case 'K':
							squares[h * 8 + i] = Piece.WK.ordinal();
							break;
						case 'p':
							squares[h * 8 + i] = Piece.BP.ordinal();
							break;
						case 'n':
							squares[h * 8 + i] = Piece.BN.ordinal();
							break;
						case 'b':
							squares[h * 8 + i] = Piece.BB.ordinal();
							break;
						case 'r':
							squares[h * 8 + i] = Piece.BR.ordinal();
							break;
						case 'q':
							squares[h * 8 + i] = Piece.BQ.ordinal();
							break;
						case 'k':
							squares[h * 8 + i] = Piece.BK.ordinal();
							break;
					}
					i++;
				}
			}
		}
		setSquares(squares);
		if (parts[1].equals("w"))
			setWhiteMove(true);
		else
			setWhiteMove(false);

		String castlePart = parts[2];

		if (!castlePart.equals("-")) {
			char[] chars = castlePart.toCharArray();
			for (char character : chars) {
				switch (character) {
					case 'K':
						setShortCastleWhite(true);
						break;
					case 'k':
						setShortCastleBlack(true);
						break;
					case 'Q':
						setLongCastleWhite(true);
						break;
					case 'q':
						setLongCastleBlack(true);
						break;
				}
			}
		}
		String enPassantPart = parts[3];
		if (enPassantPart.equals("-")) {
			setEnPassant(-1);
		} else {
			char[] chars = enPassantPart.toCharArray();
			String col = "" + chars[0];
			int x = Util.getColIndex(col);
			int y;
			if (Integer.parseInt("" + chars[1]) == 6)
				y = 4;
			else if (Integer.parseInt("" + chars[1]) == 3)
				y = 3;
			else
				throw new IllegalArgumentException("Invalid en passant string");

			setEnPassant(Util.getSquareIndex(x, y));
		}

		setHalfMovesCounter(Integer.parseInt(parts[4]));
		if (getHalfMovesCounter() > 50 || getHalfMovesCounter() < 0)
			throw new IllegalArgumentException("Half moves has to be over -1 and bellow 51.");

		setMovesCounter(Integer.parseInt(parts[5]));

	}

	/**
	 * Returns the bitboards array in {@code Piece} ordinal order excluding EMPTY.
	 * <p>
	 * This means that index 0 represents white pawns, index 1 represents white
	 * knights and so on.
	 *
	 * @return the array of bitboards
	 */
	public final long[] getBits() {
		return bits;
	}

	/**
	 * Returns the index square of the piece that can be capture using en passant
	 * rule, even when is not possible for any pawn to reach.
	 * <p>
	 * This is no the square that is shown in fen representation, is the pawn that
	 * can be capture location. Fen representation shows the place where an
	 * hypothetical pawn will end after using the en passant rule to capture a pawn
	 * in the represented position.
	 * <p>
	 * For a position like "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3
	 * 0 1" this method will return the square index for e4, 28.
	 *
	 * @return the index square of the piece that can be capture using en passant
	 *         rule
	 */
	public final int getEnPassant() {
		return enPassant;
	}

	/**
	 * Returns the half moves number according to fifty moves rules.
	 *
	 * @return the half moves number
	 */
	public final int getHalfMovesCounter() {
		return halfMovesCounter;
	}

	/**
	 * Returns the number of complete moves.
	 *
	 * @return the number of moves
	 */
	public final int getMovesCounter() {
		return movesCounter;
	}

	/**
	 * This method returns a 64 length array. It contains the piece value for each
	 * square according to {@code Piece} ordinal values.
	 * <p>
	 * Each index represents a square, the value represents a piece
	 * <p>
	 *
	 * {@code ---------Board---------}
	 * <p>
	 * {@code 56 57 58 59 60 61 62 63}
	 * <p>
	 * {@code 48 49 50 51 52 53 54 55}
	 * <p>
	 * {@code 40 41 42 43 44 45 46 47}
	 * <p>
	 * {@code 32 33 34 35 36 37 38 39}
	 * <p>
	 * {@code 24 25 26 27 28 29 30 31}
	 * <p>
	 * {@code 16 17 18 19 20 21 22 23}
	 * <p>
	 * {@code 08 09 10 11 12 13 14 15}
	 * <p>
	 * {@code 00 01 02 03 04 05 06 07}
	 * <p>
	 *
	 * @returns the squares array
	 */
	public final int[] getSquares() {
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

	/**
	 * Increments movesCounter when white moves.
	 */
	public final void increaseMovesCounter() {
		setMovesCounter(getMovesCounter() + (int) (1L & wm()));
	}

	/**
	 * If this method returns true means this position is checkmate.
	 */
	public final boolean isCheckmate() {
		return checkmate;
	}

	/**
	 * If this method returns true means this position can be draw according to
	 * Fifty Moves's rule.
	 */
	public final boolean isFiftyMoves() {
		return fiftyMoves;
	}

	/**
	 * If this method returns true means this position is a draw because of lack of
	 * material.
	 */
	public final boolean isLackOfMaterial() {
		return lackOfMaterial;
	}

	/**
	 * If this method returns true means black is able to castle queen side.
	 */
	public final boolean isLongCastleBlack() {
		return bq() == 1L;
	}

	/**
	 * If this method returns true means white is able to castle queen side.
	 */
	public final boolean isLongCastleWhite() {
		return wq() == 1L;
	}

	/**
	 * If this method returns true means this position is a draw because of
	 * repetitions.
	 */
	public final boolean isRepetitions() {
		return repetitions;
	}

	/**
	 * If this method returns true means black is able to castle king side.
	 */
	public final boolean isShortCastleBlack() {
		return bk() == 1L;
	}

	/**
	 * If this method returns true means white is able to castle king side.
	 */
	public final boolean isShortCastleWhite() {
		return wk() == 1L;
	}

	/**
	 * If this method returns true means this position is a draw because of
	 * stalemate.
	 */
	public final boolean isStalemate() {
		return stalemate;
	}

	/**
	 * If this method returns true means it is white turn to move, otherwise it is
	 * black turn.
	 */
	public final boolean isWhiteMove() {
		return wm() == 1L;
	}

	/**
	 * Returns the side to move.
	 *
	 * @return the side to move
	 * @since 1.2.3
	 */
	public final Side sideToMove() {
		return Side.values()[(int) whiteMoveNumeric];
	}

	/**
	 * Returns a deep clone of this object.
	 */
	public final Position makeClone() {
		return new Position(bits.clone(), getEnPassant(), whiteMoveNumeric, shortCastleWhiteNumeric,
				shortCastleBlackNumeric, longCastleWhiteNumeric, longCastleBlackNumeric, getMovesCounter(),
				getHalfMovesCounter());
	}

	/**
	 * Sets the array of bitboards, length has to be always 12.
	 * <p>
	 * Setting a different length array will lead to unpredictable behavior.
	 *
	 * @param bits
	 */
	public final void setBits(long[] bits) {
		this.bits = bits;

	}

	/**
	 * Sets black short castle rights as a long.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param bk
	 *           black short castle rights as a long
	 */
	public final void setBK(long bk) {
		this.shortCastleBlackNumeric = bk;
	}

	/**
	 * Sets black long castle rights as a long.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param bq
	 *           black long castle rights as a long
	 */
	public final void setBQ(long bq) {
		this.longCastleBlackNumeric = bq;
	}

	/**
	 * Sets checkmate boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param checkmate
	 */
	public final void setCheckmate(boolean checkmate) {
		this.checkmate = checkmate;
	}

	/**
	 * Sets en passant value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param enPassant
	 */
	public final void setEnPassant(int enPassant) {
		this.enPassant = enPassant;
	}

	/**
	 * Sets fifty moves boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param fiftyMoves
	 */
	public final void setFiftyMoves(boolean fiftyMoves) {
		this.fiftyMoves = fiftyMoves;
	}

	/**
	 * Sets half moves counter value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param halfMovesCounter
	 */
	public final void setHalfMovesCounter(int halfMovesCounter) {
		this.halfMovesCounter = halfMovesCounter;
	}

	/**
	 * Sets lack of material boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param lackOfMaterial
	 */
	public final void setLackOfMaterial(boolean lackOfMaterial) {
		this.lackOfMaterial = lackOfMaterial;
	}

	/**
	 * Sets long castle black boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param longCastleBlack
	 */
	public final void setLongCastleBlack(boolean longCastleBlack) {
		longCastleBlackNumeric = longCastleBlack ? 1L : 0L;
	}

	/**
	 * Sets long castle white boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param longCastleWhite
	 */
	public final void setLongCastleWhite(boolean longCastleWhite) {
		longCastleWhiteNumeric = longCastleWhite ? 1L : 0L;
	}

	/**
	 * Sets moves counter value.
	 *
	 * @param movesCounter
	 */
	public final void setMovesCounter(int movesCounter) {
		this.movesCounter = movesCounter;
	}

	/**
	 * Sets repetitions boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param repetitions
	 */
	public final void setRepetitions(boolean repetitions) {
		this.repetitions = repetitions;
	}

	/**
	 * Sets short castle black boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param shortCastleBlack
	 */
	public final void setShortCastleBlack(boolean shortCastleBlack) {
		shortCastleBlackNumeric = shortCastleBlack ? 1L : 0L;
	}

	/**
	 * Sets short castle white boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param shortCastleWhite
	 */
	public final void setShortCastleWhite(boolean shortCastleWhite) {
		shortCastleWhiteNumeric = shortCastleWhite ? 1L : 0L;
	}

	/**
	 * Sets the squares array. This will be reflected in the the bits array.
	 *
	 * @param squares
	 */
	public final void setSquares(int[] squares) {
		bits = new long[12];
		for (int i = 0; i < 64; i++) {
			if (squares[i] > 0) {
				bits[squares[i] - 1] = bits[squares[i] - 1] | (1L << i);
			}
		}
	}

	/**
	 * Sets stalemate boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param stalemate
	 */
	public final void setStalemate(boolean stalemate) {
		this.stalemate = stalemate;
	}

	/**
	 * Sets whiteMove boolean value.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param whiteMove
	 */
	public final void setWhiteMove(boolean whiteMove) {
		setWM(whiteMove ? 1L : 0L);
	}

	/**
	 * Sets the white king side castle rights as a long.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param wk
	 *           the white king side castle rights as a long
	 */
	public final void setWK(long wk) {
		this.shortCastleWhiteNumeric = wk;
	}

	/**
	 * Sets white move rights as a long, meaning 1 for white to move and 0 for black
	 * to move.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param wm
	 */
	public final void setWM(long wm) {
		this.whiteMoveNumeric = wm;
	}

	/**
	 * Sets white long castle rights as a long.
	 * <p>
	 * Setting this value manually could potentially lead to inconsistencies.
	 *
	 * @param wq
	 */
	public final void setWQ(long wq) {
		this.longCastleWhiteNumeric = wq;
	}

	/**
	 * Method for string representation.
	 *
	 * @return
	 */
	private String stringRepresentation() {
		int[] squares = getSquares();
		StringBuilder sb = new StringBuilder();
		sb.append("\n+---+---+---+---+---+---+---+---+ \n");
		for (int i = 7; i >= 0; i--) {
			for (int j = 0; j < 8; j++) {
				switch (Piece.values()[squares[i * 8 + j]]) {
					case WP:
						sb.append("| P ");
						break;
					case WN:
						sb.append("| N ");
						break;
					case WB:
						sb.append("| B ");
						break;
					case WR:
						sb.append("| R ");
						break;
					case WQ:
						sb.append("| Q ");
						break;
					case WK:
						sb.append("| K ");
						break;
					case BP:
						sb.append("| p ");
						break;
					case BN:
						sb.append("| n ");
						break;
					case BB:
						sb.append("| b ");
						break;
					case BR:
						sb.append("| r ");
						break;
					case BQ:
						sb.append("| q ");
						break;
					case BK:
						sb.append("| k ");
						break;
					default:
						sb.append("|   ");
				}
				if (j == 7) {
					int row = i + 1;
					sb.append("| " + row + "\n");
					sb.append("+---+---+---+---+---+---+---+---+ \n");
				}
			}
		}
		sb.append("  a   b   c   d   e   f   g   h \n");
		sb.append("Fen: " + toFen());
		return sb.toString();
	}

	/**
	 * Method for fen representation.
	 *
	 * @return
	 */
	public final String toFen() {
		StringBuilder fenSB = new StringBuilder();
		int[] squares = getSquares();
		for (int i = 7; i >= 0; i--) {
			StringBuilder rowFenSB = new StringBuilder();
			int emptyCounter = 0;
			for (int j = i * 8; j < i * 8 + 8; j++) {
				if (squares[j] == Piece.EMPTY.ordinal())
					emptyCounter++;
				else if (emptyCounter != 0) {
					rowFenSB.append((Integer) emptyCounter);
					emptyCounter = 0;
				}
				if (squares[j] == Piece.EMPTY.ordinal() && j == i * 8 + 7)
					rowFenSB.append((Integer) emptyCounter);
				switch (Piece.values()[squares[j]]) {
					case WP:
						rowFenSB.append("P");
						break;
					case WN:
						rowFenSB.append("N");
						break;
					case WB:
						rowFenSB.append("B");
						break;
					case WR:
						rowFenSB.append("R");
						break;
					case WQ:
						rowFenSB.append("Q");
						break;
					case WK:
						rowFenSB.append("K");
						break;
					case BP:
						rowFenSB.append("p");
						break;
					case BN:
						rowFenSB.append("n");
						break;
					case BB:
						rowFenSB.append("b");
						break;
					case BR:
						rowFenSB.append("r");
						break;
					case BQ:
						rowFenSB.append("q");
						break;
					case BK:
						rowFenSB.append("k");
						break;
					default:
						rowFenSB.append("");
				}
			}
			if (i == 7)
				fenSB.append(rowFenSB);
			else
				fenSB.append("/").append(rowFenSB);
		}
		String sideToMove = isWhiteMove() ? "w" : "b";
		String castleAbility = "";

		if (isShortCastleWhite())
			castleAbility += "K";
		if (isLongCastleWhite())
			castleAbility += "Q";
		if (isShortCastleBlack())
			castleAbility += "k";
		if (isLongCastleBlack())
			castleAbility += "q";

		if (!isShortCastleWhite() && !isLongCastleWhite() && !isShortCastleBlack() && !isLongCastleBlack())
			castleAbility += "-";
		String enPassant;
		if (getEnPassant() == -1)
			enPassant = "-";
		else
			enPassant = "" + Util.getColLetter(Util.getCol(getEnPassant()))
					+ (Util.getRow(getEnPassant()) == 3 ? 3 : 6);
		String halfMoveClock = "" + getHalfMovesCounter();

		String fullMoveCounter = "" + getMovesCounter();

		fenSB.append(" ").append(sideToMove).append(" ").append(castleAbility).append(" ").append(enPassant).append(" ")
				.append(halfMoveClock).append(" ").append(fullMoveCounter);

		return fenSB.toString();
	}

	@Override
	public String toString() {
		return stringRepresentation();
	}

	/**
	 * Returns the white short castling rights as a long.
	 * 
	 * @return the white short castling rights as a long
	 */
	public final long wk() {
		return shortCastleWhiteNumeric;
	}

	/**
	 * Returns the white move rights as a long, meaning 1 for white to move and 0
	 * for black to move.
	 * 
	 * @return the white move rights as a long
	 */
	public final long wm() {
		return whiteMoveNumeric;
	}

	/**
	 * Returns the white long castling rights as a long.
	 * 
	 * @return the white long castling rights as a long
	 */
	public final long wq() {
		return longCastleWhiteNumeric;
	}

	/**
	 * Returns the position resulting from the given move.
	 *
	 * @param move
	 * @return the position resulting from the given move
	 */
	public Optional<Position> childFromMove(Move move) {
		Generator generator = GeneratorFactory.instance();
		List<Position> children = generator.generateChildren(this);
		List<Move> moves = generator.generateMoves(this, children);
		Iterator<Move> moveIterator = moves.iterator();
		for (Position child : children) {
			if (moveIterator.next().equals(move))
				return Optional.of(child);
		}
		return Optional.empty();
	}

	/**
	 * Retrieves the {@code Piece} object that represent the piece present in th
	 * given {@code Square} object given as argument
	 *
	 * @param square
	 * @return the {@code Piece} object that represent the piece present in the
	 *         given square
	 *
	 * @since 1.2.3
	 */
	public Piece getPiece(Square square) {
		return Piece.values()[getSquares()[square.ordinal()]];
	}

	/**
	 * Retrieves the {@code Bitboard} object that represent the positions of the
	 * given {@code Piece}.
	 *
	 * @param piece
	 * @return the {@code Bitboard} object that represent the positions of the piece
	 *
	 * @since 1.2.3
	 */
	public Bitboard getBitboard(Piece piece) {
		return new Bitboard(bits[piece.ordinal() - 1]);
	}

	/**
	 * Retrieves a {@code List} with all the squares where is a piece like the given
	 * as argument.
	 *
	 * @param piece
	 * @return a {@code List} with all the squares where is a piece like the given
	 *         as argument
	 *
	 * @since 1.2.3
	 */
	public List<Square> getSquares(Piece piece) {
		return Util.longToList(bits[piece.ordinal() - 1]).stream().map(l -> Long.numberOfTrailingZeros(l))
				.map(i -> Square.get(i)).collect(LinkedList::new, LinkedList::add, LinkedList::addAll);

	}

	/**
	 * Checks if the game is over.
	 * <p>
	 * The game is over if it is checkmate, stalemate, fifty moves, lack of material
	 * or
	 * repetitions.
	 *
	 * @return true if the game is over, false otherwise
	 * 
	 * @since 1.2.6
	 */
	public boolean isGameOver() {
		return isCheckmate() || isStalemate() || isFiftyMoves() || isLackOfMaterial() || isRepetitions();
	}

	/**
	 * Retrieves if the current position is a forced draw.
	 * <p>
	 * A position is considered a draw if it results from stalemate, the fifty-move
	 * rule, insufficient material, or threefold repetition.
	 *
	 * @return true if the position is a draw, false otherwise
	 * 
	 * @since 1.2.6
	 */

	public boolean isDraw() {
		return isStalemate() || isFiftyMoves() || isLackOfMaterial() || isRepetitions();
	}
}