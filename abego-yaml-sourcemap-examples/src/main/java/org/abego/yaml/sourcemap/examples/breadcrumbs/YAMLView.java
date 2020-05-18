package org.abego.yaml.sourcemap.examples.breadcrumbs;

import org.eclipse.jdt.annotation.Nullable;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import java.awt.Color;
import java.awt.Font;

final class YAMLView {
    private final JTextArea yamlTextArea = new JTextArea();
    private final JScrollPane scrollPane = new JScrollPane(yamlTextArea);

    private final HighlightPainter valueHighlighting =
            new DefaultHighlighter.DefaultHighlightPainter(Color.orange);
    private final HighlightPainter fragmentsHighlighting =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(0xffeccc));
    @Nullable
    private Object highlightTag;
    @Nullable
    private Object weakHighlightTag;

    public YAMLView() {
        yamlTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
    }

    public JComponent getComponent() {
        return scrollPane;
    }

    public void addCaretListener(Runnable caretListener) {
        yamlTextArea.addCaretListener(e -> caretListener.run());
    }

    public void setText(String text) {
        yamlTextArea.setText(text);
    }

    public int getCaretOffset() {
        return yamlTextArea.getSelectionStart();
    }

    public void setCaretOffset(int offset) {
        yamlTextArea.setSelectionStart(offset);
        yamlTextArea.setSelectionEnd(offset);
    }

    @Nullable
    private Object changeHighlighting(@Nullable Object highlightTag,
                                      int startOffset, int endOffset,
                                      HighlightPainter highlightPainter) {
        @Nullable
        Object result = null;
        try {
            if (highlightTag != null) {
                getHighlighter().removeHighlight(highlightTag);
            }
            if (startOffset < endOffset) {
                result = getHighlighter().addHighlight(
                        startOffset, endOffset, highlightPainter);
            }
        } catch (BadLocationException e) {
            throw new IllegalArgumentException(e);
        }
        return result;
    }

    public void highlightRangeAsSelectedValue(int startOffset, int endOffset) {
        highlightTag = changeHighlighting(
                highlightTag, startOffset, endOffset, valueHighlighting);
    }

    public void highlightRangeAsSelectedEntity(int startOffset, int endOffset) {
        weakHighlightTag = changeHighlighting(
                weakHighlightTag, startOffset, endOffset, fragmentsHighlighting);
    }

    private Highlighter getHighlighter() {
        return yamlTextArea.getHighlighter();
    }

}
