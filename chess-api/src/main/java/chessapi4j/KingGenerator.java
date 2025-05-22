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

import java.util.List;

//singleton bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
final class KingGenerator {
	private static final Logger logger = LoggerFactory.getLogger(KingGenerator.class);
    private GeneratorUtil generatorUtil;
	private CheckMetrics checkMetrics;

    public KingGenerator(GeneratorUtil generatorUtil, CheckMetrics checkMetrics) {
        this.generatorUtil = generatorUtil;
		this.checkMetrics = checkMetrics;
		logger.instanciation();
    }

    public void kingMoves(int square, int pieceType, long enemies, long friends, Position pos, long inCheck,
			List<Position> children) {
		final int[] kingDirections = GeneratorUtil.KING_MATRIX[square];
		final long emptyOrEnemy = ~friends;
		long moves = 0L;
		for (int move : kingDirections) {
			moves = moves | (1L << move);
		}
		generateKingPositions(moves & emptyOrEnemy, pieceType, square, enemies, pos, children);
		long castleMoves = 0L;
		castleMoves = castleMoves | (generatorUtil.isShortCastleWhiteEnable(square, enemies, friends, pos, inCheck) << 6);
		castleMoves = castleMoves | (generatorUtil.isLongCastleWhiteEnable(square, enemies, friends, pos, inCheck) << 2);
		castleMoves = castleMoves | (generatorUtil.isShortCastleBlackEnable(square, enemies, friends, pos, inCheck) << 62);
		castleMoves = castleMoves | (generatorUtil.isLongCastleBlackEnable(square, enemies, friends, pos, inCheck) << 58);
		generateCastlePositions(castleMoves, pieceType, square, pos, children);
	}

	public long kingMoves(int square, int pieceType, long enemies, long friends, long inCheck, long[] bitboards, boolean whiteMove, long wk, long wq, long bk, long bq) {
		final int[] kingDirections = GeneratorUtil.KING_MATRIX[square];
		final long emptyOrEnemy = ~friends;
		long moves = 0L;
		for (int move : kingDirections) {
			moves = moves | (1L << move);
		}
		long legalMoves = generateKingPositions(moves & emptyOrEnemy, pieceType, square, enemies, bitboards, whiteMove);
		long castleMoves = 0L;
		castleMoves = castleMoves | (generatorUtil.isShortCastleWhiteEnable(square, enemies, friends, bitboards, wk, whiteMove ? 1L : 0L, inCheck) << 6);
		castleMoves = castleMoves | (generatorUtil.isLongCastleWhiteEnable(square, enemies, friends, bitboards, wq, whiteMove ? 1L : 0L, inCheck) << 2);
		castleMoves = castleMoves | (generatorUtil.isShortCastleBlackEnable(square, enemies, friends, bitboards, bk, whiteMove ? 1L : 0L, inCheck) << 62);
		castleMoves = castleMoves | (generatorUtil.isLongCastleBlackEnable(square, enemies, friends, bitboards, bq, whiteMove ? 1L : 0L, inCheck) << 58);
		return castleMoves | legalMoves;
	}

    private void generateKingPositions(long moves, int pieceType, int originSquare, long enemies, Position position,
			List<Position> children) {
		while (moves != 0L) {
			final long move = moves & -moves;
			final Position newPosition = position.makeClone();
			generatorUtil.makeMove(newPosition, move, pieceType, originSquare, enemies, position);
			final Position testPosition = newPosition.makeClone();
			testPosition.setWM(position.wm());
			if (generatorUtil.isInCheck(testPosition) != 1L) {
				newPosition.changeColorToMove();
				generatorUtil.applyCastleRules(newPosition);
				newPosition.changeColorToMove();
				children.add(newPosition);
			}
			moves = moves & ~move;
		}
	}

	private long generateKingPositions(long moves, int pieceType, int originSquare, long enemies, long[] bitboards, boolean whiteMove) {
		var movesCopy = moves;
		var legalMoves = 0L;
		var bitboardsCopy = new long[bitboards.length];
		System.arraycopy(bitboards, 0, bitboardsCopy, 0, bitboards.length);
		while (movesCopy != 0L) {
			final long move = movesCopy & -movesCopy;
			makeMove(originSquare, move, pieceType, bitboardsCopy);
			if (!checkMetrics.isInCheck(bitboards, whiteMove)) {
				legalMoves |= move;
			}
			movesCopy = movesCopy & ~move;
		}
		return legalMoves;
	}

	private void makeMove(int from, long move, int pieceType, long[] bitboards){
		for (var index = 0; index < 12; index++) {
            bitboards[index] = bitboards[index] & (~move);
        }
        bitboards[pieceType - 1] = (bitboards[pieceType - 1] & (~(1L << from))) | move;
		
	}

    private void generateCastlePositions(long moves, int kingPiece, int square, Position position,
			List<Position> children) {
		while (moves != 0L) {
			final long move = moves & -moves;
			final Position newPosition = position.makeClone();
			makeCastle(newPosition, move, kingPiece, square);
			children.add(newPosition);
			moves = moves & ~move;
		}
	}

    private void makeCastle(Position position, long move, int pieceType, int originSquare) {		
		position.makeCastle(move, pieceType, originSquare);
		generatorUtil.applyCastleRules(position);
		position.setHalfMovesCounter(position.getHalfMovesCounter() + 1);
		position.increaseMovesCounter();
		position.setEnPassant(-1);
	}
}
