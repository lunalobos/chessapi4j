/*
 * Copyright 2024 Miguel Angel Luna Lobos
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

class RulesTest {

	@Test
	void test() {
		try {
			boolean isLegal = Rules.legal(new Position(), MoveFactory.instance("e2e4", false));
			assertEquals(true, isLegal);
		} catch (MovementException e) {
			e.printStackTrace();
		}
	}

}