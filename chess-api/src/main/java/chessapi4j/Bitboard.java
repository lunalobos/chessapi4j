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
 * Wrapped bitboard class for several debugging or experimenting purposes. I
 * don't recommend use it in high performance related functionality.
 *
 * @author lunalobos
 *
 * @since 1.2.3
 */
public class Bitboard {
	/**
	 * Apply the "and bit to bit" operation over the values of the bitboards and
	 * retrieves a new {@code Bitboard} object that wraps the result of this
	 * operation.
	 *
	 * @param bitboards the bitboards to apply bitwise the operation
	 * @return a new {@code Bitboard} object that wraps the result of the "and"
	 *         operation
	 */
	public static Bitboard and(Bitboard... bitboards) {
		long newValue = -1L;
		for (Bitboard bitboard : bitboards) {
			newValue &= bitboard.getValue();
		}
		return new Bitboard(newValue);
	}

	/**
	 * Apply the "or bit to bit" operation over the values of the bitboards and
	 * retrieves a new {@code Bitboard} object that wraps the result of this
	 * operation.
	 *
	 * @param bitboards the bitboards to apply bitwise the operation
	 * @return a new {@code Bitboard} object that wraps the result of the "or"
	 *         operation
	 */
	public static Bitboard or(Bitboard... bitboards) {
		long newValue = 0L;
		for (Bitboard bitboard : bitboards) {
			newValue |= bitboard.getValue();
		}
		return new Bitboard(newValue);
	}

	/**
	 * Apply the "xor bit to bit" operation over the values of the bitboards and
	 * retrieves a new {@code Bitboard} object that wraps the result of this
	 * operation.
	 *
	 * @param bitboards the bitboards to apply bitwise the operation
	 * @return a new {@code Bitboard} object that wraps the result of the "xor"
	 *         operation
	 */
	public static Bitboard xor(Bitboard... bitboards) {
		long newValue = 0L;
		for (Bitboard bitboard : bitboards) {
			newValue ^= bitboard.getValue();
		}
		return new Bitboard(newValue);
	}

	/**
	 * Performs a bitwise right shift (>>>) on the given bitboard.
	 *
	 * @param x     the bitboard to shift
	 * @param shift the number of bits to shift
	 * @return a new {@code Bitboard} object with the result of the shift
	 */
	public static Bitboard shiftRight(Bitboard x, int shift) {
		return new Bitboard(x.getValue() >>> shift);
	}

	/**
	 * Performs a bitwise left shift on the given bitboard.
	 *
	 * @param x     the bitboard to shift
	 * @param shift the number of bits to shift
	 * @return a new {@code Bitboard} object with the result of the shift
	 */
	public static Bitboard shiftLeft(Bitboard x, int shift) {
		return new Bitboard(x.getValue() << shift);
	}

	/**
	 * Negates the given bitboard.
	 *
	 * @param bitboard the bitboard to negate
	 * @return a new bitboard with the negated value
	 */
	public static Bitboard not(Bitboard bitboard) {
		long newValue = ~bitboard.getValue();
		return new Bitboard(newValue);
	}

	private long value;

	/**
	 * Creates an empty wrapped bitboard
	 */
	public Bitboard() {
		value = 0L;
	}

	/**
	 * Wrap the given value.
	 *
	 * @param value the value to wrap
	 */
	public Bitboard(long value) {
		this.value = value;
	}

	/**
	 * Copy constructor.
	 *
	 * @param bitboard the bitboard to copy
	 */
	public Bitboard(Bitboard bitboard) {
		this.value = bitboard.getValue();
	}

	/**
	 * Constructs a bitboard from the given array of squares.
	 *
	 * @param squares an array of squares
	 */
	public Bitboard(Square... squares) {
		long value = 0L;
		for (Square i : squares) {
			value |= 1L << i.ordinal();
		}
		this.value = value;
	}

	/**
	 * Value wrapped by this object.
	 *
	 * @return the long wrapped value
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Sets the value of the bitboard.
	 *
	 * @param value the new value
	 */
	public void setValue(long value) {
		this.value = value;
	}

	/**
	 * Removes and returns the least significant bit set to 1 in the bitboard.
	 *
	 * @return a new bitboard with the removed bit
	 */
	public Bitboard popLastBit() {
		long lowest = value & -value;
		value &= ~(lowest);
		return new Bitboard(lowest);
	}

	/**
	 * Removes and returns the most significant bit set to 1 in the bitboard.
	 *
	 * @return a new bitboard with the removed bit
	 */
	public Bitboard popFirstBit() {
		long highest = Long.highestOneBit(value);
		;
		value &= ~(highest);
		return new Bitboard(highest);
	}

	@Override
	public int hashCode() {
		return ((Long) value).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj instanceof Bitboard) {
			Bitboard other = (Bitboard) obj;
			return value == other.getValue();
		} else
			return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(500);
		long inverted = Long.reverse(value);
		long mask = 0b11111111L;
		sb.append("\n+---+---+---+---+---+---+---+---+ \n");
		for (int i = 0; i < 8; i++) {
			StringBuilder isb = new StringBuilder(24);
			long masked = (inverted & (mask << (i * 8))) >>> (i * 8);
			int lz = Long.numberOfLeadingZeros(masked) - 56;
			for (int j = 0; j < lz; j++) {
				isb.append('0');
			}
			isb.append(masked == 0L ? "" : Long.toBinaryString(masked));
			char[] characters = isb.toString().toCharArray();
			for (char c : characters) {
				sb.append('|').append(' ').append(c).append(' ');
			}
			sb.append('|');
			sb.append("\n+---+---+---+---+---+---+---+---+ \n");
		}
		return sb.toString();
	}

}
