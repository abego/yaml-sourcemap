package org.abego.yaml.sourcemap.examples.fragmentkindcolors;

import org.abego.yaml.sourcemap.YAMLSourceMap;

import java.awt.Color;

interface FragmentKindColorScheme {
    Color getColor(YAMLSourceMap.Fragment.Kind kind);
}
