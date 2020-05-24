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


/**
 * A Source Map for YAML and JSON Documents.
 * <p>
 * See the package JavaDoc for more details.
 */
public interface YAMLSourceMap extends FragmentsAPI {

    /**
     * Returns the JSON Pointer for the given offset.
     *
     * @param offset an offset for the start of the YAML document
     * @return the JSON Pointer for the given offset
     */
    String jsonPointerAtOffset(int offset);

    /**
     * Returns the JSON Pointer for the given location.
     *
     * @param line   the number of a line in the YAML document, with 1 for the
     *               first line
     * @param column the number of a column in the given line,
     *               with 1 for the first column
     * @return the JSON Pointer for the given location
     * @throws YAMLSourceMapException when the location is invalid
     */
    String jsonPointerAtLocation(int line, int column);

    /**
     * Returns the range in the YAML/JSON document's text that is
     * related to the data value identified by the {@code jsonPointer}.
     *
     * <p>The range is empty when the {@code jsonPointer} does not identify data
     * created by the YAML/JSON document.</p>
     *
     * <p>Other than {@link #sourceRangeOfValueOfJsonPointer(String)} this also
     * includes parts of the YAML/JSON text that does not define the
     * <em>value</em> for the data. This includes surrounding whitespaces or
     * comments, but also syntactic elements like ":", "[", or "-". For a map
     * entry it also includes the map key.</p>
     *
     * @param jsonPointer A JSON Pointer for a data value created by this source
     *                    map's YAML/JSON document
     * @return the range in the YAML/JSON document's text that is
     * related to the data value identified by the {@code jsonPointer}
     */
    YAMLRange sourceRangeOfJsonPointer(String jsonPointer);

    /**
     * Returns the range in the YAML/JSON document's text that is the source
     * for the data value identified by the {@code jsonPointer}.
     *
     * <p>The range is empty when the {@code jsonPointer} does not identify data
     * created by the YAML/JSON document.</p>
     *
     * <p>Other than {@link #sourceRangeOfJsonPointer(String)} this range focuses on
     * the part of the YAML/JSON text that defines the data's <em>value</em>.
     * The range does not include parts unrelated to the data's value, like
     * surrounding whitespaces or comments.</p>
     *
     * @param jsonPointer A JSON Pointer for a data value created by this source
     *                    map's YAML/JSON document
     * @return the range in the YAML/JSON document's text that is the source
     * for the data value identified by the {@code jsonPointer}
     */
    YAMLRange sourceRangeOfValueOfJsonPointer(String jsonPointer);

}
