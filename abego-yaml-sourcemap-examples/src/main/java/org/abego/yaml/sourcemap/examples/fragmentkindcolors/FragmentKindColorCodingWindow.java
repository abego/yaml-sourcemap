package org.abego.yaml.sourcemap.examples.fragmentkindcolors;

import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

@SuppressWarnings("serial")
final class FragmentKindColorCodingWindow extends JFrame {
    private final FragmentKindColorScheme colorScheme = new FragmentKindColorSchemeDefault();
    private final FragmentKindColorCoding colorCoding = new FragmentKindColorCoding(colorScheme);
    private final JCheckBox isLegendVisibleCheckBox = new JCheckBox("Show Legend");
    private final JEditorPane output = new JEditorPane();
    private String yamlText = "";

    public FragmentKindColorCodingWindow() {
        setTitle("Fragment Kinds");
        setSize(600, 800);

        // Initially show the legend with the color codes
        isLegendVisibleCheckBox.setSelected(true);

        // Layout: the color coded YAML text in the center of the window,
        // the "Show Legend" button at the bottom
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(output), BorderLayout.CENTER);
        getContentPane().add(isLegendVisibleCheckBox, BorderLayout.SOUTH);

        // Events: when the isLegendVisibleCheckBox changes update the output
        isLegendVisibleCheckBox.addActionListener(e -> updateOutput());
    }

    public void setYaml(String yamlText) {
        this.yamlText = yamlText;

        updateOutput();
    }

    public String getYamlText() {
        return yamlText;
    }

    private boolean isLegendVisible() {
        return isLegendVisibleCheckBox.isSelected();
    }

    /**
     * Call this method when a property changed that affects the output
     * (yamlText, isLegendVisible).
     */
    private void updateOutput() {
        String htmlText = calcOutputAsHTML(getYamlText());
        output.setContentType("text/html");
        output.setText(htmlText);
    }

    private String calcOutputAsHTML(String yamlText) {
        // Use the HTMLTextBuilder to construct the output HTML text
        HTMLTextBuilder textBuilder = new HTMLTextBuilder();

        // 1. add the YAML text, with the color coding
        colorCoding.addColorCodedYamlText(textBuilder, yamlText);

        // 2. Optionally, add the legend explaining the color coding
        if (isLegendVisible()) {
            textBuilder.newLine();
            textBuilder.newLine();
            colorCoding.addLegend(textBuilder);
        }

        return textBuilder.getHtmlText();
    }
}
