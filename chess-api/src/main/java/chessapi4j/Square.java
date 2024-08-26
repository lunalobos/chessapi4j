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

/**
 * Basis enum for squares. Ordinal order follows regular convention where A1 is 0, B1 is 1 and so on.
 *
 * @author Migue
 *
 * @since 1.2.3
 */
public enum Square {
	A1, B1, C1, D1, E1, F1, G1, H1,
	A2, B2, C2, D2, E2, F2, G2, H2,
	A3, B3, C3, D3, E3, F3, G3, H3,
	A4, B4, C4, D4, E4,	F4, G4, H4,
	A5, B5, C5, D5, E5, F5, G5, H5,
	A6, B6, C6, D6, E6, F6, G6, H6,
	A7, B7, C7, D7, E7, F7, G7, H7,
	A8, B8,	C8, D8, E8, F8, G8, H8;

	/**
	 * Retrieves the square object for the square number provided.
	 * @param squareNumber
	 * @return the square object for the square number provided
	 */
	public static Square get(int squareNumber) {
		return values()[squareNumber];
	}

	/**
	 * Retrieves the square object for the square name provided.
	 * @param squareName
	 * @return the square object for the square name provided
	 */
	public static Square get(String squareName) {
		return valueOf(squareName.toUpperCase());
	}

	/**
	 * Lower case name of this square.
	 * @return the lower case name of this square
	 */
	public String getName() {
		return toString().toLowerCase();
	}
}
