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
/**
 * Enum basic class for pieces.
 * @author lunalobos
 * @since 1.0.0
 */
public enum Piece {
	/**
	 * Special value for representing empty squares
	 */
	EMPTY(null),
	/**
	 * White pawn
	 */
	WP(Side.WHITE),
	/**
	 * White knight
	 */
	WN(Side.WHITE),
	/**
	 * White bishop
	 */
	WB(Side.WHITE),
	/**
	 * White rook
	 */
	WR(Side.WHITE),
	/**
	 * White queen
	 */
	WQ(Side.WHITE),
	/**
	 * White king
	 */
	WK(Side.WHITE),
	/**
	 * Black pawn
	 */
	BP(Side.BLACK),
	/**
	 * Black knight
	 */
	BN(Side.BLACK),
	/**
	 * Black bishop
	 */
	BB(Side.BLACK),
	/**
	 * Black rook
	 */
	BR(Side.BLACK),
	/**
	 * Black queen
	 */
	BQ(Side.BLACK),
	/**
	 * Black king
	 */
	BK(Side.BLACK);
	private final Side side;

	Piece(Side side) {
		this.side = side;
	}

	/**
	 * Returns the side of the piece
	 * @return the side
	 * @since 1.2.8
	 */
	public Side side() {
		return side;
	}

	/**
	 * Retrieves the piece object for the given piece index
	 * @param pieceIndex the piece index
	 * @return the corresponding piece object
	 *
	 * @since 1.2.9
	 */
	public static Piece get(int pieceIndex){
		return Piece.values()[pieceIndex];
	}
}
