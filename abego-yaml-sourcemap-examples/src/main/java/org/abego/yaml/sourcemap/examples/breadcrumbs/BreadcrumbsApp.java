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

import javax.swing.SwingUtilities;

public final class BreadcrumbsApp {
    public static void main(String[] args) {
        new BreadcrumbsApp().run();
    }

    /**
     * A YAML example based on the "Example 2.27. Invoice" of the Yaml 1.2 Spec
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
                "    Billsmer @ 338-4338.";
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            BreadcrumbsDemoWindow wnd = new BreadcrumbsDemoWindow();

            // display the window centered on the screen
            wnd.setLocationRelativeTo(null);
            wnd.setVisible(true);

            // Display some nice sample text
            String yamlText = sampleInvoiceYaml();
            wnd.setContentDescriptorAndYaml("Invoice 2.27.", yamlText);
        });
    }

}
