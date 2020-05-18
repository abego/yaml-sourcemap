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

package org.abego.yaml.sourcemap;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YAMLSourceMapAPITest {

    private static final String FRAGMENTS_TSV_HEADER = "startOffset\tstartLine\tstartColumn\tendOffset\tendLine\tendColumn\tkind\tjsonPointer\n";

    static String toTSV(YAMLSourceMap srcMap) {
        StringWriter writer = new StringWriter();
        YAMLSourceMapAPI.writeTSV(srcMap, writer);
        return writer.toString();
    }

    static String toTSV(Iterable<YAMLSourceMap.Fragment> fragments) {
        StringWriter writer = new StringWriter();
        YAMLSourceMapAPI.writeTSV(fragments, writer);
        return writer.toString();
    }

    @Test
    void constructor() {
        YAMLSourceMapException e = assertThrows(YAMLSourceMapException.class,
                YAMLSourceMapAPI::new);
        assertEquals("Must not instantiate", e.getMessage());
    }

    @Test
    void createYAMLSourceMap_String() {
        YAMLSourceMap sourceMap = YAMLSourceMapAPI.createYAMLSourceMap("A");

        assertNotNull(sourceMap);
    }

    @Test
    void createYAMLSourceMap_Reader() {
        InputStream stream = YAMLSourceMapTest.class
                .getResourceAsStream("/org/abego/yaml/sourcemap/sample.yml");
        YAMLSourceMap sourceMap = YAMLSourceMapAPI
                .createYAMLSourceMap(new InputStreamReader(stream));

        assertNotNull(sourceMap);
        String result = toTSV(sourceMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t2\t1\t3\tSEQUENCE\t/0\n" +
                "2\t1\t3\t3\t1\t4\tSEQUENCE_ITEM\t/0\n" +
                "3\t1\t4\t6\t2\t3\tSEQUENCE\t/1\n" +
                "6\t2\t3\t7\t2\t4\tSEQUENCE_ITEM\t/1\n" +
                "7\t2\t4\t8\t3\t1\tSEQUENCE\t\n" +
                "", result);
    }

    @Test
    void writeTSV() {
        YAMLSourceMap sourceMap = YAMLSourceMapAPI.createYAMLSourceMap("A");

        String result = toTSV(sourceMap); // indirect test: toTSV calls writeTSV
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tSCALAR_VALUE\t\n" +
                "", result);
    }

    @Test
    void writeTSV_withEscapes() {
        String yaml = "{\"A\\n\\r\\t\\\\B\":2}";
        // offsets-----01-23-45-67-89-0-12-34567890123456789
        // ------------00-00-00-00-00-1-11-11111112222222222
        YAMLSourceMap sourceMap = YAMLSourceMapAPI.createYAMLSourceMap(yaml);

        String result = toTSV(sourceMap); // indirect test: toTSV calls writeTSV
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tMAP\t\n" +
                "1\t1\t2\t13\t1\t14\tMAP_KEY\t/A\\n\\r\\t\\\\B\n" +
                "13\t1\t14\t14\t1\t15\tMAP\t/A\\n\\r\\t\\\\B\n" +
                "14\t1\t15\t15\t1\t16\tMAP_VALUE\t/A\\n\\r\\t\\\\B\n" +
                "15\t1\t16\t16\t1\t17\tMAP\t\n" +
                "", result);
    }


}