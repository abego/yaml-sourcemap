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


import org.eclipse.jdt.annotation.Nullable;

/**
 * An {@link UncheckedException} mainly serves as a wrapper when rethrowing
 * a checked exception.
 */
@SuppressWarnings("serial")
public final class UncheckedException extends RuntimeException {

    private UncheckedException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public static UncheckedException newUncheckedException(Throwable cause) {
        return new UncheckedException(cause.getMessage(), cause);
    }

}
