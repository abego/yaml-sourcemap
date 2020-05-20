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

import org.abego.yaml.sourcemap.YAMLRange;
import org.abego.yaml.sourcemap.YAMLSourceMap;
import org.abego.yaml.sourcemap.YAMLSourceMapException;
import org.eclipse.jdt.annotation.Nullable;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static org.abego.yaml.sourcemap.internal.Utils.last;

/**
 * The default implementation of {@link YAMLSourceMap}.
 *
 * <p>This class is part of the internal implementation package and must not
 * be used by client code directly.
 * Use {@link org.abego.yaml.sourcemap.YAMLSourceMapAPI} and
 * {@link YAMLSourceMap} instead.</p>
 */
public final class YAMLSourceMapDefault implements YAMLSourceMap {

    /**
     * The list of the fragments of this source map.
     */
    private final List<? extends Fragment> fragments;

    /**
     * Creates a YAMLSourceMapDefault of the YAML document read
     * from the {@code yamlTextReader}.
     */
    private YAMLSourceMapDefault(Reader yamlTextReader) {
        fragments = FragmentsProvider.readFragments(yamlTextReader);
    }

    /**
     * Returns true when the given fragment is a 'value' fragment;
     * returns false otherwise.
     *
     * <p>For detail regarding 'value' fragments see chapter 'Fragments' in
     * the documentation of {@link YAMLSourceMap}.</p>
     */
    private static boolean isValueFragment(Fragment fragment) {
        switch (fragment.getKind()) {
            case ALIAS_AS_MAP_VALUE:
            case ALIAS_AS_SEQUENCE_ITEM:
            case SCALAR_VALUE:
            case SEQUENCE_ITEM:
            case MAP_VALUE:
                return true;
            default:
                return false;
        }
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
    public static YAMLSourceMap of(Reader reader) {
        try {
            return new YAMLSourceMapDefault(reader);
        } catch (Exception e) {
            throw new YAMLSourceMapException(e);
        }
    }

    /**
     * Creates a {@link YAMLSourceMap} of the YAML document defined by the
     * {@code yamlText}.
     *
     * @param yamlText the text of the YAML document to create a
     *                 {@link YAMLSourceMap} for
     * @return the {@link YAMLSourceMap} for the YAML document in yamlText
     */
    public static YAMLSourceMap of(String yamlText) {
        return of(new StringReader(yamlText));
    }

    /**
     * Returns the range covered by the fragments, with the start stored in
     * item 0 and the end in item 1.
     */
    private static YAMLRange createRange(Iterable<Fragment> fragments) {
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        for (Fragment f : fragments) {
            start = min(start, f.getStartOffset());
            end = max(end, f.getEndOffset());
        }
        if (start >= end) {
            start = end = 0;
        }
        return YAMLRangeDefault.createYAMLRangeDefault(start, end);
    }

    @Override
    public int documentLength() {
        return fragments.isEmpty() ? 0 : last(fragments).getEndOffset();
    }

    @Override
    public String jsonPointerAtOffset(int offset) {
        return fragmentAtOffset(offset).getJSONPointer();
    }

    @Override
    public String jsonPointerAtLocation(int line, int column) {
        return fragmentAtLocation(line, column).getJSONPointer();
    }

    @Override
    public YAMLRange sourceRangeOfValueOfJsonPointer(String jsonPointer) {

        // First try for "scalar" data values
        @Nullable
        Fragment fragment = valueFragmentOfJsonPointer(jsonPointer);
        if (fragment != null) {
            return YAMLRangeDefault.createYAMLRangeDefault(
                    fragment.getStartOffset(), fragment.getEndOffset());
        }

        // For non-scalar data values we return the "inner" text range
        return createRange(allFragmentsOfChildrenOfJsonPointer(jsonPointer));
    }

    @Override
    public YAMLRange sourceRangeOfJsonPointer(String jsonPointer) {
        return createRange(allFragmentsOfJsonPointer(jsonPointer));
    }

    @Override
    public List<Fragment> allFragments() {
        return new ArrayList<>(fragments);
    }

    @Override
    public List<Fragment> allFragmentsMatching(Predicate<Fragment> test) {
        List<Fragment> result = new ArrayList<>();
        allFragments().forEach(f -> {
            if (test.test(f)) {
                result.add(f);
            }
        });
        return result;
    }

    @Override
    @Nullable
    public Fragment findFirstFragmentMatching(
            Predicate<Fragment> test) {

        for (Fragment f : allFragments()) {
            if (test.test(f))
                return f;
        }
        return null;
    }

    @Override
    public Fragment fragmentAtOffset(int offset) {
        int adjustedOffset = min(max(0, offset), documentLength() - 1);

        @Nullable
        Fragment result = findFirstFragmentMatching(
                f -> f.containsOffset(adjustedOffset));

        if (result != null)
            return result;

        throw new YAMLSourceMapException(
                String.format("offset out of range. Expected 0..%d, got %d",
                        documentLength() - 1,
                        offset));
    }

    @Override
    public Fragment fragmentAtLocation(int line, int column) {
        if (line < 1) {
            throw new YAMLSourceMapException("line must be >= 1");
        }
        if (column < 1) {
            throw new YAMLSourceMapException("column must be >= 1");
        }

        @Nullable
        Fragment result = findFirstFragmentMatching(
                f -> f.containsLocation(line, column));

        if (result != null)
            return result;

        throw new YAMLSourceMapException(
                String.format("Invalid location. Got line=%d, column=%d",
                        line, column));
    }

    @Override
    public List<Fragment> allFragmentsOfJsonPointer(
            String jsonPointer) {
        return allFragmentsMatching(
                f -> f.getJSONPointer().equals(jsonPointer));
    }

    @Override
    public List<Fragment> allFragmentsOfChildrenOfJsonPointer(String jsonPointer) {
        return allFragmentsMatching(f ->
                f.getJSONPointer().startsWith(jsonPointer) &&
                        !f.getJSONPointer().equals(jsonPointer));
    }

    @Override
    @Nullable
    public Fragment valueFragmentOfJsonPointer(String jsonPointer) {
        return findFirstFragmentMatching(
                f -> f.getJSONPointer().equals(jsonPointer) &&
                        isValueFragment(f));
    }

}
