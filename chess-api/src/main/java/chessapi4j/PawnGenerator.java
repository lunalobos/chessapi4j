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
final class PawnGenerator {
	private static final Logger logger = LoggerFactory.getLogger(PawnGenerator.class);
    private static final int[] INDEXES = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
    private static final int[] EP_CHOICE = new int[] { 8, -8 };

    protected static final long isPromotion(int finalSquare) {
        //		return CORONATION_REF[finalSquare];
                return ((((finalSquare >>> 3) & 7L) >>> 2) & ((((finalSquare >>> 3) & 7L) >>> 1) & 1L)
                        & (((finalSquare >>> 3) & 7L) & 1L))
                        | (((((63 - finalSquare) >>> 3) & 7L) >>> 2) & (((((63 - finalSquare) >>> 3) & 7L) >>> 1) & 1L)
                                & ((((63 - finalSquare) >>> 3) & 7L) & 1L));
    }

    private static int transformEnPassant(int enPassant, long whiteMoveNumeric) {
		return 8 * (-2 * (int) whiteMoveNumeric + 1) + enPassant;
	}

    private VisibleMetrics visibleMetrics;
    private GeneratorUtil generatorUtil;

	public PawnGenerator(VisibleMetrics visibleMetrics, GeneratorUtil generatorUtil) {
		this.visibleMetrics = visibleMetrics;
		this.generatorUtil = generatorUtil;
		logger.instanciation();
	}

    public void pawnMoves(long br, int square, int[] pawnsDirections, int pieceType, int[][] matrix1, int[][] matrix2,
			int kingSquare, long enemies, long friends, Position position, long checkMask, long inCheckMask,
			long nextWhiteMove, List<Position> children) {
		final int[] captureArray = matrix2[square];
		long captureMoves = 0L;
		long captureCoronationMoves = 0L;
		for (int squareToCapture : captureArray) {
			captureCoronationMoves = captureCoronationMoves | ((1L & isPromotion(squareToCapture)) << squareToCapture);
			captureMoves = captureMoves | ((1L & ~isPromotion(squareToCapture)) << squareToCapture);
		}
		final int normalizedEnPassant = transformEnPassant(position.getEnPassant(), nextWhiteMove);
		final long possibleEnPassant = (1L << normalizedEnPassant) & captureMoves;
		captureMoves = captureMoves & enemies;
		captureCoronationMoves = captureCoronationMoves & enemies;
		final int[] advanceMatrix = matrix1[square];
		long advanceMoves = 0L;
		long advanceCoronationMoves = 0L;
		long advanceEnPassantMoves = 0L;
		for (int squareToOccupy : advanceMatrix) {
			advanceCoronationMoves = advanceCoronationMoves | ((1L & isPromotion(squareToOccupy)) << squareToOccupy);
			advanceEnPassantMoves = advanceEnPassantMoves
					| ((1L & isEnPassant(square, squareToOccupy, position.wm())) << squareToOccupy);
			advanceMoves = advanceMoves | ((1L & ~isPromotion(squareToOccupy)
					& ~isEnPassant(square, squareToOccupy, position.wm())) << squareToOccupy);
		}
		final long visible = visibleMetrics.visibleSquaresRook(square, friends, enemies);
		advanceMoves = advanceMoves & ~(friends | enemies | ~visible);
		advanceCoronationMoves = advanceCoronationMoves & ~(friends | enemies | ~visible);
		advanceEnPassantMoves = advanceEnPassantMoves & ~(friends | enemies | ~visible);
		final long pseudoCoronationMoves = advanceCoronationMoves | captureCoronationMoves;
		final long pseudoLegalMoves = advanceMoves | captureMoves;
		final long operation = (br & checkMask) >>> generatorUtil.squaresMap(br);
		final long defense = generatorUtil.defenseDirection(kingSquare, square);
		final long[] pin1 = new long[] { -1L, pseudoLegalMoves & checkMask & defense };
		final long pinMask1 = pin1[(int) operation];
		final long[] pin2 = new long[] { -1L, pseudoCoronationMoves & checkMask & defense };
		final long pinMask2 = pin2[(int) operation];
		final long[] pin3 = new long[] { -1L, advanceEnPassantMoves & checkMask & defense };
		final long pinMask3 = pin3[(int) operation];
		final long legalMoves = pseudoLegalMoves & pinMask1 & inCheckMask;
		final long legalCoronationMoves = pseudoCoronationMoves & pinMask2 & inCheckMask;
		final long legalAdvanceEnPassantMoves = advanceEnPassantMoves & pinMask3 & inCheckMask;
		generatorUtil.generatePositions(legalMoves, pieceType, square, pawnsDirections, enemies, position, children);
		generateCoronations(legalCoronationMoves, pieceType, square, pawnsDirections, position, children);
		generatePositionsWithEnPassant(legalAdvanceEnPassantMoves, pieceType, square, pawnsDirections, enemies,
				position, children);
		generateEnPassantCaptures(possibleEnPassant, pieceType, square, pawnsDirections, captureArray, position,
				children);
	}

    

