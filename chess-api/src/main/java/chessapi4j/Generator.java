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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lunalobos
 */
final class CheckInfo {
	private long inCheck;
	private long inCheckMask;
	private int checkCount;

	public CheckInfo(long inCheck, long inCheckMask, int checkCount) {
		super();
		this.inCheck = inCheck;
		this.inCheckMask = inCheckMask;
		this.checkCount = checkCount;
	}

	public long getInCheck() {
		return inCheck;
	}

	public void setInCheck(long inCheck) {
		this.inCheck = inCheck;
	}

	public long getInCheckMask() {
		return inCheckMask;
	}

	public void setInCheckMask(long inCheckMask) {
		this.inCheckMask = inCheckMask;
	}

	public int getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}

}
//singleton bean
/**
 * This class is intended to generate all possible moves from an original position.
 *
 * @author lunalobos
 *
 * @since 1.0.0
 */
public final class Generator {
	private static final Logger logger = LoggerFactory.getLogger(Generator.class);
	protected static final int[] BISHOP_DIRECTIONS = new int[] { 0, 1, 2, 3 };
	private static final int[] BISHOPS = new int[] { Piece.BB.ordinal(), Piece.WB.ordinal() };

	// black pawn advance matrix
	private static final int[][] BLACK_PAWN_MATRIX_1 = new int[][] { {}, {}, {}, {}, {}, {}, {}, {}, { 0 }, { 1 },
			{ 2 }, { 3 }, { 4 }, { 5 }, { 6 }, { 7 }, { 8 }, { 9 }, { 10 }, { 11 }, { 12 }, { 13 }, { 14 }, { 15 },
			{ 16 }, { 17 }, { 18 }, { 19 }, { 20 }, { 21 }, { 22 }, { 23 }, { 24 }, { 25 }, { 26 }, { 27 }, { 28 },
			{ 29 }, { 30 }, { 31 }, { 32 }, { 33 }, { 34 }, { 35 }, { 36 }, { 37 }, { 38 }, { 39 }, { 40, 32 },
			{ 41, 33 }, { 42, 34 }, { 43, 35 }, { 44, 36 }, { 45, 37 }, { 46, 38 }, { 47, 39 }, { 48 }, { 49 }, { 50 },
			{ 51 }, { 52 }, { 53 }, { 54 }, { 55 } };

	// black pawn capture matrix
	protected static final int[][] BLACK_PAWN_MATRIX_2 = new int[][] { {}, {}, {}, {}, {}, {}, {}, {}, { 1 }, { 0, 2 },
			{ 1, 3 }, { 2, 4 }, { 3, 5 }, { 4, 6 }, { 5, 7 }, { 6 }, { 9 }, { 8, 10 }, { 9, 11 }, { 10, 12 },
			{ 11, 13 }, { 12, 14 }, { 13, 15 }, { 14 }, { 17 }, { 16, 18 }, { 17, 19 }, { 18, 20 }, { 19, 21 },
			{ 20, 22 }, { 21, 23 }, { 22 }, { 25 }, { 24, 26 }, { 25, 27 }, { 26, 28 }, { 27, 29 }, { 28, 30 },
			{ 29, 31 }, { 30 }, { 33 }, { 32, 34 }, { 33, 35 }, { 34, 36 }, { 35, 37 }, { 36, 38 }, { 37, 39 }, { 38 },
			{ 41 }, { 40, 42 }, { 41, 43 }, { 42, 44 }, { 43, 45 }, { 44, 46 }, { 45, 47 }, { 46 }, { 49 }, { 48, 50 },
			{ 49, 51 }, { 50, 52 }, { 51, 53 }, { 52, 54 }, { 53, 55 }, { 54 } };

