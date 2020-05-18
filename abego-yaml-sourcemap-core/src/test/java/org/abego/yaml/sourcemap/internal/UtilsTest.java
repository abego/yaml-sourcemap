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

import org.abego.yaml.sourcemap.internal.util.PrintStreamToBuffer;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.abego.yaml.sourcemap.internal.util.PrintStreamToBuffer.newPrintStreamToBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilsTest {

    @Test
    void constructor() {
        Exception e = assertThrows(Exception.class, Utils::new);
        assertEquals("Must not instantiate", e.getMessage());
    }

    @Test
    void last_emptyList() {
        List<String> list = new ArrayList<>();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Utils.last(list));
        assertEquals("List is empty", e.getMessage());

    }

    @Test
    void last() {
        List<String> list = new ArrayList<>();

        list.add("foo");
        list.add("bar");
        assertEquals("bar", Utils.last(list));
    }

    @Test
    void removeLast_emptyList() {
        List<String> list = new ArrayList<>();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> Utils.removeLast(list));
        assertEquals("List is empty", e.getMessage());
    }

    @Test
    void removeLast() {
        List<String> list = new ArrayList<>();

        list.add("foo");
        list.add("bar");
        assertEquals("bar", Utils.removeLast(list));
        assertEquals(1, list.size());
    }

    @Test
    void dumpYamlParserEvents() {
        StringReader reader = new StringReader("[A,B]");
        PrintStreamToBuffer output = newPrintStreamToBuffer();

        Utils.dumpYamlParserEvents(reader, output);

        assertEquals("" +
                "0\t0\tStreamStart\n" +
                "    \n" +
                "    ^\n" +
                "    \n" +
                "    ^\n" +
                "0\t0\tDocumentStart\n" +
                "    [A,B]\n" +
                "    ^\n" +
                "    [A,B]\n" +
                "    ^\n" +
                "0\t1\tSequenceStart\n" +
                "    [A,B]\n" +
                "    ^\n" +
                "    [A,B]\n" +
                "     ^\n" +
                "1\t2\tScalar\n" +
                "    [A,B]\n" +
                "     ^\n" +
                "    [A,B]\n" +
                "      ^\n" +
                "3\t4\tScalar\n" +
                "    [A,B]\n" +
                "       ^\n" +
                "    [A,B]\n" +
                "        ^\n" +
                "4\t5\tSequenceEnd\n" +
                "    [A,B]\n" +
                "        ^\n" +
                "    [A,B]\n" +
                "         ^\n" +
                "5\t5\tDocumentEnd\n" +
                "    [A,B]\n" +
                "         ^\n" +
                "    [A,B]\n" +
                "         ^\n" +
                "", output.text());
    }
}