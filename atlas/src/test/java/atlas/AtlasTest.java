package atlas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class AtlasTest {
    // ---------Story 1 Tests---------
    @Test
    void testParseSimple() {
        AtlasNode root = new AtlasParser().parse("(serve priest)");
        assertEquals("serve", root.getLabel());
        assertEquals(1, root.getChildren().size());
        assertEquals("priest", root.getChildren().get(0));
    }

    @Test
    void testParseNested() {
        String input = "(serve priest (some congregation (that (perform worship))))";
        AtlasNode root = new AtlasParser().parse(input);
        assertEquals("serve", root.getLabel());
        assertEquals("priest", root.getChildren().get(0));
        assertTrue(root.getChildren().get(1) instanceof AtlasNode);
    }

    @Test
    void testParseDeep() {
        String input = "(work in scientist (some lab (that (conduct experiment))))";
        AtlasNode root = new AtlasParser().parse(input);
        assertEquals("work", root.getLabel());
        assertEquals(3, root.getChildren().size());

        AtlasNode level1 = (AtlasNode) root.getChildren().get(2);
        AtlasNode level2 = (AtlasNode) level1.getChildren().get(1);
        AtlasNode level3 = (AtlasNode) level2.getChildren().get(0);
        assertEquals("conduct", level3.getLabel());
    }
    // ---------Story 2 Tests---------

    @Test
    void testFlatString() {
        String input = "(serve priest (some congregation (that (perform worship))))";
        AtlasNode root = new AtlasParser().parse(input);
        String result = new AtlasPrinter().AtlasToFlatString(root);
        assertEquals(input, result);
    }

    @Test
    void testPrettyString() {
        String input = "(serve priest (some congregation (that (perform worship))))";
        AtlasNode root = new AtlasParser().parse(input);
        String pretty = new AtlasPrinter().toPrettyString(root);

        assertTrue(pretty.contains("\n"));
        assertTrue(pretty.contains("(some"));
        assertTrue(pretty.contains("  (some"));
        assertTrue(pretty.contains("    (that"));
    }

    @Test
    void testRoundTrip() {
        String[] inputs = {
                "(serve priest)",
                "(work in scientist (some lab (that (conduct experiment))))"
        };

        AtlasParser parser = new AtlasParser();
        AtlasPrinter printer = new AtlasPrinter();

        for (String input : inputs) {
            AtlasNode tree = parser.parse(input);
            String flat = printer.AtlasToFlatString(tree);
            assertEquals(input, flat);

            AtlasNode tree2 = parser.parse(flat);
            String flat2 = printer.AtlasToFlatString(tree2);
            assertEquals(flat, flat2);
        }
    }


    // --------------User Story 3 Unit Tests ------------------
    @Test
    void testAbstractSimple() {
        String input = "(serve priest)";
        AtlasNode root = new AtlasParser().parse(input);
        AtlasNode abstracted = new AtlasAbstractor().abstractTree(root);

        assertEquals("serve", abstracted.getLabel());
        assertEquals(1, abstracted.getChildren().size());
        assertEquals("0", abstracted.getChildren().get(0));
    }

    @Test
    void testAbstractNested() {
        String input = "(serve priest (some congregation (that (perform worship))))";
        AtlasNode root = new AtlasParser().parse(input);
        AtlasNode abstracted = new AtlasAbstractor().abstractTree(root);

        assertEquals("(serve 0 (some 1 (that (perform 2))))",
                new AtlasPrinter().AtlasToFlatString(abstracted));
    }

    @Test
    void testAbstractPrettyString() {
        String input = "(serve priest (some congregation (that (perform worship))))";
        AtlasNode root = new AtlasParser().parse(input);
        AtlasNode abstracted = new AtlasAbstractor().abstractTree(root);
        String pretty = new AtlasPrinter().toPrettyString(abstracted);

        assertTrue(pretty.contains("\n"));
        assertTrue(pretty.contains("(serve 0"));
        assertTrue(pretty.contains("  (some 1"));
        assertTrue(pretty.contains("    (that"));
        assertTrue(pretty.contains("(perform 2)"));
    }
}