	private static final int[][] KING_MATRIX = new int[][] { { 9, 8, 1 }, { 10, 8, 9, 2, 0 }, { 11, 9, 10, 3, 1 },
			{ 12, 10, 11, 4, 2 }, { 13, 11, 12, 5, 3 }, { 14, 12, 13, 6, 4 }, { 15, 13, 14, 7, 5 }, { 14, 15, 6 },
			{ 17, 1, 16, 0, 9 }, { 18, 16, 0, 2, 17, 1, 10, 8 }, { 19, 17, 1, 3, 18, 2, 11, 9 },
			{ 20, 18, 2, 4, 19, 3, 12, 10 }, { 21, 19, 3, 5, 20, 4, 13, 11 }, { 22, 20, 4, 6, 21, 5, 14, 12 },
			{ 23, 21, 5, 7, 22, 6, 15, 13 }, { 22, 6, 23, 7, 14 }, { 25, 9, 24, 8, 17 },
			{ 26, 24, 8, 10, 25, 9, 18, 16 }, { 27, 25, 9, 11, 26, 10, 19, 17 }, { 28, 26, 10, 12, 27, 11, 20, 18 },
			{ 29, 27, 11, 13, 28, 12, 21, 19 }, { 30, 28, 12, 14, 29, 13, 22, 20 }, { 31, 29, 13, 15, 30, 14, 23, 21 },
			{ 30, 14, 31, 15, 22 }, { 33, 17, 32, 16, 25 }, { 34, 32, 16, 18, 33, 17, 26, 24 },
			{ 35, 33, 17, 19, 34, 18, 27, 25 }, { 36, 34, 18, 20, 35, 19, 28, 26 }, { 37, 35, 19, 21, 36, 20, 29, 27 },
			{ 38, 36, 20, 22, 37, 21, 30, 28 }, { 39, 37, 21, 23, 38, 22, 31, 29 }, { 38, 22, 39, 23, 30 },
			{ 41, 25, 40, 24, 33 }, { 42, 40, 24, 26, 41, 25, 34, 32 }, { 43, 41, 25, 27, 42, 26, 35, 33 },
			{ 44, 42, 26, 28, 43, 27, 36, 34 }, { 45, 43, 27, 29, 44, 28, 37, 35 }, { 46, 44, 28, 30, 45, 29, 38, 36 },
			{ 47, 45, 29, 31, 46, 30, 39, 37 }, { 46, 30, 47, 31, 38 }, { 49, 33, 48, 32, 41 },
			{ 50, 48, 32, 34, 49, 33, 42, 40 }, { 51, 49, 33, 35, 50, 34, 43, 41 }, { 52, 50, 34, 36, 51, 35, 44, 42 },
			{ 53, 51, 35, 37, 52, 36, 45, 43 }, { 54, 52, 36, 38, 53, 37, 46, 44 }, { 55, 53, 37, 39, 54, 38, 47, 45 },
			{ 54, 38, 55, 39, 46 }, { 57, 41, 56, 40, 49 }, { 58, 56, 40, 42, 57, 41, 50, 48 },
			{ 59, 57, 41, 43, 58, 42, 51, 49 }, { 60, 58, 42, 44, 59, 43, 52, 50 }, { 61, 59, 43, 45, 60, 44, 53, 51 },
			{ 62, 60, 44, 46, 61, 45, 54, 52 }, { 63, 61, 45, 47, 62, 46, 55, 53 }, { 62, 46, 63, 47, 54 },
			{ 49, 48, 57 }, { 48, 50, 49, 58, 56 }, { 49, 51, 50, 59, 57 }, { 50, 52, 51, 60, 58 },
			{ 51, 53, 52, 61, 59 }, { 52, 54, 53, 62, 60 }, { 53, 55, 54, 63, 61 }, { 54, 55, 62 } };

	private static final int[] KINGS = new int[] { Piece.BK.ordinal(), Piece.WK.ordinal() };