    private long isEnPassant(int originSquare, int finalSquare, long whiteMoveNumeric) {
		final long difference = finalSquare - originSquare;
		final long choice[] = new long[] { ~difference + 1, difference };
		final long maskedDifference = 16L & choice[(int) whiteMoveNumeric];
		return maskedDifference >>> 4;
	}

    protected void generateCoronations(long moves, int pieceType, int square, int[] pawnsDirections, Position position,
			List<Position> children) {
		final int[] coronationPieces = new int[] { pieceType + 1, pieceType + 2, pieceType + 3, pieceType + 4 };
		while (moves != 0L) {
			final long move = moves & -moves;
			for (int coronationPiece : coronationPieces) {
				final Position newPosition = position.makeClone();
				makeCoronation(newPosition, move, pieceType, coronationPiece, square);
				children.add(newPosition);
			}
			moves = moves & ~move;
		}
	}

    private void makeCoronation(Position position, long move, int pieceType, int pieceToCrown, int originSquare) {
		final long[] bits = position.getBits();
		for (int index : INDEXES) {
			bits[index] = bits[index] & (~move);
		}
		bits[pieceType - 1] = (bits[pieceType - 1] & (~(1L << originSquare)));
		bits[pieceToCrown - 1] = bits[pieceToCrown - 1] | move;
		position.setBits(bits);
		position.changeColorToMove();
		generatorUtil.applyCastleRules(position);
		position.setHalfMovesCounter(0);
		position.increaseMovesCounter();
		position.setEnPassant(-1);
	}

    protected void generatePositionsWithEnPassant(long moves, int pieceType, int square, int[] pawnsDirections,
			long enemies, Position position, List<Position> children) {
		while (moves != 0L) {
			final long move = moves & -moves;
			final Position newPosition = position.makeClone();
			generatorUtil.makeMove(newPosition, move, pieceType, square, enemies, position);
			newPosition.setEnPassant(generatorUtil.squaresMap(move));
			children.add(newPosition);
			moves = moves & ~move;
		}
	}

    private void generateEnPassantCaptures(long moves, int pieceType, int originSquare, int[] pawnsDirections,
			int[] captureArray, Position position, List<Position> children) {
		while (moves != 0L) {
			final long move = moves & -moves;
			final long capture = 1L << (generatorUtil.squaresMap(move) + EP_CHOICE[(int) position.wm()]);
			Position newPosition = position.makeClone();
			makeEnPassantCapture(newPosition, capture, move, pieceType, originSquare);
			Position testPosition = newPosition.makeClone();
			testPosition.setWM(position.wm());
			if (generatorUtil.isInCheckD(testPosition, pawnsDirections) != 1L)
				children.add(newPosition);
			moves = moves & ~move;
		}
	}

    private void makeEnPassantCapture(Position position, long capture, long move, int pieceType, int originSquare) {
		final long[] bits = position.getBits();
		for (int index : INDEXES) {
			bits[index] = bits[index] & (~capture);
		}
		bits[pieceType - 1] = (bits[pieceType - 1] & (~(1L << originSquare))) | move;
		position.setBits(bits);
		position.changeColorToMove();
		position.setHalfMovesCounter(0);
		position.increaseMovesCounter();
		position.setEnPassant(-1);
	}
    

}
