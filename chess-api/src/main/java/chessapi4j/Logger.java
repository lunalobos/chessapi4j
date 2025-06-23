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
 * @author lunalobos
 * @since 1.2.8
 */
interface Logger {
    void instantiation();
    void trace(String message);
    void trace(String message, Object... args);
    void debug(String message);
    void debug(String message, Object... args);
    void info(String message);
    void info(String message, Object... args);
    void warn(String message);
    void warn(String message, Object... args);
    void error(String message);
    void error(String message, Object... args);
    void fatal(String message);
    void fatal(String message, Object... args);
    void disable();
    void setFilterLevel(String filterLevel);
}
