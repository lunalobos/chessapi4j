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

public class EcoTest {
    @Test
    void get(){
        var eco = Game.eco;

        eco.get("d4").ifPresentOrElse(value -> assertEquals("A40", value.getEco()), () -> fail());
        eco.get("e4").ifPresentOrElse(value -> assertEquals("B00", value.getEco()), () -> fail());
        eco.get("a3").ifPresentOrElse(value -> assertEquals("A00", value.getEco()), () -> fail());
        eco.get("b3").ifPresentOrElse(value -> assertEquals("A01", value.getEco()), () -> fail());
        eco.get("c4").ifPresentOrElse(value -> assertEquals("A10", value.getEco()), () -> fail());
        eco.get("d4 d5 c4 e6 Nc3 Nf6 Nf3 c6 e3").ifPresentOrElse(value -> assertEquals("D45", value.getEco()), () -> fail());
        eco.get("d4 Nf6 c4 e6 Nf3 Ne4").ifPresentOrElse(value -> assertEquals("E10", value.getEco()), () -> fail());
    }
}
