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

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Objects;

/**
 * Generic tuple for two values
 * @param <T1> The type of the first value
 * @param <T2> The type of the second value
 * @author lunalobos
 * @since 1.2.9
 */
@EqualsAndHashCode
@ToString
public final class Tuple <T1,T2> {
    private final T1 v1;
    private final T2 v2;

    /**
     * Constructs a new tuple
     * @param v1 the first value
     * @param v2 the second value
     */
    public Tuple(T1 v1, T2 v2) {
        this.v1 = Objects.requireNonNull(v1, "v1 cannot be null");
        this.v2 = Objects.requireNonNull(v2, "v2 cannot be null");
    }
    
    /**
     * Returns the first value
     * @return The first value
     */
    public T1 getV1() {
        return this.v1;
    }

    /**
     * Returns the second value
     * @return The second value
     */
    public T2 getV2() {
        return this.v2;
    }
}
