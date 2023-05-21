package chessapi4j;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import chessapi4j.core.Util;

/**
 * Interface for position representation.
 * 
 * @author lunalobos
 *
 */
public interface Position extends Serializable {

	/**
	 * Interchange players turn to move.
	 */
	default void changeColorToMove() {
		setWhiteMove(!isWhiteMove());
	}

	default void fromFen(String fen) {
		int[] squares = new int[64];

		String[] parts = fen.split(" ");

		if (parts.length != 6)
			throw new IllegalArgumentException("The string parsed to this constructor is not a normalized fen string");

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

		if (castlePart.equals("-")) {
			setShortCastleWhite(false);
			setShortCastleBlack(false);
			setLongCastleWhite(false);
			setLongCastleBlack(false);
		} else {
			char[] chars = castlePart.toCharArray();
			setShortCastleWhite(false);
			setShortCastleBlack(false);
			setLongCastleWhite(false);
			setLongCastleBlack(false);
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

		setCheckmate(false);
		setFiftyMoves(false);
		setLackOfMaterial(false);
		setRepetitions(false);
		setStalemate(false);
	}

	/**
	 * Returns the bitboards array in Piece ordinal order excluding EMPTY.
	 * <p>
	 * This means that index 0 represents white pawns, index 1 represents white
	 * knights and so on.
	 * 
	 * @return the array of bitboards
	 */
	long[] getBits();

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
	 * @return
	 */
	int getEnPassant();
	
	/**
	 * Returns the half moves number according to fifty moves rules.
	 * @return the half moves number
	 */
	int getHalfMovesCounter();

	/**
	 * Returns the number of complete moves.
	 * @return the number of moves
	 */
	int getMovesCounter();

	/**
	 * This method returns a 64 length array. It contains the piece value for each
	 * square according to Piece's ordinal values.
	 * <p>
	 * Each index represents a square, the value represents a piece
	 * <p>
	 * ---------Board---------
	 * <p>
	 * 56 57 58 59 60 61 62 63
	 * <p>
	 * 48 49 50 51 52 53 54 55
	 * <p>
	 * 40 41 42 43 44 45 46 47
	 * <p>
	 * 32 33 34 35 36 37 38 39
	 * <p>
	 * 24 25 26 27 28 29 30 31
	 * <p>
	 * 16 17 18 19 20 21 22 23
	 * <p>
	 * 08 09 10 11 12 13 14 15
	 * <p>
	 * 00 01 02 03 04 05 06 07
	 * <p>
	 * 
	 * @returns the squares array
	 */
	int[] getSquares();

	/**
	 * default hash implementation
	 * @return
	 */
	default int hash() {
		int hash = 56;
		int[] squares = getSquares();
		for (int i = 0; i < 64; i++) {
			hash += squares[i];
		}
		hash += getEnPassant();
		hash += isShortCastleWhite() ? 1 : 0;
		hash += isShortCastleBlack() ? 1 : 0;
		hash += isLongCastleWhite() ? 1 : 0;
		hash += isLongCastleBlack() ? 1 : 0;

		return hash;
	}

	/**
	 * Increments movesCounter.
	 */
	void increaseMovesCounter();

	/**
	 * If this method returns true means this position is checkmate.
	 */
	boolean isCheckmate();

	/**
	 * If this method returns true means this position can be draw according to
	 * Fifty Moves's rule.
	 */
	boolean isFiftyMoves();

	/**
	 * If this method returns true means this position is a draw because of lack of
	 * material.
	 */
	boolean isLackOfMaterial();

	/**
	 * If this method returns true means black is able to castle queen side.
	 */
	boolean isLongCastleBlack();

	/**
	 * If this method returns true means white is able to castle queen side.
	 */
	boolean isLongCastleWhite();

	/**
	 * If this method returns true means this position is a draw because of
	 * repetitions.
	 */
	boolean isRepetitions();

	/**
	 * If this method returns true means black is able to castle king side.
	 */
	boolean isShortCastleBlack();

	/**
	 * If this method returns true means white is able to castle king side.
	 */
	boolean isShortCastleWhite();

	/**
	 * If this method returns true means this position is a draw because of
	 * stalemate.
	 */
	boolean isStalemate();

	/**
	 * If this method returns true means it is white turn to move, otherwise it is
	 * black turn.
	 */
	boolean isWhiteMove();

	/**
	 * Returns a deep clone of this object.
	 */
	Position makeClone();

	/**
	 * Sets the array of bitboards, length has to be always 12.
	 * <p> Setting a different length array will lead to unpredictable behavior.
	 * @param bits
	 */
	void setBits(long[] bits);

	/**
	 * Sets checkmate boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param checkmate
	 */
	void setCheckmate(boolean checkmate);

	/**
	 * Sets en passant value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param enPassant
	 */
	void setEnPassant(int enPassant);

	/**
	 * Sets fifty moves boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param fiftyMoves
	 */
	void setFiftyMoves(boolean fiftyMoves);

	/**
	 * Sets half moves counter value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param halfMovesCounter
	 */
	void setHalfMovesCounter(int halfMovesCounter);

	/**
	 * Sets lack of material boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param lackOfMaterial
	 */
	void setLackOfMaterial(boolean lackOfMaterial);

	/**
	 * Sets long castle black boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param longCastleBlack
	 */
	void setLongCastleBlack(boolean longCastleBlack);

	/**
	 * Sets long castle white boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param longCastleWhite
	 */
	void setLongCastleWhite(boolean longCastleWhite);

	/**
	 * Sets moves counter value.
	 * 
	 * @param movesCounter
	 */
	void setMovesCounter(int movesCounter);

	/**
	 * Sets repetitions boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param repetitions
	 */
	void setRepetitions(boolean repetitions);

	/**
	 * Sets short castle black boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param shortCastleBlack
	 */
	void setShortCastleBlack(boolean shortCastleBlack);

	/**
	 * Sets a particular bitboard according to the pieceOrdinal parameter.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param bitRepresentation, pieceOrdinal
	 */
	void changePieceBit(long bitRepresentation, int pieceOrdinal);
	
	/**
	 * Sets short castle white boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param shortCastleWhite
	 */
	void setShortCastleWhite(boolean shortCastleWhite);

	/**
	 * Sets the squares array. This will be reflected in the the bits array.
	 * @param squares
	 */
	void setSquares(int[] squares);

	/**
	 * Sets stalemate boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param stalemate
	 */
	void setStalemate(boolean stalemate);

	/**
	 * Sets whiteMove boolean value.
	 * <p> Setting this value manually could potentially lead to inconsistencies.
	 * @param whiteMove
	 */
	void setWhiteMove(boolean whiteMove);

	/**
	 * Default method for string representation.
	 * @return
	 */
	default String stringRepresentation() {
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
	 * Default method for fen representation.
	 * @return
	 */
	default String toFen() {
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
}