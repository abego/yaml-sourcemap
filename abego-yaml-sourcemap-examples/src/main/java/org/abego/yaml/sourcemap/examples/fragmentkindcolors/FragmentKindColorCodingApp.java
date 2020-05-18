package org.abego.yaml.sourcemap.examples.fragmentkindcolors;

import javax.swing.SwingUtilities;

public final class FragmentKindColorCodingApp {
    public static void main(String[] args) {
        new FragmentKindColorCodingApp().run();
    }

    /**
     * A Yaml example based on the "Example 2.27. Invoice" of the Yaml 1.2 Spec
     *
     * <p>https://yaml.org/spec/1.2/spec.html</p>
     */
    private static String sampleInvoiceYaml() {
        return "" +
                "---\n" +
                "invoice: 34843\n" +
                "date   : 2001-01-23\n" +
                "bill-to: &id001\n" +
                "    given  : Chris\n" +
                "    family : Dumars\n" +
                "    address:\n" +
                "        lines: |\n" +
                "            458 Walkman Dr.\n" +
                "            Suite #292\n" +
                "        city    : Royal Oak\n" +
                "        state   : MI\n" +
                "        postal  : 48046\n" +
                "    phones:\n" +
                "        - 202-555-0120\n" +
                "        - 202-555-1432\n" +
                "        - 202-555-5362\n" +
                "ship-to: *id001\n" +
                "product:\n" +
                "    - sku         : BL394D\n" +
                "      quantity    : 4\n" +
                "      description : Basketball\n" +
                "      price       : 450.00\n" +
                "    - sku         : BL4438H\n" +
                "      quantity    : 1\n" +
                "      description : Super Hoop\n" +
                "      price       : 2392.00\n" +
                "tax  : 251.42\n" +
                "total: 4443.52\n" +
                "comments:\n" +
                "    Late afternoon is best.\n" +
                "    Backup contact is Nancy\n" +
                "    Billsmer @ 338-4338.\n" +
                "...\n";
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            FragmentKindColorCodingWindow wnd = new FragmentKindColorCodingWindow();

            // display the window centered on the screen
            wnd.setLocationRelativeTo(null);
            wnd.setVisible(true);

            // Display some nice sample text
            String yamlText = sampleInvoiceYaml();
            wnd.setYaml(yamlText);
        });
    }

}
