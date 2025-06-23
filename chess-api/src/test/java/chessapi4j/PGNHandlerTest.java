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

import java.util.List;

import org.junit.jupiter.api.Test;

class PGNHandlerTest {

	@Test
	void testParseGame() {
		List<Game> games = PGNHandler.parseGames(this.getClass().getClassLoader().getResourceAsStream("example.pgn"));
		assertFalse(games.isEmpty());
		// some assertions
		assertEquals("Bc4", games.get(0).getMoves().get(6).toString());
		assertEquals("Ke3", games.get(1).getMoves().get(72).toString());
		assertEquals("Rh6", games.get(2).getMoves().get(81).toString());
		assertEquals("d4", games.get(3).getMoves().get(0).toString());
	}

}
