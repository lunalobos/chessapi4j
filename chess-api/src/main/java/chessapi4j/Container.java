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

import lombok.Builder;

import java.util.Random;
import java.util.function.Supplier;
import java.util.function.Function;

/**
 * @author lunalobos
 * @since 1.2.9
 */
final class Container {
    static final Container.ContainerBuilder defaultBuilder = Container.builder()
            .randomFactory(Random::new)
            .matrixUtilFactory(MatrixUtil::new)
            .visibleMetricsUtilFactory(VisibleMetricsUtil::new)
            .visibleMagicFactory(vmu -> r -> new VisibleMagic(vmu, r))
            .visibleMetricsFactory(vmu -> vm -> mu ->
            new VisibleMetrics(vmu, vm, mu))
            .generatorUtilFactory(vmu -> mu -> new GeneratorUtil(vmu, mu))
            .pawnGeneratorFactory(vmu -> gu -> new PawnGenerator(vmu, gu))
            .knightGeneratorFactory(gu -> mu -> new KnightGenerator(gu, mu))
            .bishopGeneratorFactory(vm -> gu -> new BishopGenerator(vm, gu))
            .rookGeneratorFactory(vm -> gu -> new RookGenerator(vm, gu))
            .queenGeneratorFactory(vm -> gu -> new QueenGenerator(vm, gu))
            .kingGeneratorFactory(KingGenerator::new)
            .generatorFactory(pg -> ng -> bg -> rg ->
    qg ->kg -> vm -> gu ->
            new Generator(pg, ng, bg, rg, qg, kg, vm, gu));

    final Random random;
    final MatrixUtil matrixUtil;
    final VisibleMetricsUtil visibleMetricsUtil;
    final VisibleMagic visibleMagic;
    final VisibleMetrics visibleMetrics;
    final GeneratorUtil generatorUtil;
    final PawnGenerator pawnGenerator;
    final KnightGenerator knightGenerator;
    final BishopGenerator bishopGenerator;
    final RookGenerator rookGenerator;
    final QueenGenerator queenGenerator;
    final KingGenerator kingGenerator;
    final Generator generator;

    @Builder
    Container(
            Supplier<Random> randomFactory,
            Supplier<MatrixUtil> matrixUtilFactory,
            Supplier<VisibleMetricsUtil> visibleMetricsUtilFactory,
            Function<VisibleMetricsUtil,Function<Random, VisibleMagic>> visibleMagicFactory,
            Function<VisibleMetricsUtil,Function<VisibleMagic,
                    Function<MatrixUtil, VisibleMetrics>>> visibleMetricsFactory,
            Function<VisibleMetrics, Function<MatrixUtil, GeneratorUtil>> generatorUtilFactory,
            Function<VisibleMetrics, Function<GeneratorUtil, PawnGenerator>> pawnGeneratorFactory,
            Function<GeneratorUtil, Function<MatrixUtil, KnightGenerator>> knightGeneratorFactory,
            Function<VisibleMetrics, Function<GeneratorUtil, BishopGenerator>> bishopGeneratorFactory,
            Function<VisibleMetrics, Function<GeneratorUtil, RookGenerator>> rookGeneratorFactory,
            Function<VisibleMetrics, Function<GeneratorUtil, QueenGenerator>> queenGeneratorFactory,
            Function<GeneratorUtil, KingGenerator> kingGeneratorFactory,
            Function<PawnGenerator, Function<KnightGenerator, Function<BishopGenerator,Function<RookGenerator,
                            Function<QueenGenerator, Function<KingGenerator, Function<VisibleMetrics,
                                    Function<GeneratorUtil, Generator>>>>>>>>
                    generatorFactory
    ){
        this.random = randomFactory.get();
        this.matrixUtil = matrixUtilFactory.get();
        this.visibleMetricsUtil = visibleMetricsUtilFactory.get();
        this.visibleMagic = visibleMagicFactory
                .apply(visibleMetricsUtil)
                .apply(random);
        this.visibleMetrics = visibleMetricsFactory
                .apply(visibleMetricsUtil)
                .apply(visibleMagic)
                .apply(matrixUtil);
        this.generatorUtil = generatorUtilFactory
                .apply(visibleMetrics)
                .apply(matrixUtil);
        this.pawnGenerator = pawnGeneratorFactory
                .apply(visibleMetrics)
                .apply(generatorUtil);
        this.knightGenerator = knightGeneratorFactory
                .apply(generatorUtil)
                .apply(matrixUtil);
        this.bishopGenerator = bishopGeneratorFactory
                .apply(visibleMetrics)
                .apply(generatorUtil);
        this.rookGenerator = rookGeneratorFactory
                .apply(visibleMetrics)
                .apply(generatorUtil);
        this.queenGenerator = queenGeneratorFactory
                .apply(visibleMetrics)
                .apply(generatorUtil);
        this.kingGenerator = kingGeneratorFactory
                .apply(generatorUtil);
        this.generator = generatorFactory
                .apply(pawnGenerator)
                .apply(knightGenerator)
                .apply(bishopGenerator)
                .apply(rookGenerator)
                .apply(queenGenerator)
                .apply(kingGenerator)
                .apply(visibleMetrics)
                .apply(generatorUtil);
    }
}


