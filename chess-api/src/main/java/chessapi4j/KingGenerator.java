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
    private final GeneratorUtil generatorUtil;

    public KingGenerator(GeneratorUtil generatorUtil) {
        this.generatorUtil = generatorUtil;
		logger.instantiation();
    }

    public void kingMoves(int square, int pieceType, long enemies, long friends, Position pos, long inCheck,
			List<Position> children) {
		final long emptyOrEnemy = ~friends;
		long moves = GeneratorUtil.KING_MOVES[square];

		generateKingPositions(moves & emptyOrEnemy, pieceType, square, enemies, pos, children);
		long castleMoves = 0L;
		castleMoves = castleMoves | (generatorUtil.isShortCastleWhiteEnable(square, enemies, friends, pos, inCheck) << 6);
		castleMoves = castleMoves | (generatorUtil.isLongCastleWhiteEnable(square, enemies, friends, pos, inCheck) << 2);
		castleMoves = castleMoves | (generatorUtil.isShortCastleBlackEnable(square, enemies, friends, pos, inCheck) << 62);
		castleMoves = castleMoves | (generatorUtil.isLongCastleBlackEnable(square, enemies, friends, pos, inCheck) << 58);
		generateCastlePositions(castleMoves, pieceType, square, pos, children);
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
