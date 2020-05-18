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

import static org.abego.yaml.sourcemap.internal.Utils.escapeForJsonPointerStep;

/**
 * Provides a way to construct JSON pointers while parsing a YAML document.
 *
 * <p>Other than 'classic' builders this class is based on a stack: one can
 * push and pop the steps(/tags) of a JSON pointer according to the currently
 * processed YAML node. E.g when entering a map entry {@link Stack#push(Object)}
 * the entry's key, and {@link Stack#pop()} when leaving the map entry.</p>
 */
final class JSONPointerBuilder extends Stack<String> {

    /**
     * Returns the 'current' JSON Pointer.
     */
    public String createJSONPointer() {
        StringBuilder text = new StringBuilder();
        for (String step : itemsFromBottomToTop()) {
            text.append("/");
            text.append(escapeForJsonPointerStep(step));
        }
        return text.toString();
    }

    /**
     * Returns the 'current' JSON Pointer.
     */
    public String toString() {
        return createJSONPointer();
    }
}
