package org.abego.yaml.sourcemap.examples.fragmentkindcolors;

import org.abego.yaml.sourcemap.YAMLSourceMap;
import org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind;
import org.abego.yaml.sourcemap.YAMLSourceMapAPI;

import java.awt.Color;

import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.ALIAS_AS_MAP_KEY;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.ALIAS_AS_MAP_VALUE;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.ALIAS_AS_SEQUENCE_ITEM;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.DOCUMENT_END;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.DOCUMENT_START;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.MAP;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.MAP_KEY;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.MAP_VALUE;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.SCALAR;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.SCALAR_VALUE;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.SEQUENCE;
import static org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind.SEQUENCE_ITEM;

final class FragmentKindColorCoding {
    private final FragmentKindColorScheme colorScheme;

    public FragmentKindColorCoding(FragmentKindColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    public void addColorCodedYamlText(TextBuilder textBuilder, String yamlText) {
        YAMLSourceMap yamlSourceMap =
                YAMLSourceMapAPI.createYAMLSourceMap(yamlText);

        // construct the text fragment by fragment
        for (YAMLSourceMap.Fragment f : yamlSourceMap.allFragments()) {
            Color color = colorScheme.getColor(f.getKind());
            String fragmentText = yamlText.substring(
                    f.getStartOffset(), f.getEndOffset());

            // We may need to split a fragment's text into its lines, as we
            // need to tell the TextBuilder explicitly when a new line starts.
            Utils.forEachLine(fragmentText, (lineText, isFirstLine) -> {
                if (!isFirstLine) {
                    textBuilder.newLine();
                }
                textBuilder.addHighlightedText(lineText, color);
            });
        }
    }

    public void addLegend(TextBuilder textBuilder) {
        textBuilder.addText("Color codes for Fragment.Kind:");

        textBuilder.beginTable();

        addKindForLegend(textBuilder, SCALAR);
        textBuilder.newLine();
        addKindForLegend(textBuilder, SCALAR_VALUE);

        textBuilder.newTableCell();
        addKindForLegend(textBuilder, SEQUENCE);
        textBuilder.newLine();
        addKindForLegend(textBuilder, SEQUENCE_ITEM);

        textBuilder.newTableCell();
        addKindForLegend(textBuilder, DOCUMENT_START);
        textBuilder.newLine();
        addKindForLegend(textBuilder, DOCUMENT_END);

        textBuilder.newTableRow();
        addKindForLegend(textBuilder, MAP);
        textBuilder.newLine();
        addKindForLegend(textBuilder, MAP_KEY);
        textBuilder.newLine();
        addKindForLegend(textBuilder, MAP_VALUE);

        textBuilder.newTableCell();
        addKindForLegend(textBuilder, ALIAS_AS_SEQUENCE_ITEM);
        textBuilder.newLine();
        addKindForLegend(textBuilder, ALIAS_AS_MAP_KEY);
        textBuilder.newLine();
        addKindForLegend(textBuilder, ALIAS_AS_MAP_VALUE);

        textBuilder.endTable();
    }

    private void addKindForLegend(TextBuilder textBuilder, Kind kind) {
        textBuilder.addHighlightedText(kind.name(), colorScheme.getColor(kind));
    }
}
