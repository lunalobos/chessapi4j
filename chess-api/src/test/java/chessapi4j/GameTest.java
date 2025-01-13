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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class GameTest {

    @Test
    void eco() {
        var is = this.getClass().getClassLoader().getResourceAsStream("example.pgn");
        var games = PGNHandler.parseGames(is);

        var coincidenceCount = games.stream().filter(game -> {
            return game.getEcoDescriptor().getEco().equals(game.getTagValue("ECO").orElse(""));
        }).count();
        System.out.println("coincidentes: " + coincidenceCount);
        assertTrue(((double) coincidenceCount) / ((double) games.size()) > 0.74);
    }

}
