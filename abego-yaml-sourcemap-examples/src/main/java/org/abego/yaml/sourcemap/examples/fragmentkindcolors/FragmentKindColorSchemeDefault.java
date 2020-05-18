package org.abego.yaml.sourcemap.examples.fragmentkindcolors;

import org.abego.yaml.sourcemap.YAMLSourceMap.Fragment.Kind;

import java.awt.Color;

final class FragmentKindColorSchemeDefault implements FragmentKindColorScheme {

    @Override
    public Color getColor(Kind kind) {

        switch (kind) {
            case ALIAS_AS_SEQUENCE_ITEM:
                return new Color(0xCEB3C2);
            case ALIAS_AS_MAP_KEY:
                return new Color(0xF0BFC4);
            case ALIAS_AS_MAP_VALUE:
                return new Color(0xE4A2A6);
            case DOCUMENT_END:
            case DOCUMENT_START:
                return new Color(0xDDDDDD);
            case SCALAR:
                return new Color(0xFFED9C);
            case SCALAR_VALUE:
                return new Color(0xFDD787);
            case SEQUENCE:
                return new Color(0x97E0F5);
            case SEQUENCE_ITEM:
                return new Color(0x69BAE8);
            case MAP:
                return new Color(0xE2F798);
            case MAP_KEY:
                return new Color(0xCFE787);
            case MAP_VALUE:
                return new Color(0xBDD876);
            default:
                return Color.white;
        }
    }
}
