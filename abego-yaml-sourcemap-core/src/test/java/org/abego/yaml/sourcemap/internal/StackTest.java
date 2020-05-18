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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StackTest {

    @Test
    void smokeTest() {
        Stack<String> stack = new Stack<>();

        assertTrue(stack.isEmpty());

        stack.push("A");
        assertFalse(stack.isEmpty());
        assertEquals("A", stack.top());
        assertEquals(1, stack.itemsFromBottomToTop().size());
        assertEquals("A", stack.itemsFromBottomToTop().get(0));

        stack.push("B");
        assertFalse(stack.isEmpty());
        assertEquals("B", stack.top());
        assertEquals(2, stack.itemsFromBottomToTop().size());
        assertEquals("A", stack.itemsFromBottomToTop().get(0));
        assertEquals("B", stack.itemsFromBottomToTop().get(1));

        String s = stack.pop();
        assertEquals("B", s);
        assertFalse(stack.isEmpty());
        assertEquals("A", stack.top());
        assertEquals(1, stack.itemsFromBottomToTop().size());
        assertEquals("A", stack.itemsFromBottomToTop().get(0));
    }
}