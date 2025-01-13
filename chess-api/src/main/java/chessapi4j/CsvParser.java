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
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

//working correctly
/**
 * Internal class used to parse a CSV file.
 * 
 * @author lunalobos
 * 
 * @since 1.2.7
 */
public class CsvParser {

    private char separator = ',';
    private char stringSeparator = '"';

    public CsvParser() {}

    public CsvParser(char separator, char stringSeparator) {
        this.separator = separator;
        this.stringSeparator = stringSeparator;
    }


    private List<String> parseLine(String line) {
        var stack = line.chars().mapToObj(i -> (Character) (char) i)
                .collect(Collectors.toCollection(ArrayDeque::new));

        List<String> strings = new LinkedList<>();
        var sb = new StringBuilder();
        var previousString = false;
        while (!stack.isEmpty()) {
            var c = stack.pop();
            if (c == separator && !previousString) {
                strings.add(sb.toString());
                
                sb = new StringBuilder();
                previousString = false;
            } else if (c == stringSeparator && sb.isEmpty()) {
                var bucket = new Bucket(stringSeparator);
                previousString = true;
                while (bucket.hasSpace() && !stack.isEmpty()) {
                    var ch = stack.pop();
                    bucket.add(ch);
                }
                strings.add(bucket.content());
                
            } else if(c == separator){
                continue;
            } else {
                sb.append(c);
            }
        }
        if (!sb.isEmpty()) {
            strings.add(sb.toString());
        }
        return new ArrayList<>(strings);
    }

    /**
     * Parses an input stream containing CSV data and returns a list of lists of
     * strings, where each inner list represents a line in the CSV file split by
     * commas.
     *
     * @param inputStream the input stream to be parsed
     * @return a list of lists of strings parsed from the input stream
     * @throws IOException if an I/O error occurs while reading the input stream
     * 
     * @since 1.2.6
     */
    public List<List<String>> parseInputStream(InputStream inputStream) throws IOException {
        var bytes = inputStream.readAllBytes();
        
        var str = new String(bytes, "UTF-8");
        return Arrays.stream(str.split("\n")).map(this::parseLine).collect(Collectors.toCollection(LinkedList::new));
    }

}

class Bucket {

    private char end;
    private StringBuilder sb = new StringBuilder();
    private boolean hasSpace = true;

    public Bucket(char end) {
        this.end = end;
    }

    public boolean hasSpace() {
        return hasSpace;
    }

    public void add(char c) {
        if (c == end) {
            hasSpace = false;
            return;
        }
        sb.append(c);
    }

    public String content() {
        return sb.toString();
    }
}