	protected static final int[][] KNIGHT_MATRIX = new int[][] { { 17, 10 }, { 18, 16, 11 }, { 19, 17, 12, 8 },
			{ 20, 18, 13, 9 }, { 21, 19, 14, 10 }, { 22, 20, 15, 11 }, { 23, 21, 12 }, { 22, 13 }, { 25, 18, 2 },
			{ 26, 24, 19, 3 }, { 27, 25, 20, 16, 4, 0 }, { 28, 26, 21, 17, 5, 1 }, { 29, 27, 22, 18, 6, 2 },
			{ 30, 28, 23, 19, 7, 3 }, { 31, 29, 20, 4 }, { 30, 21, 5 }, { 33, 26, 1, 10 }, { 34, 32, 27, 2, 0, 11 },
			{ 35, 33, 28, 24, 3, 1, 12, 8 }, { 36, 34, 29, 25, 4, 2, 13, 9 }, { 37, 35, 30, 26, 5, 3, 14, 10 },
			{ 38, 36, 31, 27, 6, 4, 15, 11 }, { 39, 37, 28, 7, 5, 12 }, { 38, 29, 6, 13 }, { 41, 34, 9, 18 },
			{ 42, 40, 35, 10, 8, 19 }, { 43, 41, 36, 32, 11, 9, 20, 16 }, { 44, 42, 37, 33, 12, 10, 21, 17 },
			{ 45, 43, 38, 34, 13, 11, 22, 18 }, { 46, 44, 39, 35, 14, 12, 23, 19 }, { 47, 45, 36, 15, 13, 20 },
			{ 46, 37, 14, 21 }, { 49, 42, 17, 26 }, { 50, 48, 43, 18, 16, 27 }, { 51, 49, 44, 40, 19, 17, 28, 24 },
			{ 52, 50, 45, 41, 20, 18, 29, 25 }, { 53, 51, 46, 42, 21, 19, 30, 26 }, { 54, 52, 47, 43, 22, 20, 31, 27 },
			{ 55, 53, 44, 23, 21, 28 }, { 54, 45, 22, 29 }, { 57, 50, 25, 34 }, { 58, 56, 51, 26, 24, 35 },
			{ 59, 57, 52, 48, 27, 25, 36, 32 }, { 60, 58, 53, 49, 28, 26, 37, 33 }, { 61, 59, 54, 50, 29, 27, 38, 34 },
			{ 62, 60, 55, 51, 30, 28, 39, 35 }, { 63, 61, 52, 31, 29, 36 }, { 62, 53, 30, 37 }, { 58, 33, 42 },
			{ 59, 34, 32, 43 }, { 60, 56, 35, 33, 44, 40 }, { 61, 57, 36, 34, 45, 41 }, { 62, 58, 37, 35, 46, 42 },
			{ 63, 59, 38, 36, 47, 43 }, { 60, 39, 37, 44 }, { 61, 38, 45 }, { 41, 50 }, { 42, 40, 51 },
			{ 43, 41, 52, 48 }, { 44, 42, 53, 49 }, { 45, 43, 54, 50 }, { 46, 44, 55, 51 }, { 47, 45, 52 },
			{ 46, 53 } };
	private static final int[] KNIGHTS = new int[] { Piece.BN.ordinal(), Piece.WN.ordinal() };

