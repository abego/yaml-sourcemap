package org.abego.yaml.sourcemap.internal;

import org.abego.yaml.sourcemap.YAMLRange;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YAMLRangeDefaultTest {

    @Test
    void smokeTest() {
        YAMLRange range = YAMLRangeDefault.createYAMLRangeDefault(1, 2);
        YAMLRange rangeB = YAMLRangeDefault.createYAMLRangeDefault(1, 2);
        YAMLRange rangeEmpty = YAMLRangeDefault.createYAMLRangeDefault(3, 3);

        assertEquals(1, range.getStartOffset());
        assertEquals(2, range.getEndOffset());

        assertFalse(range.isEmpty());
        assertTrue(rangeEmpty.isEmpty());

        assertEquals(range.hashCode(), rangeB.hashCode());
    }

    @Test
    void testEquals() {
        YAMLRange range = YAMLRangeDefault.createYAMLRangeDefault(1, 2);
        YAMLRange rangeB = YAMLRangeDefault.createYAMLRangeDefault(1, 2);
        YAMLRange rangeEmpty = YAMLRangeDefault.createYAMLRangeDefault(3, 3);

        assertEquals(range, range);
        assertEquals(range, rangeB);
        assertNotEquals(range, rangeEmpty);
        // to ensure test coverage compare with inconvertible types
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals("foo", range);
    }
}