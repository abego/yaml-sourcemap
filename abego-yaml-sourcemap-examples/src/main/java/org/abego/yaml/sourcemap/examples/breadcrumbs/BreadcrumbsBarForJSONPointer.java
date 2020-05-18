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

package org.abego.yaml.sourcemap.examples.breadcrumbs;

import org.eclipse.jdt.annotation.NonNull;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("serial")
final class BreadcrumbsBarForJSONPointer extends JPanel {
    private final List<Consumer<String>> breadcrumbSelectedListener = new ArrayList<>();
    private String jsonPointer = "";
    private String prefix = " ";

    public BreadcrumbsBarForJSONPointer() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        updateCrumbs();
    }

    /**
     * Returns the rest of {@code strings} (all strings but the first one);
     * or an empty array when strings is empty.
     */
    private static @NonNull String[] rest(@NonNull String[] strings) {
        return strings.length == 0
                ? new String[0]
                : Arrays.copyOfRange(strings, 1, strings.length);
    }

    public void setPrefix(String text) {
        prefix = text;

        updateCrumbs();
    }

    public void setJSONPointer(String jsonPointer) {
        if (this.jsonPointer.equals(jsonPointer))
            return;

        this.jsonPointer = jsonPointer;

        updateCrumbs();
    }

    /**
     * Adds a listener that will be called when a breadcrumb is selected,
     * passing in the JSON Pointer of the selected breadcrumb.
     */
    public void addBreadcrumbSelectedListener(Consumer<String> listener) {
        breadcrumbSelectedListener.add(listener);
    }

    /**
     * Call this method when a property affecting the breadcrumbs
     * (prefix, jsonPointer) changed.
     */
    private void updateCrumbs() {
        removeAll();

        addCrumb(prefix, Font.BOLD, "");

        @NonNull
        String[] steps = rest(jsonPointer.split("/"));
        StringBuilder actJsonPointer = new StringBuilder();
        for (String s : steps) {
            addSeparator();
            actJsonPointer.append("/");
            actJsonPointer.append(s);
            addCrumb(s, Font.PLAIN, actJsonPointer.toString());
        }

        revalidate();
        repaint();
    }

    private void addCrumb(String text, int fontStyle, String jsonPointer) {
        JButton button = new JButton(text.isEmpty() ? " " : text); // [1]
        button.setFont(new Font("Sanserif", fontStyle, 12));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(l -> onBreadcrumbSelected(jsonPointer));
        add(button);

        // [1]: Regarding the " ": don't add empty strings as this would make
        // the JButton very small, also resulting in a very low breadcrumbs bar.
    }

    private void addSeparator() {
        JLabel separator = new JLabel("〉");
        separator.setFont(new Font("Sanserif", Font.PLAIN, 15));
        add(separator);
    }

    private void onBreadcrumbSelected(String jsonPointer) {
        for (Consumer<String> listener : breadcrumbSelectedListener) {
            listener.accept(jsonPointer);
        }
    }
}
