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

/**
 * Java library to map between YAML/JSON document texts and data values in
 * Java, in both directions.
 *
 * <p><strong>Overview </strong></p>
 *
 * <p>The {@link org.abego.yaml.sourcemap.YAMLSourceMap} provides a mapping
 * between locations in a YAML document (the source) and the data values
 * created from this document.</p>
 *
 * <p><img src="doc-files/mapping.png"
 * alt="Mapping between YAML document text and Data (JSON pointer)"
 * style="width=40em"></p>
 *
 * <p>The mapping is in both directions:</p>
 * <ul>
 *     <li><b>YAML document text -&gt; Data (JSON pointer)</b>:
 * <p>
 *     If you have a location in the YAML document, the source map tells you
 *     the address (JSON pointer) of the data value this location relates to.</li>
 *
 *     <li><b>Data (JSON pointer) -&gt; YAML document text</b>:
 * <p>
 *     If you have a JSON pointer for some data created from the YAML document
 *     the source tells you the locations in the YAML document that created the
 *     data.</li>
 * </ul>
 *
 * <p><strong>JSON and YAML</strong></p>
 *
 * <p>As YAML is a superset of JSON the YAMLSourceMap can also used to
 * create source maps for JSON documents.</p>
 *
 * <strong>Usage</strong>
 *
 * <p><em><strong>Creating a YAMLSourceMap</strong></em></p>
 *
 * <p>The central interface of this library is the {@link org.abego.yaml.sourcemap.YAMLSourceMap}.</p>
 * <p>
 * You create a YAMLSourceMap for a specific YAML document
 * using the {@link org.abego.yaml.sourcemap.YAMLSourceMapAPI}:</p>
 * <pre>
 *    // Create a YAMLSourceMap from a YAML/JSON document given by a {@link java.io.Reader}
 *    Reader reader = ...;
 *    YAMLSourceMap srcMap = YAMLSourceMapAPI.createYAMLSourceMap(reader);
 * </pre>
 * or
 * <pre>
 *    // Create a YAMLSourceMap from a YAML/JSON document {@link java.lang.String}
 *    String yamlText = "foo: 123\nbar: 456\n";
 *    YAMLSourceMap srcMap = YAMLSourceMapAPI.createYAMLSourceMap(yamlText);
 * </pre>
 *
 * <p><em><strong>Find the data for a YAML/JSON document text location</strong></em></p>
 *
 * <p>Once you have the YAMLSourceMap you can pass in a location in the YAML
 * document text and the source map will give you the address of the data (value)
 * the text at the given location in the YAML document created. The data address
 * is given as a JSON Pointer [1], a standard format to identify a specific
 * value in a JSON document.</p>
 * <pre>
 *    YAMLSourceMap srcMap =...;
 *
 *    // get the JSON Pointer for the YAML text at a given offset (here offset 42)
 *    String jsonPointer = srcMap.jsonPointerAtOffset(42);
 *    // returns something like "/invoice/bill-to/city"
 * </pre>
 * or
 * <pre>
 *    YAMLSourceMap srcMap =...;
 *
 *    // get the JSON Pointer for the YAML text at a location given by
 *    // line and column (here the column 14 of of the third line)
 *    String jsonPointer = srcMap.jsonPointerAtLocation(3, 14);
 *    // returns something like "/invoice/bill-to/city"
 * </pre>
 *
 * <p>This is how you get the mapping from YAML document text -&gt; Data
 * (JSON pointer).</p>
 *
 * <p><em><strong>Find the YAML/JSON document text that created a data value</strong></em></p>
 *
 * <p> For the other direction (Data (JSON pointer) -&gt; YAML document text)
 * you use {@link org.abego.yaml.sourcemap.YAMLSourceMap#sourceRangeOfValueOfJsonPointer(java.lang.String)},
 * pass in a JSON Pointer and get the range in the text of YAML/JSON document
 * that created the data value identified by the JSON Pointer:</p>
 * <pre>
 *    YAMLSourceMap srcMap =...;
 *
 *    String jsonPointer = "/invoice/bill-to/city";
 *    YAMLRange range = srcMap.sourceRangeOfValueOfJsonPointer(jsonPointer)
 * </pre>
 *
 * <p> If you interested not just in the text range that created the data
 * <em>value</em> but would like to know the larger range in the YAML text
 * related to the data value (including surrounding whitespace or comments,
 * or special characters like ":", "[" etc.) you can use the method
 * {@link org.abego.yaml.sourcemap.YAMLSourceMap#sourceRangeOfJsonPointer(java.lang.String)} instead:</p>
 * <pre>
 *    YAMLSourceMap srcMap =...;
 *
 *    String jsonPointer = "/invoice/bill-to/city";
 *    YAMLRange range = srcMap.sourceRangeOfJsonPointer(jsonPointer)
 * </pre>
 *
 * <p>[1]: <a href="https://tools.ietf.org/html/rfc6901"
 * >https://tools.ietf.org/html/rfc6901</a></p>
 */
@NonNullByDefault
package org.abego.yaml.sourcemap;

import org.eclipse.jdt.annotation.NonNullByDefault;