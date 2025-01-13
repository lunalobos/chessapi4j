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

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface for search algorithms. Classes that implements this interface
 * should look for best move. In all the cases an evaluator factory is required.
 * This ensure good responsibility differentiation and an easier testing. This
 * it's maybe not the best way to approach search algorithms because using an
 * interface means your implementation methods never can be consider inline
 * methods by the JVM. Pure final classes with final methods should be
 * consider..
 *
 * @author lunalobos
 * @since 1.2.0
 */

public interface Search {

	/**
	 * Seek for the best move if any. An evaluator factory injection is required-
	 *
	 * @param p                initial position
	 * @param evaluatorFactory factory for evaluator implementation
	 * @param depth            search depth
	 *
	 * @return an optional with the best move if any
	 */
	Optional<Move> seekBestMove(Position p, Supplier<Evaluator> evaluatorFactory, int depth);

	/**
	 * Seek for the best move if any. An evaluator factory injection is required-
	 *
	 * @param p                initial position
	 * @param evaluatorFactory factory for evaluator implementation
	 * @param depth            search depth
	 * @param sampleSize       sample size in case the algorithms requires such
	 *                         parameter.
	 * @return an optional with the best move if any
	 */
	Optional<Move> seekBestMove(Position p, Supplier<Evaluator> evaluatorFactory, int depth, int sampleSize);

	/**
	 * Seek for the best move if any. An evaluator factory injection is required.
	 *
	 * @param p                initial position
	 * @param evaluatorFactory factory for evaluator implementation
	 * @param depth            search depth
	 * @param sampleSize       sample size in case the algorithms requires such
	 *                         parameter.
	 * @param searchMoves	   moves to search
	 * @return an optional with the best move if any
	 */
	Optional<Move> seekBestMove(Position p, Supplier<Evaluator> evaluatorFactory, int depth, int sampleSize, String searchMoves);


}
