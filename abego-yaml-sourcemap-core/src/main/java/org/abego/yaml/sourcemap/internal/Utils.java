/*
 * MIT License
 *
 * Copyright (c) 2020 Udo Borkowski, (ub@abego.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.abego.yaml.sourcemap.internal;

import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;

import java.io.PrintStream;
import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of utility code used by this module, but not specific to
 * this module's domain.
 *
 * <p>This class is part of the internal implementation package and must not
 * be used by client code directly.</p>
 */
final class Utils {

    private static final Pattern PATTERN_FOR_JSON_POINTER_ESCAPE = Pattern
            .compile("([^/~]+)|(/)|(~)");

    Utils() {
        throw new IllegalStateException("Must not instantiate");
    }

    /**
     * Returns the last item of the (non-empty) {@code list}.
     *
     * @param list a non-empty list
     * @return the last item of the list
     * @throws IllegalArgumentException when the list is empty
     */
    static <T> T last(List<T> list) {
        if (list.isEmpty())
            throw new IllegalArgumentException("List is empty");

        return list.get(list.size() - 1);
    }

    /**
     * Removes the last item from the (non-empty) {@code list}.
     *
     * @param list a non-empty list
     * @return the just removed item
     * @throws IllegalArgumentException when the list is empty
     */
    static <T> T removeLast(List<T> list) {
        if (list.isEmpty())
            throw new IllegalArgumentException("List is empty");

        int lastIndex = list.size() - 1;
        T result = list.get(lastIndex);
        list.remove(lastIndex);
        return result;
    }

    static void dumpYamlParserEvents(
            Reader reader, PrintStream printStream) {
        ParserImpl parser = new ParserImpl(new StreamReader(reader));
        while (!parser.checkEvent(Event.ID.StreamEnd)) {
            Event event = parser.getEvent();
            printStream.printf("%d\t%d\t%s\n%s\n%s%n",
                    event.getStartMark().getIndex(),
                    event.getEndMark().getIndex(),
                    event.getEventId().toString(),
                    event.getStartMark().get_snippet(),
                    event.getEndMark().get_snippet());
        }
    }


    static String escapeForJsonPointerStep(String text) {
        StringBuilder result = new StringBuilder();
        Matcher m = PATTERN_FOR_JSON_POINTER_ESCAPE.matcher(text);
        while (m.find()) {
            if (m.group(1) != null) {
                result.append(m.group(1));
            } else if (m.group(2) != null) {
                result.append("~1");
            } else {
                result.append("~0");
            }
        }
        return result.toString();
    }
}
