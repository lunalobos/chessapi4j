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

import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;
import java.math.BigInteger;
import java.security.SecureRandom;

/*
 *
 * @author lunalobos
 */
class VisibleMetrics {

	private static final long[] OPTIONS = new long[] { 0L, 1L, 0b11L, 0b111L, 0b1111L, 0b11111L, 0b111111L,
			0b1111111L };

	private static final long[][][] VISIBLE_OPTIONS = new long[64][8][];

	private static final int[] TRAILING_ZEROS = new int[256];

	private static final Bitboard COL_A = Util.column("A");
	private static final Bitboard COL_H = Util.column("H");
	private static final Bitboard ROW_1 = Util.row(1);
	private static final Bitboard ROW_8 = Util.row(8);

	private static final Bitboard ALL_BORDERS = Bitboard.or(COL_A, COL_H, ROW_1, ROW_8);

	private static final Bitboard ALL_BORDERS_MINUS_A_COL = Bitboard.or(COL_H, ROW_1, ROW_8);

	private static final Bitboard ALL_BORDERS_MINUS_H_COL = Bitboard.or(COL_A, ROW_1, ROW_8);

	private static final Bitboard ALL_BORDERS_MINUS_1_ROW = Bitboard.or(COL_H, COL_A, ROW_8);

	private static final Bitboard ALL_BORDERS_MINUS_8_ROW = Bitboard.or(COL_H, COL_A, ROW_1);

	private static final Bitboard ALL_BORDERS_MINUS_1_ROW_AND_A_COL = Bitboard.or(COL_H, ROW_8);

	private static final Bitboard ALL_BORDERS_MINUS_1_ROW_AND_H_COL = Bitboard.or(COL_A, ROW_8);

	private static final Bitboard ALL_BORDERS_MINUS_8_ROW_AND_A_COL = Bitboard.or(COL_H, ROW_1);

	private static final Bitboard ALL_BORDERS_MINUS_8_ROW_AND_H_COL = Bitboard.or(COL_A, ROW_1);

	protected static final Bitboard[] BORDERS = new Bitboard[] {
			ALL_BORDERS_MINUS_1_ROW_AND_A_COL, // A1
			ALL_BORDERS_MINUS_1_ROW, // B1
			ALL_BORDERS_MINUS_1_ROW, // C1
			ALL_BORDERS_MINUS_1_ROW, // D1
			ALL_BORDERS_MINUS_1_ROW, // E1
			ALL_BORDERS_MINUS_1_ROW, // F1
			ALL_BORDERS_MINUS_1_ROW, // G1
			ALL_BORDERS_MINUS_1_ROW_AND_H_COL, // H1
			ALL_BORDERS_MINUS_A_COL, // A2
			ALL_BORDERS, // B2
			ALL_BORDERS, // C2
			ALL_BORDERS, // D2
			ALL_BORDERS, // E2
			ALL_BORDERS, // F2
			ALL_BORDERS, // G2
			ALL_BORDERS_MINUS_H_COL, // H2
			ALL_BORDERS_MINUS_A_COL, // A3
			ALL_BORDERS, // B3
			ALL_BORDERS, // C3
			ALL_BORDERS, // D3
			ALL_BORDERS, // E3
			ALL_BORDERS, // F3
			ALL_BORDERS, // G3
			ALL_BORDERS_MINUS_H_COL, // H3
			ALL_BORDERS_MINUS_A_COL, // A4
			ALL_BORDERS, // B4
			ALL_BORDERS, // C4
			ALL_BORDERS, // D4
			ALL_BORDERS, // E4
			ALL_BORDERS, // F4
			ALL_BORDERS, // G4
			ALL_BORDERS_MINUS_H_COL, // H4
			ALL_BORDERS_MINUS_A_COL, // A5
			ALL_BORDERS, // B5
			ALL_BORDERS, // C5
			ALL_BORDERS, // D5
			ALL_BORDERS, // E5
			ALL_BORDERS, // F5
			ALL_BORDERS, // G5
			ALL_BORDERS_MINUS_H_COL, // H5
			ALL_BORDERS_MINUS_A_COL, // A6
			ALL_BORDERS, // B6
			ALL_BORDERS, // C6
			ALL_BORDERS, // D6
			ALL_BORDERS, // E6
			ALL_BORDERS, // F6
			ALL_BORDERS, // G6
			ALL_BORDERS_MINUS_H_COL, // H6
			ALL_BORDERS_MINUS_A_COL, // A7
			ALL_BORDERS, // B7
			ALL_BORDERS, // C7
			ALL_BORDERS, // D7
			ALL_BORDERS, // E7
			ALL_BORDERS, // F7
			ALL_BORDERS, // G7
			ALL_BORDERS_MINUS_H_COL, // H7
			ALL_BORDERS_MINUS_8_ROW_AND_A_COL, // A8
			ALL_BORDERS_MINUS_8_ROW, // B8
			ALL_BORDERS_MINUS_8_ROW, // C8
			ALL_BORDERS_MINUS_8_ROW, // D8
			ALL_BORDERS_MINUS_8_ROW, // E8
			ALL_BORDERS_MINUS_8_ROW, // F8
			ALL_BORDERS_MINUS_8_ROW, // G8
			ALL_BORDERS_MINUS_8_ROW_AND_H_COL, // H8
	};

	protected static long[] ROOK_MAGIC_NUMBERS;
	protected static long[] BISHOP_MAGIC_NUMBERS;

