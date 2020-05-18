package org.abego.yaml.sourcemap;

/**
 * Identifies a range in a YAML/JSON text.
 *
 * <p>A YAMLRange includes all characters of the referenced text from
 * from {@link #getStartOffset()} to {@link #getEndOffset()}, with the
 * character at endOffset not included.</p>
 */
public interface YAMLRange {
    /**
     * Returns the offset of the start of the range to the beginning
     * of the text.
     *
     * @return the offset of the start of the range to the beginning
     * of the text
     */
    int getStartOffset();

    /**
     * Returns the offset of the end of the range to the beginning
     * of the text.
     *
     * <p>The character at endOffset is not included.</p>
     *
     * @return the offset of the end of the range to the beginning
     * of the text
     */
    int getEndOffset();

    /**
     * Returns true when the range is empty; false otherwise.
     *
     * @return true when the range is empty; false otherwise
     */
    default boolean isEmpty() {
        return getStartOffset() >= getEndOffset();
    }

    /**
     * Returns the text of the range, assuming it refers to a range in
     * {@code fullText}.
     *
     * @param fullText the text this range refers to
     * @return the text of the range, assuming it refers to a range in
     * {@code fullText}
     */
    default String getRangeText(String fullText) {
        return fullText.substring(getStartOffset(), getEndOffset());
    }
}
