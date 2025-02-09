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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Random;
import java.util.stream.Collectors;

//singleton bean
/**
 * Provides 837 longs from zobrist.txt
 * @since 1.2.9
 * @author lunalobos
 */
class LongProvider {
    private static final Logger logger = LoggerFactory.getLogger(LongProvider.class);
    private Long[] backedArray;
    private int pointer;
    //private Random random;

    public LongProvider(){//Random random) {
        pointer = 0;
        try (var is = this.getClass().getClassLoader().getResourceAsStream("zobrist.txt")) {
            backedArray = Arrays.stream(new String(is.readAllBytes(), "UTF-8").split("\n"))
                    .map(row -> Long.parseUnsignedLong(row.trim(), 16))
                    .collect(Collectors.toCollection(() -> new ArrayList<>(837)))
                    .toArray(new Long[837]);
        } catch (IOException | NumberFormatException e) {
            var fatalException = new ResourceAccessException("zobrist.txt", e);
            logger.fatal(fatalException.getMessage());
            throw fatalException;
        }
        logger.instanciation();
        //this.random = random;
    }

    public long nextLong() {
        if (pointer == backedArray.length) {
            logger.fatal("No more longs");
            throw new RuntimeException("No more longs");
        }
        return backedArray[pointer++];
        //return random.nextLong();
    }
}
