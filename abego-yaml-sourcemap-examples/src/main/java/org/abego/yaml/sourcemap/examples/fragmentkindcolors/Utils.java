package org.abego.yaml.sourcemap.examples.fragmentkindcolors;

import java.awt.Color;
import java.util.regex.Pattern;

final class Utils {
    private static final Pattern END_OF_LINE_PATTERN = Pattern
            .compile("\\r?\\n");

    public interface LineProcessor {
        void processLine(String lineText, boolean isFirstLine);
    }

    private Utils() {
    }

    /**
     * Return the {@code color} in HTML syntax (like "{@code #FE2353}").
     */
    public static String toHtml(Color color) {
        return String.format("#%02X%02X%02X",
                color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Calls the lineProcessor for each line of the {@code text}, also providing
     * the information if that line is the first one or not.
     */
    public static void forEachLine(String text, LineProcessor lineProcessor) {
        boolean isFirstLine = true;
        for (String lineText : END_OF_LINE_PATTERN.split(text, -1)) {
            lineProcessor.processLine(lineText, isFirstLine);
            isFirstLine = false;
        }
    }

    static String toHtml(String s) {
        StringBuilder result = new StringBuilder(s.length());
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
                //noinspection MagicCharacter
                case '&':
                    result.append("&amp;"); //NON-NLS
                    break;
                //noinspection MagicCharacter
                case '<':
                    result.append("&lt;"); //NON-NLS
                    break;
                //noinspection MagicCharacter
                case '>':
                    result.append("&gt;"); //NON-NLS
                    break;
                //noinspection MagicCharacter
                case '"':
                    result.append("&quot;"); //NON-NLS
                    break;
                //noinspection MagicCharacter
                case '\'':
                    // The single quote has no name (like "&quote;") so use the
                    // generic "&#nnn;" construct
                    result.append("&#039;");
                    break;
                default:
                    result.append(c);
                    break;
            }
        }
        return result.toString();
    }
}
