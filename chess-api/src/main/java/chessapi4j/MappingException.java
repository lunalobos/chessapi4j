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
 * Exception for mapping exceptions.
 * @author lunalobos
 * @since 1.2.8
 */
final class MappingException extends RuntimeException {

    public static MappingException index(int index, int capacity) {
        return new MappingException(String.format(
                        "Index %d is out of range for this hashmap with capacity %d.",
                        index,
                        capacity));
    }

    private MappingException(String message) {
        super(message);
    }
}

