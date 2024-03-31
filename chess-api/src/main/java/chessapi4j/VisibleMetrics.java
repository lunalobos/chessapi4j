package chessapi4j;

class VisibleMetrics {

	private static final long[] OPTIONS = new long[] { 0L, 1L, 0b11L, 0b111L, 0b1111L, 0b11111L, 0b111111L,
			0b1111111L };

	private static final long[][][] VISIBLE_OPTIONS = new long[64][8][];

	private static final int[] TRAILING_ZEROS = new int[256];

	static {
		fillMap();
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

	private static void fillMap() {
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


}