	static {
		fillMap();
		ROOK_MAGIC_NUMBERS = magicNumbers(Util.ROOK_DIRECTIONS);
		BISHOP_MAGIC_NUMBERS = magicNumbers(Util.ROOK_DIRECTIONS);
	}

	
	public final long computeVisible(int square, int[] directionsIndexs, int[][] directions, long friends,
			long enemies) {
		long moves = 0L;
		for (int index : directionsIndexs) {
			moves = moves
					| getVisible(square, index, directions[index], friends, enemies);
		}
		return moves;
	}

	
	public final long getVisible(int square, int index, int[] direction, long friends, long enemies) {

		// space transformation: board -> direction
		int fimage = 0;
		int eimage = 0;
		int counter = 0;
		for (int sq : direction) {
			fimage |= (int) (((friends & (1L << sq)) >>> sq) << counter);
			eimage |= (int) (((enemies & (1L << sq)) >>> sq) << (counter + 1));
			counter++;
		}

		// image for direction space with bit population always <= 7
		final int image = (fimage | eimage) & 0b1111111;

		// trailing zeros count and visible bitboard selection
		return VISIBLE_OPTIONS[square][index][TRAILING_ZEROS[image]];
	}

	private static final void fillMap() {
		TRAILING_ZEROS[0] = 7;
		for (int i = 1; i < 0b10000000; i++) {
			TRAILING_ZEROS[i] = Integer.numberOfTrailingZeros(i);
		}
		for (int square = 0; square < 64; square++) {
			for (int directionIndex = 0; directionIndex < 8; directionIndex++) {
				int[] direction = Util.QUEEN_MEGAMATRIX[square][directionIndex];
				long[] options = new long[8];
				for (int optionIndex = 0; optionIndex < 8; optionIndex++) {
					long image = OPTIONS[optionIndex];
					long visible = 0L;
					int counter = 0;
					for (int sq : direction) {
						visible |= ((image & (1L << counter)) >>> counter) << sq;
						counter++;
					}
					options[optionIndex] = visible;
				}
				VISIBLE_OPTIONS[square][directionIndex] = options;
			}
		}

	}

	protected static Set<Long> combinations(int square, int[] directionsIndexs) {
		List<Integer> list = squaresList(square, directionsIndexs);
		var combinationsSize = 1 << list.size();
		Set<Long> combinationList = new HashSet<>();
		for (var combination = 0; combination < combinationsSize; combination++) {
			var bitIterator = new BitIterator(combination);
			var listIterator = list.iterator();
			var bitboard = 0L;
			while (bitIterator.hasNext() && listIterator.hasNext()) {
				bitboard |= (((long) bitIterator.next()) << listIterator.next());
			}
			combinationList.add(bitboard);
		}
		return combinationList;
	}

	private static List<Integer> squaresList(int square, int[] directionsIndexs) {
		int[][] directions = Util.QUEEN_MEGAMATRIX[square];
		List<Integer> list = new LinkedList<>();
		for (int directionIndex : directionsIndexs) {
			for (int sq : directions[directionIndex]) {
				list.add(sq);
			}
		}
		return list;
	}

	protected int hash(long ocuppied, long magic, int indexBits, int square, int[] directionsIndexs,
			int[][] directions) {
		var mask = computeVisible(square, directionsIndexs, directions, 0L, 0L);
		var blockers = ocuppied & mask & ~BORDERS[square].getValue();
		return (int) ((blockers * magic) >>> (64 - indexBits));
	}

	protected static final long[] magicNumbers(int[] directionsIndexs){
		final Random random = new SecureRandom();
		var visibleMetrics = new VisibleMetrics();
        final Map<Integer, Long> magicNumbers = new HashMap<>();
        IntStream.range(0, 64).mapToObj(i -> i)
                .forEach(square -> {
                    boolean searching = true;
                    var combinations = VisibleMetrics.combinations(square, directionsIndexs);
                    var max = 10000;
                    var attemp = 0;
                    while (searching && attemp++ < max) {
                        var magicNumber = BigInteger.probablePrime(63, random).longValue();
                        if (magicNumber < 0) {
                            magicNumber = -magicNumber;
                        }
                        var map = new HashMap<Integer, Bitboard>();
                        var isMagic = true;
                        var squareBitboard = Square.get(square).getBitboard();
                        for (long combination : combinations) {
                            var directions = Util.QUEEN_MEGAMATRIX[square];
                            var visible = new Bitboard(
								visibleMetrics.computeVisible(square, directionsIndexs, directions, 0L,
                                            combination & squareBitboard.not().getValue()));
                            visible = visible.and(VisibleMetrics.BORDERS[square].not());
                            var index = visibleMetrics.hash(combination & squareBitboard.not().getValue(), magicNumber, 16, square, 
									directionsIndexs, directions);
                            if (map.containsKey(index)) {
                                if(map.get(index).equals(visible)) {
                                    continue;
                                } else {
                                    isMagic = false;
                                }
                            } else {
                                map.put(index, visible);
                            }
                        }
                        
                        if (isMagic) {
                            searching = false;
                            magicNumbers.put(square, magicNumber);
                            break;
                        }
                    }
                });
        long[] magicNumbersArray = new long[magicNumbers.size()];
		int i = 0;
		for (var entry : magicNumbers.entrySet()) {
			magicNumbersArray[i++] = entry.getValue();
		}	
		return magicNumbersArray;
	}

}

/**
 * 
 * 
 * @author lunalobos
 */
class BitIterator implements Iterator<Integer> {

	private final int bits;
	private int pointer = 0;
	private final int bitsLength;

	public BitIterator(int bits) {
		this.bits = bits;
		bitsLength = 32 - Integer.numberOfLeadingZeros(bits);
	}

	@Override
	public boolean hasNext() {
		return pointer < bitsLength;
	}

	@Override
	public Integer next() {
		var currentPointer = pointer;
		pointer++;
		return (bits & (1 << currentPointer)) >>> currentPointer;
	}

}
