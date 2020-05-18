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
 * <strong>Breadcrumbs</strong>
 *
 * <p>The application demonstrates how a
 * {@link org.abego.yaml.sourcemap.YAMLSourceMap} can be used to implement a
 * "Breadcrumbs" bar for JSON/YAML text.</p>
 *
 * <img src="doc-files/breadcrumbs-demo.png" alt="Image of the 'Breadcrumbs Demo' application">
 *
 * <p>The Breadcrumbs bar displays the path to the YAML/JSON element currently
 * at the text cursor position in the YAML/JSON text. <em>Here the
 * YAML Source Map is used for "from text to data (JSON Pointer)" mapping: the
 * position in the YAML text is converted into the corresponding JSON Pointer.
 * The Breadcrumbs bar displays this JSON Pointer "in a user friendly way".</em></p>
 *
 * <p>When clicking a breadcrumb in the Breadcrumbs bar the cursor in the
 * YAML text view moves to the corresponding element. <em>Here the
 * YAML Source Map is used for "from data (JSON Pointer) to Text" mapping: the
 * JSON Pointer (stored in the breadcrumb) is converted into a corresponding
 * position in the YAML text.</em></p>
 */
@NonNullByDefault
package org.abego.yaml.sourcemap.examples.breadcrumbs;

import org.eclipse.jdt.annotation.NonNullByDefault;