	private static final int[][] WHITE_PAWN_MATRIX_1 = new int[][] { { 8 }, { 9 }, { 10 }, { 11 }, { 12 }, { 13 },
			{ 14 }, { 15 }, { 16, 24 }, { 17, 25 }, { 18, 26 }, { 19, 27 }, { 20, 28 }, { 21, 29 }, { 22, 30 },
			{ 23, 31 }, { 24 }, { 25 }, { 26 }, { 27 }, { 28 }, { 29 }, { 30 }, { 31 }, { 32 }, { 33 }, { 34 }, { 35 },
			{ 36 }, { 37 }, { 38 }, { 39 }, { 40 }, { 41 }, { 42 }, { 43 }, { 44 }, { 45 }, { 46 }, { 47 }, { 48 },
			{ 49 }, { 50 }, { 51 }, { 52 }, { 53 }, { 54 }, { 55 }, { 56 }, { 57 }, { 58 }, { 59 }, { 60 }, { 61 },
			{ 62 }, { 63 }, {}, {}, {}, {}, {}, {}, {}, {} };
	protected static final int[][] WHITE_PAWN_MATRIX_2 = new int[][] { { 9 }, { 8, 10 }, { 9, 11 }, { 10, 12 },
			{ 11, 13 }, { 12, 14 }, { 13, 15 }, { 14 }, { 17 }, { 16, 18 }, { 17, 19 }, { 18, 20 }, { 19, 21 },
			{ 20, 22 }, { 21, 23 }, { 22 }, { 25 }, { 24, 26 }, { 25, 27 }, { 26, 28 }, { 27, 29 }, { 28, 30 },
			{ 29, 31 }, { 30 }, { 33 }, { 32, 34 }, { 33, 35 }, { 34, 36 }, { 35, 37 }, { 36, 38 }, { 37, 39 }, { 38 },
			{ 41 }, { 40, 42 }, { 41, 43 }, { 42, 44 }, { 43, 45 }, { 44, 46 }, { 45, 47 }, { 46 }, { 49 }, { 48, 50 },
			{ 49, 51 }, { 50, 52 }, { 51, 53 }, { 52, 54 }, { 53, 55 }, { 54 }, { 57 }, { 56, 58 }, { 57, 59 },
			{ 58, 60 }, { 59, 61 }, { 60, 62 }, { 61, 63 }, { 62 }, {}, {}, {}, {}, {}, {}, {}, {} };
	private static final int[][][] PAWN_MATRIX1 = new int[][][] { BLACK_PAWN_MATRIX_1, WHITE_PAWN_MATRIX_1 };
	private static final int[][][] PAWN_MATRIX2 = new int[][][] { BLACK_PAWN_MATRIX_2, WHITE_PAWN_MATRIX_2 };
	private static final int[] PAWNS = new int[] { Piece.BP.ordinal(), Piece.WP.ordinal() };
	protected static final int[] QUEEN_DIRECTIONS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
	protected static final int[][][] QUEEN_MEGAMATRIX = Util.QUEEN_MEGAMATRIX;
	private static final int[] QUEENS = new int[] { Piece.BQ.ordinal(), Piece.WQ.ordinal() };
	protected static final int[] ROOK_DIRECTIONS = new int[] { 4, 5, 6, 7 };
	private static final int[] ROOKS = new int[] { Piece.BR.ordinal(), Piece.WR.ordinal() };
	// private static final long[] OPTIONS = new long[] { 0L, 1L, 0b11L, 0b111L,
	// 0b1111L, 0b11111L, 0b111111L,
	// 0b1111111L };
	// private static final long[] CORONATION_REF = new long[] { 1L, 1L, 1L, 1L, 1L,
	// 1L, 1L, 1L, 0L, 0L, 0L, 0L, 0L, 0L,
	// 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
	// 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
	// 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1L, 1L, 1L, 1L,
	// 1L, 1L, 1L, 1L };

	private static int squaresMap(long input) {
		return Long.numberOfTrailingZeros(input);
	}

	private PawnGenerator pawnGenerator;
	private KnightGenerator knightGenerator;
	private BishopGenerator bishopGenerator;
	private RookGenerator rookGenerator;
	private QueenGenerator queenGenerator;
	private KingGenerator kingGenerator;
	private VisibleMetrics visibleMetrics;
	private GeneratorUtil generatorUtil;


	protected Generator(PawnGenerator pawnGenerator, KnightGenerator knightGenerator, BishopGenerator bishopGenerator,
			RookGenerator rookGenerator, QueenGenerator queenGenerator, KingGenerator kingGenerator,
			VisibleMetrics visibleMetrics, GeneratorUtil generatorUtil) {
		this.pawnGenerator = pawnGenerator;
		this.knightGenerator = knightGenerator;
		this.bishopGenerator = bishopGenerator;
		this.rookGenerator = rookGenerator;
		this.queenGenerator = queenGenerator;
		this.kingGenerator = kingGenerator;
		this.visibleMetrics = visibleMetrics;
		this.generatorUtil = generatorUtil;
		logger.instanciation();
	}

