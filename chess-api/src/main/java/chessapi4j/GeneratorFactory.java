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


import java.util.Random;

/**
 * Factory class for {@code Generator} instances.
 * 
 * @author lunalobos
 * @since 1.0.0
 */
public class GeneratorFactory {
	// dependency injection chain
	protected static final Random random = new Random();
	private static final VisibleMetricsUtil visibleMetricsUtil = new VisibleMetricsUtil();
	protected static final VisibleMagic visibleMagic = new VisibleMagic(visibleMetricsUtil, random);
	protected static final VisibleMetrics visibleMetrics = new VisibleMetrics(visibleMetricsUtil, visibleMagic);
	protected static final GeneratorUtil generatorUtil = new GeneratorUtil(visibleMetrics);
	private static final PawnGenerator pawnGenerator = new PawnGenerator(visibleMetrics, generatorUtil);
	private static final KnightGenerator knightGenerator = new KnightGenerator(generatorUtil);
	private static final BishopGenerator bishopGenerator = new BishopGenerator(visibleMetrics, generatorUtil);
	private static final RookGenerator rookGenerator = new RookGenerator(visibleMetrics, generatorUtil);
	private static final QueenGenerator queenGenerator = new QueenGenerator(visibleMetrics, generatorUtil);
	private static final KingGenerator kingGenerator = new KingGenerator(generatorUtil);
	private static final Generator generator = new Generator(pawnGenerator, knightGenerator, bishopGenerator,
			rookGenerator, queenGenerator, kingGenerator, visibleMetrics, generatorUtil);

	/**
	 * Generator main implementation
	 *
	 * @return a new {@code Generator} instance
	 */
	public static Generator instance() {
		return generator;
	}

}
