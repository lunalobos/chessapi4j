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

/**
 * This exception is thrown when a game produce no result in the ECO code
 * search.
 * 
 * @author lunalobos
 * @since 1.2.7
 */
public class MissingECOException extends RuntimeException {

    /**
     * Constructs a new MissingECOException for the given game.
     * @param game the game that produce the exception
     */ 
    public MissingECOException(Game game) {
        super(String.format("Missing ECO code for game: \n%s",game));
    }

    /**
     * Constructs a new MissingECOException for the given game.
     * @param game the game that produce the exception
     */
    public MissingECOException(chessapi4j.functional.Game game) {
        super(String.format("Missing ECO code for game: \n%s",game));
    }
}
