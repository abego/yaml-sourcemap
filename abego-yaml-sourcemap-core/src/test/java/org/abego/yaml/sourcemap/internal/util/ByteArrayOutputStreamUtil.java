/*
 * MIT License
 *
 * Copyright (c) 2018 Udo Borkowski, (ub@abego.org)
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

package org.abego.yaml.sourcemap.internal.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.abego.yaml.sourcemap.internal.util.UncheckedException.newUncheckedException;

@SuppressWarnings("WeakerAccess")
public final class ByteArrayOutputStreamUtil {

    private ByteArrayOutputStreamUtil() {
    }

    // --- Queries ---

    public static String textOf(ByteArrayOutputStream outputStream,
                                String charsetName) {
        try {
            return new String(outputStream.toByteArray(), charsetName);
        } catch (UnsupportedEncodingException e) {
            throw newUncheckedException(e);
        }
    }

    public static String textOf(ByteArrayOutputStream outputStream,
                                Charset charset) {
        return textOf(outputStream, charset.name());
    }

    public static String textOf(ByteArrayOutputStream outputStream) {
        return textOf(outputStream, StandardCharsets.UTF_8);
    }

}