	private long createCheckMask(int kingSquare, long enemies, long friends, Position position, long nextWhiteMove,
			long[] bits) {
		final long empty = ~(enemies | friends);
		final int[] enemyRookChoice = new int[] { Piece.WR.ordinal(), Piece.BR.ordinal() };
		final int[] enemyQueenChoice = new int[] { Piece.WQ.ordinal(), Piece.BQ.ordinal() };
		final int[] enemyBishopChoice = new int[] { Piece.WB.ordinal(), Piece.BB.ordinal() };
		final int enemyRook = enemyRookChoice[(int) position.wm()];
		final int enemyQueen = enemyQueenChoice[(int) position.wm()];
		final int enemyBishop = enemyBishopChoice[(int) position.wm()];
		long checkMask = 0L;
		for (int j = 0; j < 4; j++) {
			final long visibleEmptyOrFriendsRD = visibleMetrics.visibleSquares(position.getBits(),
					new int[] { ROOK_DIRECTIONS[j] },
					kingSquare, nextWhiteMove);
			final long friendsRD = visibleEmptyOrFriendsRD & ~empty;
			final long[] testBitsRD = bits.clone();
			for (int i = 0; i < 12; i++) {
				testBitsRD[i] = testBitsRD[i] & ~friendsRD;
			}
			final long visibleEmptyOrEnemyRD = visibleMetrics.visibleSquares(testBitsRD,
					new int[] { ROOK_DIRECTIONS[j] }, kingSquare, position.wm());
			final long enemiesThreadsRD = visibleEmptyOrEnemyRD & (bits[enemyRook - 1] | bits[enemyQueen - 1]);
			final long[] choice = new long[] { 0L, friendsRD | visibleEmptyOrEnemyRD };
			checkMask = checkMask | choice[(int) generatorUtil.hasBitsPresent(enemiesThreadsRD)];
		}
		for (int j = 0; j < 4; j++) {
			final long visibleEmptyOrFriendsBD = visibleMetrics.visibleSquares(position.getBits(),
					new int[] { BISHOP_DIRECTIONS[j] },
					kingSquare, nextWhiteMove);
			final long friendsBD = visibleEmptyOrFriendsBD & ~empty;
			final long[] testBitsBD = bits.clone();
			for (int i = 0; i < 12; i++) {
				testBitsBD[i] = testBitsBD[i] & ~friendsBD;
			}
			final long visibleEmptyOrEnemyBD = visibleMetrics.visibleSquares(testBitsBD,
					new int[] { BISHOP_DIRECTIONS[j] },
					kingSquare, position.wm());
			final long enemiesThreadsBD = visibleEmptyOrEnemyBD & (bits[enemyBishop - 1] | bits[enemyQueen - 1]);
			final long[] choice = new long[] { 0L, friendsBD | visibleEmptyOrEnemyBD };
			checkMask = checkMask | choice[(int) generatorUtil.hasBitsPresent(enemiesThreadsBD)];
		}

		return checkMask;
	}

