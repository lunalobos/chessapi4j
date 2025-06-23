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
package chessapi4j.functional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Random;

/**
 * @author lunalobos
 * @since 1.2.9
 */
@Getter
@AllArgsConstructor
@Builder
final class Container {
    final Random random;
    final InternalUtil internalUtil;
    final MatrixUtil matrixUtil;
    final VisibleMetrics visibleMetrics;
    final LackOfMaterialMetrics lackOfMaterialMetrics;
    final CheckMetrics checkMetrics;
    final CheckmateMetrics checkmateMetrics;
    final StalemateMetrics stalemateMetrics;
    final PawnGenerator pawnGenerator;
    final KnightGenerator knightGenerator;
    final BishopGenerator bishopGenerator;
    final RookGenerator rookGenerator;
    final QueenGenerator queenGenerator;
    final KingGenerator kingGenerator;
    final BitboardGenerator bitboardGenerator;
    final Generator generator;
}
