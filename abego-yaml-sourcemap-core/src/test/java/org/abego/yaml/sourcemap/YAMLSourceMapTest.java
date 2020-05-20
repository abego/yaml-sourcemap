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
import org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.DOCUMENT_END;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.DOCUMENT_START;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.SCALAR_VALUE;
import static org.abego.yaml.sourcemap.YAMLSourceMapAPI.createYAMLSourceMap;
import static org.abego.yaml.sourcemap.YAMLSourceMapAPITest.toTSV;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class YAMLSourceMapTest {

    private static final String FRAGMENTS_TSV_HEADER = "startOffset\tstartLine\tstartColumn\tendOffset\tendLine\tendColumn\tkind\tjsonPointer\n";

    /**
     * A YAML example based on the "Example 2.27. Invoice" of the Yaml 1.2 Spec
     *
     * <p>https://yaml.org/spec/1.2/spec.html</p>
     */
    private static String example_2_27_Invoice_yaml() {
        return "" +
                "---\n" +
                "invoice: 34843\n" +
                "date   : 2001-01-23\n" +
                "bill-to: &id001\n" +
                "    given  : Chris\n" +
                "    family : Dumars\n" +
                "    address:\n" +
                "        lines: |\n" +
                "            458 Walkman Dr.\n" +
                "            Suite #292\n" +
                "        city    : Royal Oak\n" +
                "        state   : MI\n" +
                "        postal  : 48046\n" +
                "ship-to: *id001\n" +
                "product:\n" +
                "    - sku         : BL394D\n" +
                "      quantity    : 4\n" +
                "      description : Basketball\n" +
                "      price       : 450.00\n" +
                "    - sku         : BL4438H\n" +
                "      quantity    : 1\n" +
                "      description : Super Hoop\n" +
                "      price       : 2392.00\n" +
                "tax  : 251.42\n" +
                "total: 4443.52\n" +
                "comments:\n" +
                "    Late afternoon is best.\n" +
                "    Backup contact is Nancy\n" +
                "    Billsmer @ 338-4338.";
    }

    public static void assertRangeEquals(
            int expectedStartOffset,
            int expectedEndOffset,
            @Nullable
                    YAMLRange actualRange) {
        if (actualRange == null) {
            fail("actualRange in null");
        } else {
            Assertions.assertAll(
                    () -> assertEquals(
                            expectedStartOffset,
                            actualRange.getStartOffset(),
                            "startOffset"),
                    () -> assertEquals(
                            expectedEndOffset,
                            actualRange.getEndOffset(),
                            "endOffset"));
        }
    }

    public static void assertFragmentEquals(
            int expectedStartOffset,
            int expectedStartLine,
            int expectedStartColumn,
            int expectedEndOffset,
            int expectedEndLine,
            int expectedEndColumn,
            Kind expectedKind,
            String jsonPointer,
            @Nullable Fragment actualFragment) {
        if (actualFragment == null) {
            fail("actualFragment in null");
        } else {

            Assertions.assertAll(
                    () -> assertEquals(
                            expectedStartOffset,
                            actualFragment.getStartOffset(),
                            "startOffset"),
                    () -> assertEquals(
                            expectedStartLine,
                            actualFragment.getStartLine(),
                            "startLine"),
                    () -> assertEquals(
                            expectedStartColumn,
                            actualFragment.getStartColumn(),
                            "startColumn"),
                    () -> assertEquals(
                            expectedEndOffset,
                            actualFragment.getEndOffset(),
                            "endOffset"),
                    () -> assertEquals(
                            expectedEndLine,
                            actualFragment.getEndLine(),
                            "endLine"),
                    () -> assertEquals(
                            expectedEndColumn,
                            actualFragment.getEndColumn(),
                            "endColumn"),
                    () -> assertEquals(
                            expectedKind,
                            actualFragment.getKind(),
                            "kind"),
                    () -> assertEquals(
                            jsonPointer,
                            actualFragment.getJSONPointer(),
                            "jsonPointer"));
        }
    }

    @Test
    void documentLength() {
        String yaml = "   foo   ";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        assertEquals(9, srcMap.documentLength());
    }

    @Test
    void fragment_containsLocation() {
        // stub a Fragment with locations 1:1 .. 1:3 (line:column)
        Fragment f = Mockito.mock(Fragment.class);
        when(f.containsLocation(anyInt(), anyInt())).thenCallRealMethod();
        when(f.getStartLine()).thenReturn(1);
        when(f.getStartColumn()).thenReturn(1);
        when(f.getEndLine()).thenReturn(1);
        when(f.getEndColumn()).thenReturn(3);

        assertFalse(f.containsLocation(0, 0));
        assertFalse(f.containsLocation(1, 0));
        assertTrue(f.containsLocation(1, 1));
        assertTrue(f.containsLocation(1, 2));
        assertFalse(f.containsLocation(1, 3));
        assertFalse(f.containsLocation(1, 4));
        assertFalse(f.containsLocation(2, 0));
    }

    @Test
    void allFragments() {
        String yaml = "   foo   ";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        List<? extends Fragment> allFragments = srcMap.allFragments();

        assertEquals(3, allFragments.size());
        assertFragmentEquals(0, 1, 1, 3, 1, 4, DOCUMENT_START, "",
                allFragments.get(0));
        assertFragmentEquals(3, 1, 4, 6, 1, 7, SCALAR_VALUE, "",
                allFragments.get(1));
        assertFragmentEquals(6, 1, 7, 9, 1, 10, DOCUMENT_END, "",
                allFragments.get(2));
    }

    @Test
    void allFragmentsMatching() {
        String yaml = "\nA: B\nC: D\n";
        // offsets-----0-12345-67890-1234567890123456789
        // ------------0-00000-00001-1111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        List<? extends Fragment> frags;

        // no match
        frags = srcMap.allFragmentsMatching(f -> false);
        assertEquals(0, frags.size());

        // everything matches
        frags = srcMap.allFragmentsMatching(f -> true);
        assertEquals(9, frags.size());

        // a single match (the fragment containing the "D")
        frags = srcMap.allFragmentsMatching(f -> f.getStartOffset() == 9);

        assertEquals(1, frags.size());
        assertFragmentEquals(9, 3, 4, 10, 3, 5, Kind.MAP_VALUE, "/C",
                frags.get(0));

        // a multi match (the "mapValues", i.e. "B" and "D")
        frags = srcMap.allFragmentsMatching(i -> i.getKind() == Kind.MAP_VALUE);
        assertEquals(2, frags.size());
        assertFragmentEquals(4, 2, 4, 5, 2, 5, Kind.MAP_VALUE, "/A",
                frags.get(0));
        assertFragmentEquals(9, 3, 4, 10, 3, 5, Kind.MAP_VALUE, "/C",
                frags.get(1));
    }

    @Test
    void findFirstFragmentMatching() {
        String yaml = "\nA: B\nC: D\n";
        // offsets-----0-12345-67890-1234567890123456789
        // ------------0-00000-00001-1111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        @Nullable
        Fragment frag;

        // no match
        frag = srcMap.findFirstFragmentMatching(f -> false);
        assertNull(frag);

        // everything matches
        frag = srcMap.findFirstFragmentMatching(f -> true);
        assertFragmentEquals(0, 1, 1, 1, 2, 1, DOCUMENT_START, "", frag);

        // a single match (the fragment containing the "D")
        frag = srcMap.findFirstFragmentMatching(f -> f.getStartOffset() == 9);
        assertFragmentEquals(9, 3, 4, 10, 3, 5, Kind.MAP_VALUE, "/C", frag);

        // a multi match (the "mapValues", i.e. "B" and "D")
        frag = srcMap.findFirstFragmentMatching(
                i -> i.getKind() == Kind.MAP_VALUE);
        assertFragmentEquals(4, 2, 4, 5, 2, 5, Kind.MAP_VALUE, "/A", frag);
    }

    @Test
    void fragment_toString() {
        String yaml = "\nA: B\nC: D\n";
        // offsets-----0-12345-67890-1234567890123456789
        // ------------0-00000-00001-1111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        Fragment frag = srcMap.fragmentAtOffset(9);
        assertEquals("" +
                "YAMLFragment{" +
                "startOffset=9, startLine=3, startColumn=4, " +
                "endOffset=10, endLine=3, endColumn=5, " +
                "kind=MAP_VALUE, jsonPointer=/C}" +
                "", frag.toString());
    }


    @Test
    void fragmentAtOffset() {
        String yaml = "- A\n- b\n";
        // offsets-----0123-4567-8901234567890123456789
        // ------------0000-0000-0011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        assertFragmentEquals(0, 1, 1, 2, 1, 3, Kind.SEQUENCE, "/0",
                srcMap.fragmentAtOffset(0));
        assertFragmentEquals(0, 1, 1, 2, 1, 3, Kind.SEQUENCE, "/0",
                srcMap.fragmentAtOffset(1));
        assertFragmentEquals(2, 1, 3, 3, 1, 4, Kind.SEQUENCE_ITEM, "/0",
                srcMap.fragmentAtOffset(2));
        assertFragmentEquals(6, 2, 3, 7, 2, 4, Kind.SEQUENCE_ITEM, "/1",
                srcMap.fragmentAtOffset(6));
        assertFragmentEquals(7, 2, 4, 8, 3, 1, Kind.SEQUENCE, "",
                srcMap.fragmentAtOffset(7));

        // an offset < 0 return the first fragment
        assertFragmentEquals(0, 1, 1, 2, 1, 3, Kind.SEQUENCE, "/0",
                srcMap.fragmentAtOffset(0));

        // an offset >= documentLength-1 returns the last fragment
        assertFragmentEquals(7, 2, 4, 8, 3, 1, Kind.SEQUENCE, "",
                srcMap.fragmentAtOffset(8));
    }

    @Test
    void fragmentAtLocation() {
        String yaml = "- A\n- b\n";
        // offsets-----0123-4567-8901234567890123456789
        // ------------0000-0000-0011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);
        assertFragmentEquals(2, 1, 3, 3, 1, 4, Kind.SEQUENCE_ITEM, "/0",
                srcMap.fragmentAtLocation(1, 3));

        assertFragmentEquals(0, 1, 1, 2, 1, 3, Kind.SEQUENCE, "/0",
                srcMap.fragmentAtLocation(1, 1));
        assertFragmentEquals(0, 1, 1, 2, 1, 3, Kind.SEQUENCE, "/0",
                srcMap.fragmentAtLocation(1, 2));
        assertFragmentEquals(2, 1, 3, 3, 1, 4, Kind.SEQUENCE_ITEM, "/0",
                srcMap.fragmentAtLocation(1, 3));
        assertFragmentEquals(3, 1, 4, 6, 2, 3, Kind.SEQUENCE, "/1",
                srcMap.fragmentAtLocation(1, 4));
        assertFragmentEquals(3, 1, 4, 6, 2, 3, Kind.SEQUENCE, "/1",
                srcMap.fragmentAtLocation(2, 1));
        assertFragmentEquals(3, 1, 4, 6, 2, 3, Kind.SEQUENCE, "/1",
                srcMap.fragmentAtLocation(2, 2));
        assertFragmentEquals(6, 2, 3, 7, 2, 4, Kind.SEQUENCE_ITEM, "/1",
                srcMap.fragmentAtLocation(2, 3));
        assertFragmentEquals(7, 2, 4, 8, 3, 1, Kind.SEQUENCE, "",
                srcMap.fragmentAtLocation(2, 4));

        // Exceptions
        YAMLSourceMapException e;
        // line too small
        e = assertThrows(YAMLSourceMapException.class,
                () -> srcMap.fragmentAtLocation(0, 1));
        assertEquals("line must be >= 1", e.getMessage());

        // column too small
        e = assertThrows(YAMLSourceMapException.class,
                () -> srcMap.fragmentAtLocation(1, 0));
        assertEquals("column must be >= 1", e.getMessage());

        // location too large
        e = assertThrows(YAMLSourceMapException.class,
                () -> srcMap.fragmentAtLocation(3, 1));
        assertEquals(
                "Invalid location. Got line=3, column=1",
                e.getMessage());
    }

    @Test
    void allFragmentsOfJsonPointer() {
        String yaml = "\nA: B\nC: D\n";
        // offsets-----0-12345-67890-1234567890123456789
        // ------------0-00000-00001-1111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        List<Fragment> frags = srcMap.allFragmentsOfJsonPointer("/C");
        assertEquals(4, frags.size());
        assertFragmentEquals(
                5, 2, 5, 6, 3, 1, Kind.MAP, "/C",
                frags.get(0));
        assertFragmentEquals(
                6, 3, 1, 7, 3, 2, Kind.MAP_KEY, "/C",
                frags.get(1));
        assertFragmentEquals(
                7, 3, 2, 9, 3, 4, Kind.MAP, "/C",
                frags.get(2));
        assertFragmentEquals(
                9, 3, 4, 10, 3, 5, Kind.MAP_VALUE, "/C",
                frags.get(3));
    }

    @Test
    void allFragmentsOfChildrenOfJsonPointer() {
        String yaml = "\nA:\n  B:\n    C: 9\n    D: 8\nE: 8\n";
        // offsets-----0-123-45678-901234567-89012345-67890
        // ------------0-000-00000-011111111-11222222-22223

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        // root
        List<Fragment> frags = srcMap.allFragmentsOfChildrenOfJsonPointer("");

        String result = toTSV(frags);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "1\t2\t1\t2\t2\t2\tMAP_KEY\t/A\n" +
                "2\t2\t2\t6\t3\t3\tMAP\t/A\n" +
                "6\t3\t3\t7\t3\t4\tMAP_KEY\t/A/B\n" +
                "7\t3\t4\t13\t4\t5\tMAP\t/A/B\n" +
                "13\t4\t5\t14\t4\t6\tMAP_KEY\t/A/B/C\n" +
                "14\t4\t6\t16\t4\t8\tMAP\t/A/B/C\n" +
                "16\t4\t8\t17\t4\t9\tMAP_VALUE\t/A/B/C\n" +
                "17\t4\t9\t22\t5\t5\tMAP\t/A/B/D\n" +
                "22\t5\t5\t23\t5\t6\tMAP_KEY\t/A/B/D\n" +
                "23\t5\t6\t25\t5\t8\tMAP\t/A/B/D\n" +
                "25\t5\t8\t26\t5\t9\tMAP_VALUE\t/A/B/D\n" +
                "26\t5\t9\t27\t6\t1\tMAP\t/A/B\n" +
                "27\t6\t1\t28\t6\t2\tMAP_KEY\t/E\n" +
                "28\t6\t2\t30\t6\t4\tMAP\t/E\n" +
                "30\t6\t4\t31\t6\t5\tMAP_VALUE\t/E\n" +
                "", result);

        // non-root, with siblings
        frags = srcMap.allFragmentsOfChildrenOfJsonPointer("/A");

        result = toTSV(frags);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "6\t3\t3\t7\t3\t4\tMAP_KEY\t/A/B\n" +
                "7\t3\t4\t13\t4\t5\tMAP\t/A/B\n" +
                "13\t4\t5\t14\t4\t6\tMAP_KEY\t/A/B/C\n" +
                "14\t4\t6\t16\t4\t8\tMAP\t/A/B/C\n" +
                "16\t4\t8\t17\t4\t9\tMAP_VALUE\t/A/B/C\n" +
                "17\t4\t9\t22\t5\t5\tMAP\t/A/B/D\n" +
                "22\t5\t5\t23\t5\t6\tMAP_KEY\t/A/B/D\n" +
                "23\t5\t6\t25\t5\t8\tMAP\t/A/B/D\n" +
                "25\t5\t8\t26\t5\t9\tMAP_VALUE\t/A/B/D\n" +
                "26\t5\t9\t27\t6\t1\tMAP\t/A/B\n" +
                "", result);

        // leaf
        frags = srcMap.allFragmentsOfChildrenOfJsonPointer("/A/C");

        result = toTSV(frags);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "", result);

        // Undefined element
        frags = srcMap.allFragmentsOfChildrenOfJsonPointer("/foo");

        result = toTSV(frags);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "", result);
    }


    @Test
    void sourceRangeOfJsonPointer() {
        String yaml = "\nA:\n  B:\n    C: 9\n    D: 8\nE: 8\n";
        // offsets-----0-123-45678-901234567-89012345-67890
        // ------------0-000-00000-011111111-11222222-22223

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        // scalar value
        YAMLRange range = srcMap.sourceRangeOfJsonPointer("/A/B/C");

        assertFalse(range.isEmpty());
        assertRangeEquals(13, 17, range);
        assertEquals("C: 9", range.getRangeText(yaml));

        // non-scalar value
        range = srcMap.sourceRangeOfJsonPointer("/A/B");

        assertFalse(range.isEmpty());
        assertRangeEquals(6, 27, range);
        assertEquals("B:\n    C: 9\n    D: 8\n",
                range.getRangeText(yaml));

        // Undefined value
        range = srcMap.sourceRangeOfJsonPointer("/foo");

        assertTrue(range.isEmpty());
    }

    @Test
    void sourceRangeOfValueOfJsonPointer() {
        String yaml = "\nA:\n  B:\n    C: 9\n    D: 8\nE: 8\n";
        // offsets-----0-123-45678-901234567-89012345-67890
        // ------------0-000-00000-011111111-11222222-22223

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        // scalar value
        YAMLRange range = srcMap.sourceRangeOfValueOfJsonPointer("/A/B/C");

        assertFalse(range.isEmpty());
        assertRangeEquals(16, 17, range);
        assertEquals("9", range.getRangeText(yaml));

        // non-scalar value
        range = srcMap.sourceRangeOfValueOfJsonPointer("/A/B");

        assertFalse(range.isEmpty());
        assertRangeEquals(13, 26, range);
        assertEquals("C: 9\n    D: 8", range.getRangeText(yaml));

        // Undefined value
        range = srcMap.sourceRangeOfValueOfJsonPointer("/foo");

        assertTrue(range.isEmpty());
    }


    @Test
    void valueFragmentOfJsonPointer_string_map() {
        String yaml = "\nA: B\nC: D\n";
        // offsets-----0-12345-67890-1234567890123456789
        // ------------0-00000-00001-1111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        @Nullable
        Fragment frag = srcMap.valueFragmentOfJsonPointer("/C");
        assertFragmentEquals(
                9, 3, 4, 10, 3, 5, Kind.MAP_VALUE, "/C",
                frag);
    }

    @Test
    void valueFragmentOfJsonPointer_string_sequence() {

        String yaml = "- A\n- b\n";
        // offsets-----0123-4567-8901234567890123456789
        // ------------0000-0000-0011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        @Nullable
        Fragment frag = srcMap.valueFragmentOfJsonPointer("/1");
        assertFragmentEquals(
                6, 2, 3, 7, 2, 4, Kind.SEQUENCE_ITEM, "/1",
                frag);
    }

    @Test
    void valueFragmentOfJsonPointer_string_scalar() {

        String yaml = "   foo   ";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        @Nullable
        Fragment frag = srcMap.valueFragmentOfJsonPointer("");
        assertFragmentEquals(
                3, 1, 4, 6, 1, 7, Kind.SCALAR_VALUE, "",
                frag);
    }

    @Test
    void valueFragmentForJsonPointer_aliasAsMapValue() {

        String yaml = "A: &X B\nC: *X";
        // offsets-----01234567-89012345-67890123456789
        // ------------00000000-00111111-11112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        @Nullable
        Fragment frag = srcMap.valueFragmentOfJsonPointer("/C");
        assertFragmentEquals(
                11, 2, 4, 13, 2, 6, Kind.ALIAS_AS_MAP_VALUE, "/C",
                frag);
    }


    @Test
    void valueFragmentForJsonPointer_aliasAsSequenceItem() {

        String yaml = "- &X A\n- *X";
        // offsets-----0123456-789012345-67890123456789
        // ------------0000000-000111111-11112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        @Nullable
        Fragment frag = srcMap.valueFragmentOfJsonPointer("/1");
        assertFragmentEquals(
                9, 2, 3, 11, 2, 5, Kind.ALIAS_AS_SEQUENCE_ITEM, "/1",
                frag);
    }

    @Test
    void valueFragmentForJsonPointer_invalidJsonPointer() {

        String yaml = "- &X A\n- *X";
        // offsets-----0123456-789012345-67890123456789
        // ------------0000000-000111111-11112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        @Nullable
        Fragment frag = srcMap.valueFragmentOfJsonPointer("/foo");
        assertNull(frag);
    }

    @Test
    void jsonPointerAtOffset() {
        String yaml = "\nA: B\nC: D\n";
        // offsets-----0-12345-67890-1234567890123456789
        // ------------0-00000-00001-1111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        assertEquals("", srcMap.jsonPointerAtOffset(0));
        assertEquals("/A", srcMap.jsonPointerAtOffset(1));
        assertEquals("/A", srcMap.jsonPointerAtOffset(2));
        assertEquals("/A", srcMap.jsonPointerAtOffset(3));
        assertEquals("/A", srcMap.jsonPointerAtOffset(4));
        assertEquals("/C", srcMap.jsonPointerAtOffset(5));
        assertEquals("/C", srcMap.jsonPointerAtOffset(6));
        assertEquals("/C", srcMap.jsonPointerAtOffset(7));
        assertEquals("/C", srcMap.jsonPointerAtOffset(8));
        assertEquals("/C", srcMap.jsonPointerAtOffset(9));
        assertEquals("", srcMap.jsonPointerAtOffset(10));
    }

    @Test
    void jsonPointerAtLocation() {
        String yaml = "\nA: B\nC: D\n";
        // offsets-----0-12345-67890-1234567890123456789
        // ------------0-00000-00001-1111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        assertEquals("", srcMap.jsonPointerAtLocation(1, 1));
        assertEquals("/A", srcMap.jsonPointerAtLocation(2, 1));
        assertEquals("/A", srcMap.jsonPointerAtLocation(2, 2));
        assertEquals("/A", srcMap.jsonPointerAtLocation(2, 3));
        assertEquals("/A", srcMap.jsonPointerAtLocation(2, 4));
        assertEquals("/C", srcMap.jsonPointerAtLocation(2, 5));
        assertEquals("/C", srcMap.jsonPointerAtLocation(3, 1));
        assertEquals("/C", srcMap.jsonPointerAtLocation(3, 2));
        assertEquals("/C", srcMap.jsonPointerAtLocation(3, 3));
        assertEquals("/C", srcMap.jsonPointerAtLocation(3, 4));
        assertEquals("", srcMap.jsonPointerAtLocation(3, 5));
    }


    @Test
    void scalar() {
        String yaml = "   foo   ";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t3\t1\t4\tDOCUMENT_START\t\n" +
                "3\t1\t4\t6\t1\t7\tSCALAR_VALUE\t\n" +
                "6\t1\t7\t9\t1\t10\tDOCUMENT_END\t\n" +
                "", result);
    }

    @Test
    void sequence() {
        String yaml = "- A\n- b\n";
        // offsets-----0123-4567-8901234567890123456789
        // ------------0000-0000-0011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t2\t1\t3\tSEQUENCE\t/0\n" +
                "2\t1\t3\t3\t1\t4\tSEQUENCE_ITEM\t/0\n" +
                "3\t1\t4\t6\t2\t3\tSEQUENCE\t/1\n" +
                "6\t2\t3\t7\t2\t4\tSEQUENCE_ITEM\t/1\n" +
                "7\t2\t4\t8\t3\t1\tSEQUENCE\t\n" +
                "", result);
    }

    @Test
    void sequence_empty() {
        String yaml = " [ ] ";
        // offsets-----01234567-8901234567890123456789
        // ------------00000000-0011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tDOCUMENT_START\t\n" +
                "1\t1\t2\t4\t1\t5\tSEQUENCE\t\n" +
                "4\t1\t5\t5\t1\t6\tDOCUMENT_END\t\n" +
                "", result);
    }

    @Test
    void sequence_floatStyle() {

        String yaml = "[ 1,2,  3  ] ";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tSEQUENCE\t\n" +
                "1\t1\t2\t2\t1\t3\tSEQUENCE\t/0\n" +
                "2\t1\t3\t3\t1\t4\tSEQUENCE_ITEM\t/0\n" +
                "3\t1\t4\t4\t1\t5\tSEQUENCE\t/1\n" +
                "4\t1\t5\t5\t1\t6\tSEQUENCE_ITEM\t/1\n" +
                "5\t1\t6\t8\t1\t9\tSEQUENCE\t/2\n" +
                "8\t1\t9\t9\t1\t10\tSEQUENCE_ITEM\t/2\n" +
                "9\t1\t10\t12\t1\t13\tSEQUENCE\t\n" +
                "12\t1\t13\t13\t1\t14\tDOCUMENT_END\t\n" +
                "", result);
    }

    @Test
    void sequence_nested() {

        String yaml = "[1,[2,3],4]";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tSEQUENCE\t\n" +
                "1\t1\t2\t2\t1\t3\tSEQUENCE_ITEM\t/0\n" +
                "2\t1\t3\t4\t1\t5\tSEQUENCE\t/1\n" +
                "4\t1\t5\t5\t1\t6\tSEQUENCE_ITEM\t/1/0\n" +
                "5\t1\t6\t6\t1\t7\tSEQUENCE\t/1/1\n" +
                "6\t1\t7\t7\t1\t8\tSEQUENCE_ITEM\t/1/1\n" +
                "7\t1\t8\t8\t1\t9\tSEQUENCE\t/1\n" +
                "8\t1\t9\t9\t1\t10\tSEQUENCE\t/2\n" +
                "9\t1\t10\t10\t1\t11\tSEQUENCE_ITEM\t/2\n" +
                "10\t1\t11\t11\t1\t12\tSEQUENCE\t\n" +
                "", result);
    }

    @Test
    void map() {
        String yaml = "\nA: B\nC: D\n";
        // offsets-----0-12345-67890-1234567890123456789
        // ------------0-00000-00001-1111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t2\t1\tDOCUMENT_START\t\n" +
                "1\t2\t1\t2\t2\t2\tMAP_KEY\t/A\n" +
                "2\t2\t2\t4\t2\t4\tMAP\t/A\n" +
                "4\t2\t4\t5\t2\t5\tMAP_VALUE\t/A\n" +
                "5\t2\t5\t6\t3\t1\tMAP\t/C\n" +
                "6\t3\t1\t7\t3\t2\tMAP_KEY\t/C\n" +
                "7\t3\t2\t9\t3\t4\tMAP\t/C\n" +
                "9\t3\t4\t10\t3\t5\tMAP_VALUE\t/C\n" +
                "10\t3\t5\t11\t4\t1\tMAP\t\n" +
                "", result);
    }

    @Test
    void map_floatStyle() {
        String yaml = " {1: 2, 3 : 4   }  ";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tDOCUMENT_START\t\n" +
                "1\t1\t2\t2\t1\t3\tMAP\t\n" +
                "2\t1\t3\t3\t1\t4\tMAP_KEY\t/1\n" +
                "3\t1\t4\t5\t1\t6\tMAP\t/1\n" +
                "5\t1\t6\t6\t1\t7\tMAP_VALUE\t/1\n" +
                "6\t1\t7\t8\t1\t9\tMAP\t/3\n" +
                "8\t1\t9\t9\t1\t10\tMAP_KEY\t/3\n" +
                "9\t1\t10\t12\t1\t13\tMAP\t/3\n" +
                "12\t1\t13\t13\t1\t14\tMAP_VALUE\t/3\n" +
                "13\t1\t14\t17\t1\t18\tMAP\t\n" +
                "17\t1\t18\t19\t1\t20\tDOCUMENT_END\t\n" +
                "", result);
    }

    @Test
    void map_nested() {
        String yaml = "{9: 1, 8: {7: 2,6: 3},5: 4}";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tMAP\t\n" +
                "1\t1\t2\t2\t1\t3\tMAP_KEY\t/9\n" +
                "2\t1\t3\t4\t1\t5\tMAP\t/9\n" +
                "4\t1\t5\t5\t1\t6\tMAP_VALUE\t/9\n" +
                "5\t1\t6\t7\t1\t8\tMAP\t/8\n" +
                "7\t1\t8\t8\t1\t9\tMAP_KEY\t/8\n" +
                "8\t1\t9\t11\t1\t12\tMAP\t/8\n" +
                "11\t1\t12\t12\t1\t13\tMAP_KEY\t/8/7\n" +
                "12\t1\t13\t14\t1\t15\tMAP\t/8/7\n" +
                "14\t1\t15\t15\t1\t16\tMAP_VALUE\t/8/7\n" +
                "15\t1\t16\t16\t1\t17\tMAP\t/8/6\n" +
                "16\t1\t17\t17\t1\t18\tMAP_KEY\t/8/6\n" +
                "17\t1\t18\t19\t1\t20\tMAP\t/8/6\n" +
                "19\t1\t20\t20\t1\t21\tMAP_VALUE\t/8/6\n" +
                "20\t1\t21\t21\t1\t22\tMAP\t/8\n" +
                "21\t1\t22\t22\t1\t23\tMAP\t/5\n" +
                "22\t1\t23\t23\t1\t24\tMAP_KEY\t/5\n" +
                "23\t1\t24\t25\t1\t26\tMAP\t/5\n" +
                "25\t1\t26\t26\t1\t27\tMAP_VALUE\t/5\n" +
                "26\t1\t27\t27\t1\t28\tMAP\t\n" +
                "", result);
    }

    @Test
    void map_empty() {
        String yaml = " { } ";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tDOCUMENT_START\t\n" +
                "1\t1\t2\t4\t1\t5\tMAP\t\n" +
                "4\t1\t5\t5\t1\t6\tDOCUMENT_END\t\n" +
                "", result);
    }

    @Test
    void scalar_blockStyle_in_sequence() {

        String yaml = "- >\n  A\n  B\n- C\n";
        // offsets-----0123-4567-8901-2345-67890123456789
        // ------------0000-0000-0011-1111-11112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t2\t1\t3\tSEQUENCE\t/0\n" +
                "2\t1\t3\t12\t4\t1\tSEQUENCE_ITEM\t/0\n" +
                "12\t4\t1\t14\t4\t3\tSEQUENCE\t/1\n" +
                "14\t4\t3\t15\t4\t4\tSEQUENCE_ITEM\t/1\n" +
                "15\t4\t4\t16\t5\t1\tSEQUENCE\t\n" +
                "", result);
    }

    @Test
    void comment() {

        String yaml = "- A # cmt\n- B\n# cmt\n- C\n";
        // offsets-----0123456789-0123-456789-0123-456789
        // ------------0000000000-1111-111111-2222-222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t2\t1\t3\tSEQUENCE\t/0\n" +
                "2\t1\t3\t3\t1\t4\tSEQUENCE_ITEM\t/0\n" +
                "3\t1\t4\t12\t2\t3\tSEQUENCE\t/1\n" +
                "12\t2\t3\t13\t2\t4\tSEQUENCE_ITEM\t/1\n" +
                "13\t2\t4\t22\t4\t3\tSEQUENCE\t/2\n" +
                "22\t4\t3\t23\t4\t4\tSEQUENCE_ITEM\t/2\n" +
                "23\t4\t4\t24\t5\t1\tSEQUENCE\t\n" +
                "", result);
    }

    @Test
    void aliasAsMapKey() {

        String yaml = "A: &X B\n*X: D";
        // offsets-----01234567-89012345-67890123456789
        // ------------00000000-00111111-11112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tMAP_KEY\t/A\n" +
                "1\t1\t2\t3\t1\t4\tMAP\t/A\n" +
                "3\t1\t4\t7\t1\t8\tMAP_VALUE\t/A\n" +
                "7\t1\t8\t8\t2\t1\tMAP\t/*X\n" +
                "8\t2\t1\t10\t2\t3\tALIAS_AS_MAP_KEY\t/*X\n" +
                "10\t2\t3\t12\t2\t5\tMAP\t/*X\n" +
                "12\t2\t5\t13\t2\t6\tMAP_VALUE\t/*X\n" +
                "", result);
    }

    @Test
    void aliasAsMapValue() {

        String yaml = "A: &X B\nC: *X";
        // offsets-----01234567-89012345-67890123456789
        // ------------00000000-00111111-11112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tMAP_KEY\t/A\n" +
                "1\t1\t2\t3\t1\t4\tMAP\t/A\n" +
                "3\t1\t4\t7\t1\t8\tMAP_VALUE\t/A\n" +
                "7\t1\t8\t8\t2\t1\tMAP\t/C\n" +
                "8\t2\t1\t9\t2\t2\tMAP_KEY\t/C\n" +
                "9\t2\t2\t11\t2\t4\tMAP\t/C\n" +
                "11\t2\t4\t13\t2\t6\tALIAS_AS_MAP_VALUE\t/C\n" +
                "", result);
    }

    @Test
    void aliasAsSequenceItem() {

        String yaml = "- &X A\n- *X";
        // offsets-----0123456-789012345-67890123456789
        // ------------0000000-000111111-11112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t2\t1\t3\tSEQUENCE\t/0\n" +
                "2\t1\t3\t6\t1\t7\tSEQUENCE_ITEM\t/0\n" +
                "6\t1\t7\t9\t2\t3\tSEQUENCE\t/1\n" +
                "9\t2\t3\t11\t2\t5\tALIAS_AS_SEQUENCE_ITEM\t/1\n" +
                "", result);
    }


    @Test
    void sequenceAndMap() {

        String yaml = "[1,{9: 8}, 4]";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tSEQUENCE\t\n" +
                "1\t1\t2\t2\t1\t3\tSEQUENCE_ITEM\t/0\n" +
                "2\t1\t3\t3\t1\t4\tSEQUENCE\t/1\n" +
                "3\t1\t4\t4\t1\t5\tMAP\t/1\n" +
                "4\t1\t5\t5\t1\t6\tMAP_KEY\t/1/9\n" +
                "5\t1\t6\t7\t1\t8\tMAP\t/1/9\n" +
                "7\t1\t8\t8\t1\t9\tMAP_VALUE\t/1/9\n" +
                "8\t1\t9\t9\t1\t10\tMAP\t/1\n" +
                "9\t1\t10\t11\t1\t12\tSEQUENCE\t/2\n" +
                "11\t1\t12\t12\t1\t13\tSEQUENCE_ITEM\t/2\n" +
                "12\t1\t13\t13\t1\t14\tSEQUENCE\t\n" +
                "", result);
    }


    @Test
    void documentStartAndMap() {

        String yaml = "---\nA: B";
        // offsets-----0123-45678901234567890123456789
        // ------------0000-00000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t4\t2\t1\tDOCUMENT_START\t\n" +
                "4\t2\t1\t5\t2\t2\tMAP_KEY\t/A\n" +
                "5\t2\t2\t7\t2\t4\tMAP\t/A\n" +
                "7\t2\t4\t8\t2\t5\tMAP_VALUE\t/A\n" +
                "", result);
    }

    @Test
    void mapAtStart() {

        String yaml = "#\nA: B";
        // offsets-----01-2345678901234567890123456789
        // ------------00-0000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t2\t2\t1\tDOCUMENT_START\t\n" +
                "2\t2\t1\t3\t2\t2\tMAP_KEY\t/A\n" +
                "3\t2\t2\t5\t2\t4\tMAP\t/A\n" +
                "5\t2\t4\t6\t2\t5\tMAP_VALUE\t/A\n" +
                "", result);
    }

    @Test
    void documentStart() {

        String yaml = "---\n A \n";
        // offsets-----0123-4567-890123-456789-0123456789
        // ------------0000-0000-001111-111111-2222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t5\t2\t2\tDOCUMENT_START\t\n" +
                "5\t2\t2\t6\t2\t3\tSCALAR_VALUE\t\n" +
                "6\t2\t3\t8\t3\t1\tDOCUMENT_END\t\n" +
                "", result);
    }

    @Test
    void documentEnd() {

        String yaml = "A\n...\n";
        // offsets-----01-2345-67-890123-456789-0123456789
        // ------------00-0000-00-001111-111111-2222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tSCALAR_VALUE\t\n" +
                "1\t1\t2\t6\t3\t1\tDOCUMENT_END\t\n" +
                "", result);
    }

    @Test
    void onlyOneDocumentSupported() {

        String yaml = "A\n---\n";
        // offsets-----01-2345-67-890123-456789-0123456789
        // ------------00-0000-00-001111-111111-2222222222

        YAMLSourceMapException e = assertThrows(YAMLSourceMapException.class,
                () -> createYAMLSourceMap(yaml));

        assertEquals("Only one document supported", e.getMessage());
    }

    @Test
    void emptyDocument() {
        String yaml = "";
        // offsets-----012345678901234567890123456789
        // ------------000000000011111111112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        // empty list
        assertEquals(FRAGMENTS_TSV_HEADER, result);
    }


    @Test
    void jsonPointer_withCharactersToEscape() {
        // Checking the Json Pointer implementation for the escape cases
        // (see RFC 6901 (https://tools.ietf.org/html/rfc6901))

        String yaml = "{\"a/b\": 1, \"c~d\": 2}";
        // offsets-----01-2345-678901-2345-67890123456789
        // ------------00-0000-000011-1111-11112222222222

        YAMLSourceMap srcMap = createYAMLSourceMap(yaml);

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t1\t1\t2\tMAP\t\n" +
                "1\t1\t2\t6\t1\t7\tMAP_KEY\t/a~1b\n" +
                "6\t1\t7\t8\t1\t9\tMAP\t/a~1b\n" +
                "8\t1\t9\t9\t1\t10\tMAP_VALUE\t/a~1b\n" +
                "9\t1\t10\t11\t1\t12\tMAP\t/c~0d\n" +
                "11\t1\t12\t16\t1\t17\tMAP_KEY\t/c~0d\n" +
                "16\t1\t17\t18\t1\t19\tMAP\t/c~0d\n" +
                "18\t1\t19\t19\t1\t20\tMAP_VALUE\t/c~0d\n" +
                "19\t1\t20\t20\t1\t21\tMAP\t\n" +
                "", result);
    }

    @Test
    void example_2_27_Invoice() {

        YAMLSourceMap srcMap = createYAMLSourceMap(example_2_27_Invoice_yaml());

        String result = toTSV(srcMap);
        assertEquals("" +
                FRAGMENTS_TSV_HEADER +
                "0\t1\t1\t4\t2\t1\tDOCUMENT_START\t\n" +
                "4\t2\t1\t11\t2\t8\tMAP_KEY\t/invoice\n" +
                "11\t2\t8\t13\t2\t10\tMAP\t/invoice\n" +
                "13\t2\t10\t18\t2\t15\tMAP_VALUE\t/invoice\n" +
                "18\t2\t15\t19\t3\t1\tMAP\t/date\n" +
                "19\t3\t1\t23\t3\t5\tMAP_KEY\t/date\n" +
                "23\t3\t5\t28\t3\t10\tMAP\t/date\n" +
                "28\t3\t10\t38\t3\t20\tMAP_VALUE\t/date\n" +
                "38\t3\t20\t39\t4\t1\tMAP\t/bill-to\n" +
                "39\t4\t1\t46\t4\t8\tMAP_KEY\t/bill-to\n" +
                "46\t4\t8\t59\t5\t5\tMAP\t/bill-to\n" +
                "59\t5\t5\t64\t5\t10\tMAP_KEY\t/bill-to/given\n" +
                "64\t5\t10\t68\t5\t14\tMAP\t/bill-to/given\n" +
                "68\t5\t14\t73\t5\t19\tMAP_VALUE\t/bill-to/given\n" +
                "73\t5\t19\t78\t6\t5\tMAP\t/bill-to/family\n" +
                "78\t6\t5\t84\t6\t11\tMAP_KEY\t/bill-to/family\n" +
                "84\t6\t11\t87\t6\t14\tMAP\t/bill-to/family\n" +
                "87\t6\t14\t93\t6\t20\tMAP_VALUE\t/bill-to/family\n" +
                "93\t6\t20\t98\t7\t5\tMAP\t/bill-to/address\n" +
                "98\t7\t5\t105\t7\t12\tMAP_KEY\t/bill-to/address\n" +
                "105\t7\t12\t115\t8\t9\tMAP\t/bill-to/address\n" +
                "115\t8\t9\t120\t8\t14\tMAP_KEY\t/bill-to/address/lines\n" +
                "120\t8\t14\t122\t8\t16\tMAP\t/bill-to/address/lines\n" +
                "122\t8\t16\t175\t11\t1\tMAP_VALUE\t/bill-to/address/lines\n" +
                "175\t11\t1\t183\t11\t9\tMAP\t/bill-to/address/city\n" +
                "183\t11\t9\t187\t11\t13\tMAP_KEY\t/bill-to/address/city\n" +
                "187\t11\t13\t193\t11\t19\tMAP\t/bill-to/address/city\n" +
                "193\t11\t19\t202\t11\t28\tMAP_VALUE\t/bill-to/address/city\n" +
                "202\t11\t28\t211\t12\t9\tMAP\t/bill-to/address/state\n" +
                "211\t12\t9\t216\t12\t14\tMAP_KEY\t/bill-to/address/state\n" +
                "216\t12\t14\t221\t12\t19\tMAP\t/bill-to/address/state\n" +
                "221\t12\t19\t223\t12\t21\tMAP_VALUE\t/bill-to/address/state\n" +
                "223\t12\t21\t232\t13\t9\tMAP\t/bill-to/address/postal\n" +
                "232\t13\t9\t238\t13\t15\tMAP_KEY\t/bill-to/address/postal\n" +
                "238\t13\t15\t242\t13\t19\tMAP\t/bill-to/address/postal\n" +
                "242\t13\t19\t247\t13\t24\tMAP_VALUE\t/bill-to/address/postal\n" +
                "247\t13\t24\t248\t14\t1\tMAP\t/bill-to/address\n" +
                "248\t14\t1\t255\t14\t8\tMAP_KEY\t/ship-to\n" +
                "255\t14\t8\t257\t14\t10\tMAP\t/ship-to\n" +
                "257\t14\t10\t263\t14\t16\tALIAS_AS_MAP_VALUE\t/ship-to\n" +
                "263\t14\t16\t264\t15\t1\tMAP\t/product\n" +
                "264\t15\t1\t271\t15\t8\tMAP_KEY\t/product\n" +
                "271\t15\t8\t277\t16\t5\tMAP\t/product\n" +
                "277\t16\t5\t279\t16\t7\tSEQUENCE\t/product/0\n" +
                "279\t16\t7\t282\t16\t10\tMAP_KEY\t/product/0/sku\n" +
                "282\t16\t10\t293\t16\t21\tMAP\t/product/0/sku\n" +
                "293\t16\t21\t299\t16\t27\tMAP_VALUE\t/product/0/sku\n" +
                "299\t16\t27\t306\t17\t7\tMAP\t/product/0/quantity\n" +
                "306\t17\t7\t314\t17\t15\tMAP_KEY\t/product/0/quantity\n" +
                "314\t17\t15\t320\t17\t21\tMAP\t/product/0/quantity\n" +
                "320\t17\t21\t321\t17\t22\tMAP_VALUE\t/product/0/quantity\n" +
                "321\t17\t22\t328\t18\t7\tMAP\t/product/0/description\n" +
                "328\t18\t7\t339\t18\t18\tMAP_KEY\t/product/0/description\n" +
                "339\t18\t18\t342\t18\t21\tMAP\t/product/0/description\n" +
                "342\t18\t21\t352\t18\t31\tMAP_VALUE\t/product/0/description\n" +
                "352\t18\t31\t359\t19\t7\tMAP\t/product/0/price\n" +
                "359\t19\t7\t364\t19\t12\tMAP_KEY\t/product/0/price\n" +
                "364\t19\t12\t373\t19\t21\tMAP\t/product/0/price\n" +
                "373\t19\t21\t379\t19\t27\tMAP_VALUE\t/product/0/price\n" +
                "379\t19\t27\t384\t20\t5\tMAP\t/product/0\n" +
                "384\t20\t5\t386\t20\t7\tSEQUENCE\t/product/1\n" +
                "386\t20\t7\t389\t20\t10\tMAP_KEY\t/product/1/sku\n" +
                "389\t20\t10\t400\t20\t21\tMAP\t/product/1/sku\n" +
                "400\t20\t21\t407\t20\t28\tMAP_VALUE\t/product/1/sku\n" +
                "407\t20\t28\t414\t21\t7\tMAP\t/product/1/quantity\n" +
                "414\t21\t7\t422\t21\t15\tMAP_KEY\t/product/1/quantity\n" +
                "422\t21\t15\t428\t21\t21\tMAP\t/product/1/quantity\n" +
                "428\t21\t21\t429\t21\t22\tMAP_VALUE\t/product/1/quantity\n" +
                "429\t21\t22\t436\t22\t7\tMAP\t/product/1/description\n" +
                "436\t22\t7\t447\t22\t18\tMAP_KEY\t/product/1/description\n" +
                "447\t22\t18\t450\t22\t21\tMAP\t/product/1/description\n" +
                "450\t22\t21\t460\t22\t31\tMAP_VALUE\t/product/1/description\n" +
                "460\t22\t31\t467\t23\t7\tMAP\t/product/1/price\n" +
                "467\t23\t7\t472\t23\t12\tMAP_KEY\t/product/1/price\n" +
                "472\t23\t12\t481\t23\t21\tMAP\t/product/1/price\n" +
                "481\t23\t21\t488\t23\t28\tMAP_VALUE\t/product/1/price\n" +
                "488\t23\t28\t489\t24\t1\tMAP\t/product/1\n" +
                "489\t24\t1\t492\t24\t4\tMAP_KEY\t/tax\n" +
                "492\t24\t4\t496\t24\t8\tMAP\t/tax\n" +
                "496\t24\t8\t502\t24\t14\tMAP_VALUE\t/tax\n" +
                "502\t24\t14\t503\t25\t1\tMAP\t/total\n" +
                "503\t25\t1\t508\t25\t6\tMAP_KEY\t/total\n" +
                "508\t25\t6\t510\t25\t8\tMAP\t/total\n" +
                "510\t25\t8\t517\t25\t15\tMAP_VALUE\t/total\n" +
                "517\t25\t15\t518\t26\t1\tMAP\t/comments\n" +
                "518\t26\t1\t526\t26\t9\tMAP_KEY\t/comments\n" +
                "526\t26\t9\t532\t27\t5\tMAP\t/comments\n" +
                "532\t27\t5\t608\t29\t25\tMAP_VALUE\t/comments\n" +
                "", result);
    }
}