	private void fillChildrenList(List<Position> children, long[] bits, long friends, long enemies, Position position,
			long checkMask, long inCheckMask, long nextWhiteMove, long inCheck, int pawnPiece, int kingSquare,
			int knightPiece, int bishopPiece, int rookPiece, int queenPiece, int kingPiece, int[] pawnsDirections,
			int[][] matrix1, int[][] matrix2) {
		long lb;
		long j;

		// Pawn Moves
		j = bits[pawnPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			pawnGenerator.pawnMoves(lb, squaresMap(lb), pawnsDirections, pawnPiece, matrix1, matrix2, kingSquare,
					enemies, friends,
					position, checkMask, inCheckMask, nextWhiteMove, children);
			j = j & ~lb;
		}
		// Knight Moves
		j = bits[knightPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			knightGenerator.knightMoves(lb, squaresMap(lb), knightPiece, enemies, friends, position, checkMask,
					inCheckMask, children);
			j = j & ~lb;
		}
		// Bishop Moves
		j = bits[bishopPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			bishopGenerator.bishopMoves(lb, squaresMap(lb), bishopPiece, kingSquare, enemies, friends, position,
					checkMask, inCheckMask,
					children);
			j = j & ~lb;
		}
		// Rook Moves
		j = bits[rookPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			rookGenerator.rookMoves(lb, squaresMap(lb), rookPiece, pawnsDirections, kingSquare, enemies, friends,
					position, checkMask,
					inCheckMask, children);
			j = j & ~lb;
		}
		// Queen Moves
		j = bits[queenPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			queenGenerator.queenMoves(lb, squaresMap(lb), queenPiece, kingSquare, friends, enemies, position, checkMask,
					inCheckMask,
					children);
			j = j & ~lb;
		}
		// King Moves
		j = bits[kingPiece - 1];
		while (j != 0L) {
			lb = j & -j;
			kingGenerator.kingMoves(squaresMap(lb), kingPiece, enemies, friends, position, inCheck, children);
			j = j & ~lb;
		}
	}

	/**
	 * Generates the list of legal positions that arise from this particular
	 * position.
	 * 
	 * @param position the position from which the children are generated
	 * @return the list of legal positions that arise from this particular position
	 */
	public final List<Position> generateChildren(Position position) {
		List<Position> children = new LinkedList<>();
		final long[] bits = position.getBits();
		final int aux = (int) (6L & (position.wm() << 1 | position.wm() << 2));
		final long friends = bits[Piece.BP.ordinal() - aux - 1] | bits[Piece.BN.ordinal() - aux - 1]
				| bits[Piece.BB.ordinal() - aux - 1] | bits[Piece.BR.ordinal() - aux - 1]
				| bits[Piece.BQ.ordinal() - aux - 1] | bits[Piece.BK.ordinal() - aux - 1];
		final long enemies = bits[Piece.WP.ordinal() + aux - 1] | bits[Piece.WN.ordinal() + aux - 1]
				| bits[Piece.WB.ordinal() + aux - 1] | bits[Piece.WR.ordinal() + aux - 1]
				| bits[Piece.WQ.ordinal() + aux - 1] | bits[Piece.WK.ordinal() + aux - 1];

		final long nextWhiteMove = (~position.wm()) & 1L;
		final int whiteMove = (int) position.wm();

		final int kingPiece = KINGS[whiteMove];

		final int kingSquare = squaresMap(bits[kingPiece - 1]);

		final int[] pawnsDirections = new int[][] { BLACK_PAWN_MATRIX_2[kingSquare],
				WHITE_PAWN_MATRIX_2[kingSquare] }[(int) position.wm()];
		final CheckInfo info = isInCheckWhithMask(kingPiece, position.getBits(), position.wm(), pawnsDirections);

		long inCheckMask = info.getInCheckMask();

		final long checkMask = createCheckMask(kingSquare, enemies, friends, position, nextWhiteMove, bits);
		final long[] choice = new long[] { -1L, inCheckMask, 0L, 0L, 0L, 0L };
		inCheckMask = choice[info.getCheckCount()];

		fillChildrenList(children, bits, friends, enemies, position, checkMask, inCheckMask, nextWhiteMove,
				info.getInCheck(), PAWNS[whiteMove], kingSquare, KNIGHTS[whiteMove], BISHOPS[whiteMove],
				ROOKS[whiteMove], QUEENS[whiteMove], kingPiece, pawnsDirections, PAWN_MATRIX1[whiteMove],
				PAWN_MATRIX2[whiteMove]);
		return children;
	}

