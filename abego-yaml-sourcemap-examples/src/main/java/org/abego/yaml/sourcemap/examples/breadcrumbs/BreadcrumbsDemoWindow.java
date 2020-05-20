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

import org.abego.yaml.sourcemap.YAMLRange;
import org.abego.yaml.sourcemap.YAMLSourceMap;
import org.abego.yaml.sourcemap.YAMLSourceMap.Fragment;
import org.abego.yaml.sourcemap.YAMLSourceMapAPI;
import org.eclipse.jdt.annotation.Nullable;

import javax.swing.JFrame;
import java.awt.BorderLayout;

@SuppressWarnings("serial")
final class BreadcrumbsDemoWindow extends JFrame {
    private final BreadcrumbsBarForJSONPointer breadcrumbsBar =
            new BreadcrumbsBarForJSONPointer();
    private final YAMLView yamlView = new YAMLView();

    private String contentDescriptor = "";
    private String yamlText = "";
    private YAMLSourceMap yamlSourceMap =
            YAMLSourceMapAPI.createYAMLSourceMap(yamlText);

    public BreadcrumbsDemoWindow() {
        setTitle("Breadcrumbs Demo");
        setSize(600, 800);


        // Layout: the Breadcrumbs at the top, the YAML text below
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(breadcrumbsBar, BorderLayout.NORTH);
        getContentPane().add(yamlView.getComponent(), BorderLayout.CENTER);

        // Events:
        // When the caret/selection in the yamlView changes, update the
        // breadcrumbs and Highlighting
        yamlView.addCaretListener(() -> {
            updateBreadcrumbs();
            updateHighlighting();
        });
        // When a breadcrumb is selected, move the cursor to the matching
        // fragment in the yamlView
        breadcrumbsBar.addBreadcrumbSelectedListener(jsonPointer -> {
            @Nullable Fragment fragment =
                    yamlSourceMap.findFirstFragmentMatching(f ->
                            f.getJSONPointer().equals(jsonPointer));
            if (fragment != null) {
                yamlView.setCaretOffset(fragment.getStartOffset());
            }
        });

    }

    public void setContentDescriptorAndYaml(String descriptor, String yamlText) {
        setContentDescriptor(descriptor);
        setYaml(yamlText);

        // Update the views that depend on the descriptor or the YAML text
        updateYamlView();
        updateBreadcrumbs();
    }

    private void updateYamlView() {
        yamlView.setText(yamlText);
    }

    private String getContentDescriptor() {
        return contentDescriptor;
    }

    private void setContentDescriptor(String contentDescriptor) {
        this.contentDescriptor = contentDescriptor;
    }

    private void setYaml(String yamlText) {
        this.yamlText = yamlText;
        this.yamlSourceMap = YAMLSourceMapAPI.createYAMLSourceMap(yamlText);
    }

    /**
     * Call this method when a property changed that affects the breadcrumbs
     * (contentDescriptor, yamlText, current text selection).
     */
    private void updateBreadcrumbs() {
        breadcrumbsBar.setPrefix(getContentDescriptor());
        breadcrumbsBar.setJSONPointer(getJSONPointerOfYAMLTextSelection());
    }

    private String getJSONPointerOfYAMLTextSelection() {
        int offset = yamlView.getCaretOffset();
        return yamlSourceMap.jsonPointerAtOffset(offset);
    }

    private void updateHighlighting() {
        String jsonPointer = getJSONPointerOfYAMLTextSelection();

        highlightSourceRangeOfValueOfJsonPointer(jsonPointer);
        highlightSourceRangeOfJsonPointer(jsonPointer);
    }

    private void highlightSourceRangeOfJsonPointer(String jsonPointer) {
        YAMLRange range = yamlSourceMap.sourceRangeOfJsonPointer(jsonPointer);
        yamlView.highlightRangeAsSelectedEntity(
                range.getStartOffset(), range.getEndOffset());
    }

    private void highlightSourceRangeOfValueOfJsonPointer(String jsonPointer) {
        YAMLRange range = yamlSourceMap.sourceRangeOfValueOfJsonPointer(jsonPointer);
        yamlView.highlightRangeAsSelectedValue(
                range.getStartOffset(), range.getEndOffset());
    }
}