package org.abego.yaml.sourcemap.internal;

import org.abego.yaml.sourcemap.YAMLRange;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Objects;

final class YAMLRangeDefault implements YAMLRange {
    private final int startOffset;
    private final int endOffset;

    private YAMLRangeDefault(int startOffset, int endOffset) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public static YAMLRangeDefault createYAMLRangeDefault(int startOffset, int endOffset) {
        return new YAMLRangeDefault(startOffset, endOffset);
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public int getEndOffset() {
        return endOffset;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof YAMLRange)) return false;
        YAMLRange that = (YAMLRange) o;
        return startOffset == that.getStartOffset() &&
                endOffset == that.getEndOffset();
    }

    @Override
    public int hashCode() {
        return Objects.hash(startOffset, endOffset);
    }
}
