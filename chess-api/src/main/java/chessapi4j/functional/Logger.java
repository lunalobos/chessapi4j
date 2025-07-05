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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
/**
 * @author lunalobos
 * @since 1.2.9
 */
final class Logger {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Executor executor = Executors.newFixedThreadPool(3);
    private final String className;
    private Level filterLevel;

    public Logger(Class<?> clazz) {
        this.className = clazz.getCanonicalName();
    }

    public void instantiation() {
        debug("%s created", className);
    }

    public void instantiation(OffsetDateTime t1, OffsetDateTime t2) {
        var ms = t2.toInstant().toEpochMilli() - t1.toInstant().toEpochMilli();
        debug("%s created in %d ms", className, ms);
    }

    public void trace(String msg) {
        executor.execute(() -> {
            if (check(Level.TRACE)) {
                sendMsg(msg, Level.TRACE);
            }
        });
    }

    public void trace(String format, Object... args) {
        trace(String.format(format,args));
    }

    public void debug(String msg) {
        executor.execute(() -> {
            if (check(Level.DEBUG)) {
                sendMsg(msg, Level.DEBUG);
            }
        });
    }

    public void debug(String format, Object... args) {
        debug(String.format(format,args));
    }

    public void info(String msg) {
        executor.execute(() -> {
            if (check(Level.INFO)) {
                sendMsg(msg, Level.DEBUG);
            }
        });
    }

    public void info(String format, Object... args) {
        info(String.format(format,args));
    }

    public void warn(String msg) {
        executor.execute(() -> {
            if (check(Level.WARN)) {
                sendMsg(msg, Level.DEBUG);
            }
        });
    }

    public void warn(String format, Object... args) {
        warn(String.format(format,args));
    }

    public void error(String msg) {
        executor.execute(() -> {
            if (check(Level.ERROR)) {
                sendMsg(msg, Level.DEBUG);
            }
        });
    }

    public void error(String format, Object... args) {
        error(String.format(format,args));
    }

    public Throwable error(Throwable throwable){
        error(String.format("Error: msg: %s, class: %s, cause: %s", throwable.getMessage(),
                throwable.getClass().getName(), throwable.getCause()));
        return throwable;
    }

    public void fatal(String msg) {
        executor.execute(() -> {
            if (check(Level.FATAL)) {
                sendMsg(msg, Level.DEBUG);
            }
        });
    }

    public void fatal(String format, Object... args) {
        fatal(String.format(format,args));
    }

    public Throwable fatal(Throwable throwable){
        fatal(String.format("Error: msg: %s, class: %s, cause: %s", throwable.getMessage(),
                throwable.getClass().getName(), throwable.getCause()));
        return throwable;
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
        System.out.println(msg.trim());
    }

    private String format(String msg, Level level) {
        return String.format("[%s] %s - %s: %s",level.name(), DATE_FORMATTER.format(OffsetDateTime.now()), className, msg);
    }

    public void setFilterLevel(String filterLevel) {
        this.filterLevel = Level.valueOf(filterLevel);
    }
}
/**
 * @author lunalobos
 * @since 1.2.9
 */
enum Level {
    TRACE, DEBUG, INFO, WARN, ERROR, FATAL, NONE
}
