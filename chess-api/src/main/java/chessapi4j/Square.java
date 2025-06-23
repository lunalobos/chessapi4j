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
 * Basis enum for squares. Ordinal order follows regular convention where A1 is 0, B1 is 1 and so on.
 *
 * @author Migue
 *
 * @since 1.2.3
 */
public enum Square {
	/** a1 square */
    A1, 
    /** b1 square */
    B1,
    /** c1 square */
    C1, 
    /** d1 square */
    D1,
    /** e1 square */
    E1,
    /** f1 square */
    F1,
    /** g1 square */
    G1,
    /** h1 square */
    H1,
    /** a2 square */
    A2,
    /** b2 square */
    B2,
    /** c2 square */
    C2,
    /** d2 square */
    D2,
    /** e2 square */
    E2,
    /** f2 square */
    F2,
    /** g2 square */
    G2,
    /** h2 square */
    H2,
    /** a3 square */
    A3,
    /** b3 square */
    B3,
    /** c3 square */
    C3,
    /** d3 square */
    D3,
    /** e3 square */
    E3,
    /** f3 square */
    F3,
    /** g3 square */
    G3,
    /** h3 square */
    H3,
    /** a4 square */
    A4,
    /** b4 square */
    B4,
    /** c4 square */
    C4,
    /** d4 square */
    D4,
    /** e4 square */
    E4,
    /** f4 square */
    F4,
    /** g4 square */
    G4,
    /** h4 square */
    H4,
    /** a5 square */
    A5,
    /** b5 square */
    B5,
    /** c5 square */
    C5,
    /** d5 square */
    D5,
    /** e5 square */
    E5,
    /** f5 square */
    F5,
    /** g5 square */
    G5,
    /** h5 square */
    H5,
    /** a6 square */
    A6,
    /** b6 square */
    B6,
    /** c6 square */
    C6,
    /** d6 square */
    D6,
    /** e6 square */
    E6,
    /** f6 square */
    F6,
    /** g6 square */
    G6,
    /** h6 square */
    H6,
    /** a7 square */
    A7,
    /** b7 square */
    B7,
    /** c7 square */
    C7,
    /** d7 square */
    D7,
    /** e7 square */
    E7,
    /** f7 square */
    F7,
    /** g7 square */
    G7,
    /** h7 square */
    H7,
    /** a8 square */
    A8,
    /** b8 square */
    B8,
    /** c8 square */
    C8,
    /** d8 square */
    D8,
    /** e8 square */
    E8,
    /** f8 square */
    F8,
    /** g8 square */
    G8,
    /** h8 square */
    H8;

	/**
	 * Retrieves the square object for the square number provided.
	 * @param squareNumber the number of the square
	 * @return the square object for the square number provided
	 */
	public static Square get(int squareNumber) {
		return values()[squareNumber];
	}

	/**
	 * Retrieves the square object for the square name provided.
	 * @param squareName the name of the square
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
