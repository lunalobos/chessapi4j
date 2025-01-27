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

//bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
class FastFailLongMap {
    private Long[] map;

    public FastFailLongMap(int capacity) {
        map = new Long[capacity];
    }

    public void put(int index, long value) throws MappingException {
        if (index < 0 || index >= map.length)
            throw MappingException.index(index, map.length);
        map[index] = value;
    }

    public Long[] toLongArray() {
        var result = new Long[map.length];
        System.arraycopy(map, 0, result, 0, map.length);
        return result;
    }

    public Long get(int index) throws MappingException {
        if (index < 0 || index >= map.length)
            return null;
        else
            return map[index];
    }

    public boolean containsKey(int index) throws MappingException {
        if (index < 0 || index >= map.length)
            return false;
        else
            return map[index] != null;
    }

    public void clear() {
        for (int i = 0; i < map.length; i++) {
            map[i] = null;
        }
    }
}
