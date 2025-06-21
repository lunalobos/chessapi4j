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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import lombok.ToString;

//singleton bean
/**
 * Montecarlo search implementation
 *
 * @author lunalobos
 * @since 1.2.0
 */
@Data
class MontecarloSearch implements Search {
	private static final Logger logger = LoggerFactory.getLogger(MontecarloSearch.class);
	private int depth;
	private Supplier<Evaluator> $evaluationFactory;
	private Position initialPosition;
	private int sampleSize;
	private double $totalScore;
	private List<MoveData> candidates;

	public MontecarloSearch() {
		logger.instantiation();
	}

	@Override
	public Optional<Move> seekBestMove(Position p, Supplier<Evaluator> evaluatorFactory, int depth) {
		return seekBestMove(p, evaluatorFactory, depth, 3);
	}

	@Override
	public Optional<Move> seekBestMove(Position p, Supplier<Evaluator> evaluatorFactory, int depth, int numberOfMoves) {
		return seekBestMove(p, evaluatorFactory, depth, numberOfMoves, null);
	}

	@Override
	public Optional<Move> seekBestMove(Position p, Supplier<Evaluator> evaluatorFactory, int depth, int sampleSize,
			String searchMoves) {

		final List<Move> searchMovesList = new LinkedList<>();

		Pattern movePattern = Pattern.compile("[a-z1-8]{4,5}");

		Matcher moveMatcher = movePattern.matcher(searchMoves == null ? "" : searchMoves);

		while (moveMatcher.find()) {
			try {
				searchMovesList.add(MoveFactory.instance(moveMatcher.group(), p.isWhiteMove()));
			} catch (MovementException e) {
				logger.error("MovementException: %s", e.getMessage());
			}
		}

		final Predicate<MoveData> moveFilter = searchMovesList.isEmpty() ? moveData -> true
				: moveData -> searchMovesList.contains(moveData.getMove());

		initialPosition = p;

		this.$evaluationFactory = evaluatorFactory;

		this.depth = Math.max(depth, 5);

		this.sampleSize = sampleSize;

		candidates = candidateMoves().filter(moveFilter).peek(md -> {
			md.calculate(this.depth);
		}).collect(Collectors.toCollection(ArrayList::new));

		candidates.sort((c1, c2) -> (initialPosition.isWhiteMove() ? -1 : 1) *
				Double.compare(c1.getScore(), c2.getScore()));

		$totalScore = candidates.stream().mapToDouble(MoveData::getScore).sum();
		candidates.forEach(md -> {
			md.setScore(md.getScore() / $totalScore);
		});
		return candidates.stream().map(MoveData::getMove).findFirst();
	}

	private Stream<MoveData> candidateMoves() {
		List<Position> children = GeneratorFactory.instance().generateChildren(initialPosition);
		Iterator<Position> posIterator = children.iterator();
		Iterator<Move> moveIterator = GeneratorFactory.instance().generateMoves(initialPosition, children).iterator();
		List<MoveData> candidates = new LinkedList<>();
		while (posIterator.hasNext()) {
			candidates.add(new MoveData(moveIterator.next(), posIterator.next(), sampleSize, $evaluationFactory));
		}
		return candidates.parallelStream();
	}

}

@Data
class MoveData {

	private int $positionsCounter;
	private Supplier<Evaluator> $evaluationFactory;
	private Move move;
	@ToString.Exclude
	private Position position;
	private int $sampleSize;
	private double score;

	public MoveData(Move move, Position position, int sample, Supplier<Evaluator> factory) {
		this.move = move;
		this.position = position;
		score = 0.0;
		this.$sampleSize = sample;
		this.$evaluationFactory = factory;
		$positionsCounter = 0;

	}

	public void calculate(int depth) {
		int sum = firstIteration(depth);
		score = (double) sum / (double) $positionsCounter;
	}

	private int firstIteration(int depth) {

		List<Position> children = GeneratorFactory.instance().generateChildren(position);
		int output = 0;
		for (Position child : children) {
			output += secondIteration(child, depth - 1);
		}
		return output;
	}

	private int secondIteration(Position p, int depth) {
		List<Position> children = GeneratorFactory.instance().generateChildren(p);
		int output = 0;
		for (Position child : children) {
			output += subsequentIteration(child, depth - 1);
		}
		return output;
	}

	private int subsequentIteration(Position p, int depth) {
		if (depth == 0) {
			$positionsCounter++;
			return $evaluationFactory.get().evaluate(p);
		}
		List<Position> children = GeneratorFactory.instance().generateChildren(p);
		if (children.isEmpty()) {
			Rules.setStatus(position);
			int c = position.isWhiteMove() ? 1 : -1;
			return position.isCheckmate() ? c * 100000 : 0;
		}
		// sample
		if (children.size() > $sampleSize) {
			Collections.shuffle(children);
			children.subList($sampleSize, children.size()).clear();
		}

		int output = 0;
		for (Position child : children) {
			output += subsequentIteration(child, depth - 1);
		}
		return output;
	}

}