	/**
	 * Generates a list with the Move objects for the given children in the same
	 * order.
	 * 
	 * @param parent the position from which the children are generated
	 * @param children the list of children
	 * @return a list with the Move objects for the given children in the same order
	 */
	public final List<Move> generateMoves(Position parent, List<Position> children) {
		List<Move> legalMoves = new ArrayList<>(children.size());
		for (Position child : children) {
			final MoveDetector d = new MoveDetector(parent, child);
			legalMoves.add(d.getUnsafeMove());
		}
		return legalMoves;
	}

	private CheckInfo isInCheckWhithMask(int kingPiece, long[] bits, long whiteMoveNumeric, int[] pawnsDirections) {
		long inCheckMask = 0L;
		int checkCount = 0;
		final int kingSquare = squaresMap(bits[kingPiece - 1]);
		long isInCheck = 0L;
		final int aux = (int) (6L & (whiteMoveNumeric << 1 | whiteMoveNumeric << 2));
		final int[] enemies = { Piece.WP.ordinal() + aux, Piece.WN.ordinal() + aux, Piece.WB.ordinal() + aux,
				Piece.WR.ordinal() + aux, Piece.WQ.ordinal() + aux, Piece.WK.ordinal() + aux };
		// pawns directions

		for (int pawnDirection : pawnsDirections) {

			final long enemyPawnDangerLocation = 1L << pawnDirection;
			final long operation = ((bits[enemies[0] - 1] & enemyPawnDangerLocation) >>> pawnDirection);
			isInCheck = isInCheck | operation;

			inCheckMask = inCheckMask | new long[] { 0L, enemyPawnDangerLocation }[(int) operation];
			checkCount += (int) operation;
		}
		// kings directions (only used in test cases)

		long kingDirectionsBits = 0L;

		for (int square : KING_MATRIX[kingSquare]) {
			kingDirectionsBits = kingDirectionsBits | (1L << square);
		}
		final long operation = kingDirectionsBits & bits[enemies[5] - 1];
		final long operation3 = (operation >>> squaresMap(operation));
		isInCheck = isInCheck | operation3;
		checkCount += (int) operation3;
		// knight directions

		long knightDirectionsBits;
		for (int square : KNIGHT_MATRIX[kingSquare]) {
			knightDirectionsBits = 1L << square;

			final long operation2 = ((knightDirectionsBits & bits[enemies[1] - 1]) >>> square);
			isInCheck = isInCheck | operation2;
			inCheckMask = inCheckMask | new long[] { 0L, knightDirectionsBits }[(int) operation2];
			checkCount += (int) operation2;
		}
		// bishops directions
		final long enemyBishopsAndQuens = bits[enemies[2] - 1] | bits[enemies[4] - 1];

		for (int i = 0; i < 4; i++) {
			final long visible = visibleMetrics.visibleSquares(bits, new int[] { i },
					kingSquare, whiteMoveNumeric);

			final long isPresent = generatorUtil.hasBitsPresent(enemyBishopsAndQuens & visible);
			isInCheck = isInCheck | isPresent;
			inCheckMask = inCheckMask | new long[] { 0L, visible }[(int) isPresent];
			checkCount += (int) isPresent;
		}

		// rooks directions
		final long enemyRooksAndQuens = bits[enemies[3] - 1] | bits[enemies[4] - 1];
		for (int i = 4; i < 8; i++) {
			final long visible = visibleMetrics.visibleSquares(bits, new int[] { i }, kingSquare, whiteMoveNumeric);

			final long isPresent = generatorUtil.hasBitsPresent(enemyRooksAndQuens & visible);
			isInCheck = isInCheck | isPresent;
			inCheckMask = inCheckMask | new long[] { 0L, visible }[(int) isPresent];
			checkCount += (int) isPresent;
		}
		return new CheckInfo(isInCheck, inCheckMask, checkCount);
	}

}
