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

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;

// passing tests
/**
 * This internal class handles the search of the ECO description. Thansk to
 * the csv file created by Destaq. An original version is provided at
 * https://github.com/Destaq/chess-graph/blob/master/elo_reading/openings_sheet.csv
 * <p>The file in resources has been sanitized.
 * 
 * @author lunalobos
 * @since 1.2.7
 * 
 */
class Eco {

    private Map<String, EcoDescriptor> movesMap;
    private Map<Position, EcoDescriptor> positionMap;

    protected Eco() {
        movesMap = loadMoves();
        movesMap.remove("moves");// removing the header
        positionMap = loadPositionMap();
    }

    private Map<String, EcoDescriptor> loadMoves() {
        try (var is = Eco.class.getClassLoader().getResourceAsStream("openings_sheet.csv")) {
            var csvParser = new CsvParser();
            var rows = csvParser.parseInputStream(is);
            return rows.stream().map(list -> new EcoRow(list.get(0), list.get(1), list.get(2)))
                    .peek(er -> er.setMoves(er.getMoves().trim())) // this was so annoying to understand
                    .collect(HashMap::new, (map, er) -> map.put(er.getMoves(), er.getDescriptor()),
                            (m1, m2) -> m1.putAll(m2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Position makeMove(Position position, String sanMove) {
        return PGNHandler.toUCI(position, sanMove).map(position::childFromMove)
                .map(o -> o.orElseThrow(
                        () -> new IllegalArgumentException("Move: %s, position: %s".formatted(sanMove, position))))
                .orElseThrow(() -> new IllegalArgumentException("Move: %s, position: %s".formatted(sanMove, position)));
    }

    private Map<Position, EcoDescriptor> loadPositionMap() {
        return movesMap.entrySet().stream().map(entry -> {
            var moves = new ArrayDeque<>(Arrays.asList(entry.getKey().split("\\s+")));
            var position = new Position();

            while (!moves.isEmpty()) {
                try {
                    position = makeMove(position, moves.pop());
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("entry: %s, position: %s".formatted(entry, position), e);
                }
            }
            return Map.entry(position, entry.getValue());
        }).collect(HashMap::new, (map, e) -> map.merge(e.getKey(), e.getValue(), (v1, v2) -> v1),
                (m1, m2) -> m1.putAll(m2));
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
