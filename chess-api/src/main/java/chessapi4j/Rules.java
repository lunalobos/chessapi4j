/*
 * Copyright 2025 Miguel Angel Luna Lobos
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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for game rules.
 *
 * @author lunalobos
 * @since 1.0.0
 */
public class Rules {

	private static final Pattern WK_PATTERN = Pattern.compile("K");
	private static final Pattern BK_PATTERN = Pattern.compile("k");
	private static final Pattern INVALID_ROW_PATTERN = Pattern.compile("[^PNBRQKpnbrqk12345678]");
	private static final Pattern VALID_CASTLE_ABILITY_PATTERN = Pattern.compile("^([-]{1}|([K]?[Q]?[k]?[q]?))$");
	private static final Pattern VALID_EN_PASSANT_PATTERN = Pattern.compile("^[-]{1}$|^[abcdefgh][36]$");
	private static final Pattern VALID_HALF_MOVE_CLOCK_PATTERN = Pattern.compile("^[012345679]+$");
	private static final Pattern VALID_FULL_MOVE_COUNTER_PATTERN = Pattern.compile("^[1-9][0-9]*$");
	private static final List<List<Integer>> LACK_OF_MATERIAL_MATRIX = new LinkedList<>();
	// private static final List<Integer> MATERIAL_PIECES = new
	// ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 6, 7, 8, 9, 10));
	static {
		Integer[][] matrix = new Integer[][] { { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, // K k
				{ 0, 1, 0, 0, 0, 0, 1, 0, 0, 0 }, // KN kn
				{ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, // KN k
				{ 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, // K kn
				{ 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 }, // KN kb
				{ 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 }, // KB kn
				{ 0, 0, 1, 0, 0, 0, 0, 1, 0, 0 }, // KB kb
				{ 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, // KB k
				{ 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 } // K kb
		};
		for (Integer[] array : matrix) {
			LACK_OF_MATERIAL_MATRIX.add(new LinkedList<>(Arrays.asList(array)));
		}
	}

	private static int movesCounter(Position position) {
		return GeneratorFactory.instance().generateChildren(position).size();
	}

	/**
	 * Set the internal variables checkmate, stalemate, fiftyMoves and
	 * lackOfMaterial
	 * of the given position object
	 *
	 * @param position the position to check
	 */
	public static void setStatus(Position position) {
		position.setLackOfMaterial(AdvanceUtil.lackOfMaterial(position) == 1);
		position.setFiftyMoves(position.getHalfMovesCounter() == 50);
		boolean inCheck = GeneratorFactory.generatorUtil.isInCheck(position) == 1;
		boolean noMoves = movesCounter(position) == 0;
		position.setCheckmate(noMoves && inCheck);
		position.setStalemate(noMoves && !inCheck);
	}

	/**
	 * Allows determinate if an specific move is legal for a given position.
	 *
	 * @param position the position to check
	 * @param move     the move to check
	 *
	 * @return {@code true} if the move is legal, {@code false} otherwise
	 */
	public static boolean legal(Position position, Move move) {
		return GeneratorFactory.instance()
				.generateMoves(position, GeneratorFactory.instance().generateChildren(position)).stream()
				.anyMatch(m -> m.equals(move));
	}

	/**
	 * Checks if the FEN corresponds to a legal position. This function does not
	 * verify if the position is possible to reach through valid moves; it only
	 * ensures that the basic rules are followed.
	 * 
	 * <p>
	 * Rules checked by this function:
	 * <ul>
	 * <li>Fen format: it has to be in the correct format, otherwise is not
	 * valid.</li>
	 * <li>Kings presence: there must be a white and a black king, otherwise is not
	 * valid.</li>
	 * <li>Side that does not move in check: must not be in check, otherwise is not
	 * valid.</li>
	 * <li>Pawns in 8th rank: there must be no pawns in 8th rank, otherwise is not
	 * valid.</li>
	 * <li>Castle: if fen indicates castle available there king an rooks
	 * most be in specific places, otherwise is not valid.</li>
	 * <li>En passant: there most to be a pawn in fourth rank that generates this
	 * indication, otherwise is not valid.</li>
	 * </ul>
	 * 
	 * @param fen the FEN to check
	 * @return true if the FEN corresponds to a legal position false otherwise
	 * 
	 * @since 1.2.4
	 */
	public static boolean isValidFen(String fen) {
		// fen format
		var validFormat = isValidFenFormat(fen);
		if (!validFormat)
			return false;

		Position position = new Position(fen);

		var bitboards = new Position(fen).getBits();

		// side to move in check
		position.changeColorToMove();
		var validCheck = !(GeneratorFactory.generatorUtil.isInCheck(position) == 1);

		// pawns in 8th rank
		var wpBitboards = bitboards[Piece.WP.ordinal() - 1];
		var bpBitboards = bitboards[Piece.BP.ordinal() - 1];
		var pawnsBitboard = wpBitboards | bpBitboards;
		var rank = 0xFF00000000000000L | 0xFFL;
		var noPawnsIn8thRank = (pawnsBitboard & rank) == 0L;

		// castle

		var validWk = true;
		if (position.isShortCastleWhite()) {
			var rookBitboard = 1L << 7;
			var kingBitboard = 1L << 4;
			var wkCondition = (Long.bitCount(bitboards[Piece.WR.ordinal() - 1] & rookBitboard) == 1)
					|| (Long.bitCount(bitboards[Piece.WK.ordinal() - 1] & kingBitboard) == 1);
			validWk = wkCondition;
		}

		var validWq = true;
		if (position.isLongCastleWhite()) {
			var rookBitboard = 1L << 0;
			var kingBitboard = 1L << 4;
			var wqCondition = (Long.bitCount(bitboards[Piece.WR.ordinal() - 1] & rookBitboard) == 1)
					|| (Long.bitCount(bitboards[Piece.WK.ordinal() - 1] & kingBitboard) == 1);
			validWq = wqCondition;
		}

		var validBk = true;
		if (position.isShortCastleBlack()) {
			var rookBitboard = 1L << 63;
			var kingBitboard = 1L << 60;
			var bkCondition = (Long.bitCount(bitboards[Piece.BR.ordinal() - 1] & rookBitboard) == 1)
					|| (Long.bitCount(bitboards[Piece.BK.ordinal() - 1] & kingBitboard) == 1);
			validBk = bkCondition;
		}

		var validBq = true;
		if (position.isLongCastleBlack()) {
			var rookBitboard = 1L << 56;
			var kingBitboard = 1L << 60;
			var bqCondition = (Long.bitCount(bitboards[Piece.BR.ordinal() - 1] & rookBitboard) == 1)
					|| (Long.bitCount(bitboards[Piece.BK.ordinal() - 1] & kingBitboard) == 1);
			validBq = bqCondition;
		}

		// en passant
		position.changeColorToMove();
		var enPassantSquare = position.getEnPassant();
		var pawnBitboard = bitboards[(position.isWhiteMove() ? Piece.WP.ordinal() : Piece.BP.ordinal()) - 1];
		var isValidEnPassant = (Long.bitCount(pawnBitboard & (1L << enPassantSquare)) == 1) || (enPassantSquare == -1);

		return validCheck && noPawnsIn8thRank && validWk && validWq && validBk && validBq
				&& isValidEnPassant;
	}

	/*
	 * Checks if the given FEN format is valid and checks if the kings are present.
	 */
	private static boolean isValidFenFormat(String fen) {
		var parts = fen.split(" ");

		if (parts.length != 6)
			return false;

		var piecesString = fen.split(" ")[0];

		// has 8 rows
		var rows = piecesString.split("/");
		var has8Rows = rows.length == 8;

		// valid rows
		var validRows = true;

		for (var row : rows) {
			var matcher = INVALID_ROW_PATTERN.matcher(row);
			validRows = validRows && !matcher.find();
		}

		// valid side to move
		var sideToMove = parts[1];
		var validSideToMove = sideToMove.equals("w") || sideToMove.equals("b");

		// valid castle ability
		var castleAbility = parts[2];
		var validCastleAbility = VALID_CASTLE_ABILITY_PATTERN.matcher(castleAbility).find();

		// valid en passant
		var enPassant = parts[3];
		var validEnPassant = VALID_EN_PASSANT_PATTERN.matcher(enPassant).find();

		// valid half move clock
		var halfMoveClock = parts[4];
		var validHalfMoveClock = VALID_HALF_MOVE_CLOCK_PATTERN.matcher(halfMoveClock).find();

		// valid full move counter
		var fullMoveCounter = parts[5];
		var validFullMoveCounter = VALID_FULL_MOVE_COUNTER_PATTERN.matcher(fullMoveCounter).find();

		// Kings presence
		var wkMatcher = WK_PATTERN.matcher(piecesString);
		var bkMatcher = BK_PATTERN.matcher(piecesString);

		var kingsPresence = wkMatcher.find() && bkMatcher.find();

		return has8Rows && validRows && validSideToMove && validCastleAbility && validEnPassant
				&& validHalfMoveClock && validFullMoveCounter && kingsPresence;
	}

	private static boolean isCheckmate(Position position) {
		var enemiesDeque = new ArrayDeque<Integer>();
		var bits = position.getBits();
		final var black = bits[6] | bits[7] | bits[8] | bits[9] | bits[10] | bits[11];
		final var white = bits[0] | bits[1] | bits[2] | bits[3] | bits[4] | bits[5];
		long friends;
		long enemies;
		var isWhiteMove = position.isWhiteMove();
		if (isWhiteMove) {
			friends = white;
			enemies = black;
		} else {
			friends = black;
			enemies = white;
		}
		var kingSquare = Long.numberOfTrailingZeros(
				bits[isWhiteMove ? Piece.WK.ordinal() - 1 : Piece.BK.ordinal() - 1]);
		var enemiesCopy = enemies;
		while(enemiesCopy != 0L){
			var bitboard = Long.lowestOneBit(enemies);
			enemiesCopy &= ~bitboard;
			var square = Square.values()[Long.numberOfTrailingZeros(bitboard)];
			var piece = position.getPiece(square);

		}
		// TODO continue implementing in version 1.2.9
		return false;
	}

}