package chessapi4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;
import lombok.ToString;

/**
 * Montecarlo search implementation
 *
 * @author lunalobos
 */
@Data
class MontecarloSearch implements Search {

	private int depth;
	private Supplier<Evaluator> $evaluationFactory;
	private Supplier<Generator> $generatorFactory;
	private Position initialPosition;
	private int sampleSize;
	private double $totalScore;
	private List<MoveData> candidates;

	@Override
	public Optional<Move> seekBestMove(Position p, Supplier<Evaluator> evaluatorFactory, int depth) {
		return seekBestMove(p, evaluatorFactory, depth, 3);
	}

	@Override
	public Optional<Move> seekBestMove(Position p, Supplier<Evaluator> evaluatorFactory, int depth, int numberOfMoves) {
		return seekBestMove(p, evaluatorFactory, depth, () -> GeneratorFactory.instance(), numberOfMoves);
	}

	@Override
	public Optional<Move> seekBestMove(Position p, Supplier<Evaluator> evaluatorFactory, int depth,
			Supplier<Generator> generatorFactory, int sampleSize) {

		initialPosition = p;

		this.$evaluationFactory = evaluatorFactory;

		this.depth = depth < 5 ? 5 : depth;

		this.$generatorFactory = generatorFactory;

		this.sampleSize = sampleSize;

		candidates = candidateMoves().peek(md -> {
			md.calculate(this.depth);
		}).collect(Collectors.toCollection(ArrayList::new));

		Collections.sort(candidates,
				(c1, c2) -> (initialPosition.isWhiteMove() ? -1 : 1) * Double.compare(c1.getScore(), c2.getScore()));

		$totalScore = candidates.stream().mapToDouble(c -> c.getScore()).sum();
		candidates.stream().forEach(md -> {
			md.setScore(md.getScore() / $totalScore);
		});
		return candidates.stream().map(md -> md.getMove()).findFirst();
	}

	private Stream<MoveData> candidateMoves() {
		List<Position> children = GeneratorFactory.instance().generateChildren(initialPosition);
		Iterator<Position> posIterator = children.iterator();
		Iterator<Move> moveIterator = GeneratorFactory.instance().generateMoves(initialPosition, children).iterator();
		List<MoveData> candidates = new LinkedList<>();
		while (posIterator.hasNext()) {
			candidates.add(new MoveData(moveIterator.next(), posIterator.next(), sampleSize, $evaluationFactory,
					$generatorFactory));
		}
		return candidates.parallelStream();
	}

}

@Data
class MoveData {

	private int $positionsCounter;
	private Supplier<Evaluator> $evaluationFactory;
	private Supplier<Generator> $generatorFactory;
	private Move move;
	@ToString.Exclude
	private Position position;
	private int $sampleSize;
	private double score;

	public MoveData(Move move, Position position, int sample, Supplier<Evaluator> factory,
			Supplier<Generator> generatorFactory) {
		this.move = move;
		this.position = position;
		score = 0.0;
		this.$sampleSize = sample;
		this.$evaluationFactory = factory;
		$positionsCounter = 0;
		$generatorFactory = generatorFactory;
	}

	public double calculate(int depth) {
		int sum = firstItearion(depth);
		score = (double) sum / (double) $positionsCounter;
		return score;
	}

	private int firstItearion(int depth) {

		List<Position> children = $generatorFactory.get().generateChildren(position);
		int output = 0;
		for (Position child : children) {
			output += secondIteration(child, depth - 1);
		}
		return output;
	}

	private int secondIteration(Position p, int depth) {
		List<Position> children = $generatorFactory.get().generateChildren(p);
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
		List<Position> children = $generatorFactory.get().generateChildren(p);
		if (children.size() == 0) {
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
