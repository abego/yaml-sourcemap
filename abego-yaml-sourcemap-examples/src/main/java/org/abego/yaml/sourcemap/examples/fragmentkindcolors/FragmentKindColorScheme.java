package org.abego.yaml.sourcemap.examples.fragmentkindcolors;

import org.abego.yaml.sourcemap.FragmentsAPI.Fragment;

import java.awt.Color;

interface FragmentKindColorScheme {
    Color getColor(Fragment.Kind kind);
}
