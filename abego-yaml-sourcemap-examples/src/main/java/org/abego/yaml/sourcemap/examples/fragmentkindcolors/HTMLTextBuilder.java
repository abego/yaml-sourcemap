package org.abego.yaml.sourcemap.examples.fragmentkindcolors;

import java.awt.Color;
import java.util.regex.Pattern;

import static org.abego.yaml.sourcemap.examples.fragmentkindcolors.Utils.toHtml;

class HTMLTextBuilder implements TextBuilder {
    private static final String PARAGRAPH_START =
            "<p style=\"margin: 0;font-family: monospace;\">";
    private static final String PARAGRAPH_END = "</p>";
    private static final Pattern SPACE_PATTERN = Pattern.compile(" ");
    private final StringBuilder result = new StringBuilder();

    HTMLTextBuilder() {
    }

    /**
     * Returns the {@code text} with all spaces converted to "{@code &nbsp;}"s.
     */
    private static String spacesToNBSPConverted(String text) {
        return SPACE_PATTERN.matcher(text).replaceAll("&nbsp;");
    }

    /**
     * Returns the {@code text} escaped for HTML, also with all spaces converted
     * to "{@code &nbsp;}"s.
     */
    private static String toHtmlWithNBSP(String text) {
        return spacesToNBSPConverted(Utils.toHtml(text));
    }

    public String getHtmlText() {
        return PARAGRAPH_START + result + PARAGRAPH_END;
    }

    @Override
    public void addText(String text) {
        result.append(toHtmlWithNBSP(text));
    }

    @Override
    public void addHighlightedText(String text, Color highlightColor) {
        String html = "<span style=\"background-color: " + toHtml(highlightColor) + "\">" + spacesToNBSPConverted(text) + "</span>";
        result.append(html);
    }

    @Override
    public void newLine() {
        result.append(PARAGRAPH_END);
        result.append(PARAGRAPH_START);
    }

    @Override
    public void beginTable() {
        result.append(PARAGRAPH_END + "<table><tr><td>" + PARAGRAPH_START);
    }

    @Override
    public void newTableCell() {
        result.append(PARAGRAPH_END + "</td><td>" + PARAGRAPH_START);
    }

    @Override
    public void newTableRow() {
        result.append(PARAGRAPH_END + "</td></tr><tr><td>" + PARAGRAPH_START);
    }

    @Override
    public void endTable() {
        result.append(PARAGRAPH_END + "</td></tr></table>");
    }

}
