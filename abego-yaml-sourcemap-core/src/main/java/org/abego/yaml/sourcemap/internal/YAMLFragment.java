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

import org.abego.yaml.sourcemap.FragmentsAPI.Fragment;
import org.yaml.snakeyaml.error.Mark;

/**
 * An implementation of {@link Fragment}, using SnakeYaml's
 * {@link Mark} object to track the start and end of the fragment.
 */
final class YAMLFragment implements Fragment {
    private final Kind kind;
    private final Mark startMark;
    private Mark endMark;
    private String jsonPointer;

    YAMLFragment(Mark startMark, Mark endMark,
                 Kind kind,
                 String jsonPointer) {
        this.startMark = startMark;
        this.endMark = endMark;
        this.kind = kind;
        this.jsonPointer = jsonPointer;
    }

    @Override
    public int getStartOffset() {
        return startMark.getIndex();
    }

    @Override
    public int getStartLine() {
        return startMark.getLine() + 1;
    }

    @Override
    public int getStartColumn() {
        return startMark.getColumn() + 1;
    }

    @Override
    public int getEndOffset() {
        return endMark.getIndex();
    }

    @Override
    public int getEndLine() {
        return endMark.getLine() + 1;
    }

    @Override
    public int getEndColumn() {
        return endMark.getColumn() + 1;
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public String getJSONPointer() {
        return jsonPointer;
    }

    public void setJSONPointer(String jsonPointer) {
        this.jsonPointer = jsonPointer;
    }

    public Mark getEndMark() {
        return endMark;
    }

    public void setEndMark(Mark endMark) {
        this.endMark = endMark;
    }

    @Override
    public String toString() {
        return "YAMLFragment{" +
                "startOffset=" + getStartOffset() +
                ", startLine=" + getStartLine() +
                ", startColumn=" + getStartColumn() +
                ", endOffset=" + getEndOffset() +
                ", endLine=" + getEndLine() +
                ", endColumn=" + getEndColumn() +
                ", kind=" + kind +
                ", jsonPointer=" + jsonPointer +
                '}';
    }

}
