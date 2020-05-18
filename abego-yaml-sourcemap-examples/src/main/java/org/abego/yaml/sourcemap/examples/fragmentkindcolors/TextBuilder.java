package org.abego.yaml.sourcemap.examples.fragmentkindcolors;

import java.awt.Color;

interface TextBuilder {
    void addText(String text);

    void addHighlightedText(String text, Color highlightColor);

    void newLine();

    void beginTable();

    void newTableCell();

    void newTableRow();

    void endTable();
}
