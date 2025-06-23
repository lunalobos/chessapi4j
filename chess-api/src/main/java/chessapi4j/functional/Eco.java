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
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import chessapi4j.EcoDescriptor;
import lombok.AllArgsConstructor;
import lombok.Data;

// singleton bean
// passing tests
/**
 * This internal class handles the search of the ECO description. Thansk to
 * the csv file created by Destaq. An original version is provided at
 * <a href="https://github.com/Destaq/chess-graph/blob/master/elo_reading/openings_sheet.csv">openings_sheet</a>
 * The file in resources has been sanitized.
 *
 * @author lunalobos
 * @since 1.2.7
 *
 */
final class Eco {
    private static final Logger logger = Factory.getLogger(Eco.class);
    private final Map<String, EcoDescriptor> movesMap;
    private final Map<Position, EcoDescriptor> positionMap;
    private final CsvParser csvParser;

    Eco(CsvParser csvParser) {
        this.csvParser = csvParser;
        movesMap = loadMoves();
        movesMap.remove("moves");// removing the header
        positionMap = loadPositionMap();
        logger.instantiation();
    }

    private Map<String, EcoDescriptor> loadMoves() {
        try (var is = Eco.class.getClassLoader().getResourceAsStream("openings_sheet.csv")) {
            var rows = csvParser.parseInputStream(is);
            return rows.stream().map(list -> new EcoRow(list.get(0), list.get(1), list.get(2)))
                    .peek(er -> er.setMoves(er.getMoves().trim())) // this was so annoying
                    .collect(HashMap::new, (map, er) -> map.put(er.getMoves(), er.getDescriptor()),
                            HashMap::putAll);
        } catch (IOException e) {
            var fatalException = new ResourceAccessException("openings_sheet.csv", e);
            throw fatalException;
        }
    }

    private Position makeMove(Position position, String sanMove) {
        return PGNHandler.toUCI(position, sanMove).map(position::move)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Move: %s, position: %s", sanMove, position)));
    }

    private Map<Position, EcoDescriptor> loadPositionMap() {
        return movesMap.entrySet().stream().map(entry -> {
            var moves = new ArrayDeque<>(Arrays.asList(entry.getKey().split("\\s+")));
            var position = new Position();

            while (!moves.isEmpty()) {
                try {
                    position = makeMove(position, moves.pop());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(String.format("entry: %s, position: %s", entry, position), e);
                }
            }
            return Map.entry(position, entry.getValue());
        }).collect(HashMap::new, (map, e) -> map.merge(e.getKey(), e.getValue(), (v1, v2) -> v1),
                HashMap::putAll);
    }

    public Optional<EcoDescriptor> get(String moves) {
        return Optional.ofNullable(movesMap.get(moves));
    }

    public Optional<EcoDescriptor> get(Position position) {
        return Optional.ofNullable(positionMap.get(position));
    }

}

@Data
@AllArgsConstructor
class EcoRow {
    private String eco;
    private String name;
    private String moves;

    public EcoDescriptor getDescriptor() {
        return new EcoDescriptor(eco, name);
    }
}
