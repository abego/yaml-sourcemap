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

import org.abego.yaml.sourcemap.YAMLSourceMap.Fragment;
import org.abego.yaml.sourcemap.internal.YAMLSourceMapDefault;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The entry class to the abego YAML SourceMap API.
 *
 * <p>Clients of the abego YAML SourceMap module must use this class
 * to get access to the module's features.</p>
 */
public final class YAMLSourceMapAPI {

    private static final Pattern PATTERN_FOR_TSV_ESCAPE = Pattern
            .compile("([^\\n\\r\\t\\\\]+)|(\\n)|(\\r)|(\\t)|(\\\\)");

    YAMLSourceMapAPI() {
        throw new YAMLSourceMapException("Must not instantiate");
    }

    /**
     * Creates a {@link YAMLSourceMap} of the YAML document read from the
     * {@code reader}.
     *
     * @param reader the Reader to read the YAML document to create a
     *               {@link YAMLSourceMap} for
     * @return the {@link YAMLSourceMap} for the YAML document read from the
     * reader
     */
    public static YAMLSourceMap createYAMLSourceMap(Reader reader) {
        return YAMLSourceMapDefault.of(reader);
    }

    /**
     * Creates a {@link YAMLSourceMap} of the YAML document defined by the
     * {@code yamlText}.
     *
     * @param yamlText the text of the YAML document to create a
     *                 {@link YAMLSourceMap} for
     * @return the {@link YAMLSourceMap} for the YAML document in yamlText
     */
    public static YAMLSourceMap createYAMLSourceMap(String yamlText) {
        return YAMLSourceMapDefault.of(yamlText);
    }

    /**
     * Writes the fragments of the {@code yamlSourceMap} to the {@code output},
     * as tab separated values (TSV).
     *
     * <p>The output also includes a header line.</p>
     *
     * @param yamlSourceMap the {@link YAMLSourceMap} to write to the output
     * @param output        the Writer to write the output to
     */
    public static void writeTSV(YAMLSourceMap yamlSourceMap, Writer output) {
        writeTSV(yamlSourceMap.allFragments(), output);
    }

    /**
     * Writes the {@code fragments} to the {@code output},
     * as tab separated values (TSV).
     *
     * <p>The output also includes a header line.</p>
     *
     * @param fragments the {@link Fragment}s to write to the output
     * @param output    the Writer to write the output to
     */
    public static void writeTSV(Iterable<Fragment> fragments, Writer output) {
        try (PrintWriter writer = new PrintWriter(output)) {
            writer.println("" +
                    "startOffset\tstartLine\tstartColumn\t" +
                    "endOffset\tendLine\tendColumn\t" +
                    "kind\tjsonPointer");
            for (Fragment f : fragments) {
                writer.print(f.getStartOffset());
                writer.print('\t');
                writer.print(f.getStartLine());
                writer.print('\t');
                writer.print(f.getStartColumn());
                writer.print('\t');
                writer.print(f.getEndOffset());
                writer.print('\t');
                writer.print(f.getEndLine());
                writer.print('\t');
                writer.print(f.getEndColumn());
                writer.print('\t');
                writer.print(f.getKind());
                writer.print('\t');
                writer.print(escapeForTSV(f.getJSONPointer()));
                writer.println();
            }
        }
    }

    /**
     * Returns the text with all necessary characters escaped so the result can
     * be used as a value in a tab-separated value (TSV) text.
     *
     * @param text the text to escape
     * @return the escaped text if the text must be escaped; text otherwise.
     */
    private static String escapeForTSV(String text) {
        StringBuilder result = new StringBuilder();
        Matcher m = PATTERN_FOR_TSV_ESCAPE.matcher(text);
        while (m.find()) {
            if (m.group(1) != null) {
                result.append(m.group(1));
            } else if (m.group(2) != null) {
                result.append("\\n");
            } else if (m.group(3) != null) {
                result.append("\\r");
            } else if (m.group(4) != null) {
                result.append("\\t");
            } else {
                result.append("\\\\");
            }
        }
        return result.toString();
    }
}
