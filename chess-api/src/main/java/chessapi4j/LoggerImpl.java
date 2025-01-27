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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

//bean
/**
 * @author lunalobos
 * @since 1.2.8
 */
class LoggerImpl implements Logger {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private String className;
    private Level filterLevel;

    public LoggerImpl(Class<?> clazz) {
        this.className = clazz.getSimpleName();

    }

    @Override
    public void instanciation() {
        debug("%s created", className);
    }

    @Override
    public void trace(String msg) {
        if (check(Level.TRACE)) {
            sendMsg(msg, Level.TRACE);
        }
    }

    @Override
    public void trace(String format, Object... args) {
        if (check(Level.TRACE)) {
            sendMsg(format.formatted(args), Level.TRACE);
        }
    }

    @Override
    public void debug(String msg) {
        if (check(Level.DEBUG)) {
            sendMsg(msg, Level.DEBUG);
        }
    }

    @Override
    public void debug(String format, Object... args) {
        if (check(Level.DEBUG)) {
            sendMsg(format.formatted(args), Level.DEBUG);
        }
    }

    @Override
    public void info(String msg) {
        if (check(Level.INFO)) {
            sendMsg(msg, Level.INFO);
        }
    }

    @Override
    public void info(String format, Object... args) {
        info(format.formatted(args));
    }

    @Override
    public void warn(String msg) {
        if (check(Level.WARN)) {
            sendMsg(msg, Level.WARN);
        }
    }

    @Override
    public void warn(String format, Object... args) {
        warn(format.formatted(args));
    }

    @Override
    public void error(String msg) {
        if (check(Level.ERROR)) {
            sendMsg(msg, Level.ERROR);
        }
    }

    @Override
    public void error(String format, Object... args) {
        error(format.formatted(args));
    }

    @Override
    public void fatal(String msg) {
        if (check(Level.FATAL)) {
            sendMsg(msg, Level.FATAL);
        }
    }

    @Override
    public void fatal(String format, Object... args) {
        fatal(format.formatted(args));
    }
    
    public void disable() {
        filterLevel = Level.NONE;
    }

    private boolean check(Level level) {
        return level.ordinal() >= filterLevel.ordinal();
    }

    private void sendMsg(String msg, Level level) {
        append(format(msg, level));
    }

    private void append(String msg) {
        System.out.println(msg);
    }

    private String format(String msg, Level level) {
        return "[%s] %s - %s: %s".formatted(level.name(), DATE_FORMATTER.format(OffsetDateTime.now()), className, msg);
    }

    public void setFilterLevel(String filterLevel) {
        this.filterLevel = Level.valueOf(filterLevel);
    }


}

enum Level {
    TRACE, DEBUG, INFO, WARN, ERROR, FATAL, NONE
}

