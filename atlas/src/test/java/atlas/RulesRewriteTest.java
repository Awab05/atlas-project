package atlas;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import atlas.Rules.RewriteRuleLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import atlas.Rules.RewriteStructure;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RulesRewriteTest {
    // -----------------Story 3.1 Tests--------------

    @Test
    void testLoadRulesFileNotEmpty() throws IOException {
        RewriteRuleLoader loader = new RewriteRuleLoader();
        loader.loadRules();

        assertFalse(loader.getRules().isEmpty());
    }

    @Test
    void testLoadSingleRewriteRule() throws IOException {
        RewriteRuleLoader loader = new RewriteRuleLoader();
        loader.loadRules();

        assertTrue(loader.getRules().containsKey("dislike"));
        assertEquals(1, loader.getRules().get("dislike").size());
        assertEquals("!respect_as:friend&disliking", loader.getRules().get("dislike").get(0));
    }

    @Test
    void testLoadMultipleRewriteRules() throws IOException {
        RewriteRuleLoader loader = new RewriteRuleLoader();
        loader.loadRules();

        assertTrue(loader.getRules().containsKey("undermine"));
        assertEquals(2, loader.getRules().get("undermine").size());
        assertEquals("!support_as:leader&undermining", loader.getRules().get("undermine").get(0));
        assertEquals("!respect_as:leader&undermining", loader.getRules().get("undermine").get(1));
    }

    @Test
    void testLoadAnotherMultipleRewriteRule() throws IOException {
        RewriteRuleLoader loader = new RewriteRuleLoader();
        loader.loadRules();

        assertTrue(loader.getRules().containsKey("flunk"));
        assertEquals(2, loader.getRules().get("flunk").size());
        assertEquals("^!respect_for:teacher&flunking", loader.getRules().get("flunk").get(0));
        assertEquals("^reject_for:teacher&flunking", loader.getRules().get("flunk").get(1));
    }

    @Test
    void testKnownPredicateExists() throws IOException {
        RewriteRuleLoader loader = new RewriteRuleLoader();
        loader.loadRules();

        assertTrue(loader.getRules().containsKey("exercise"));
    }


    //----------------story 3.2 tests-------------------------
    private RewriteStructure rewriteStructure;

    @BeforeEach
    public void setUp() {
        HashMap<String, List<String>> rules = new HashMap<>();
        
        rules.put("exercise", List.of("perform_of:exercise*&exercising"));
        rules.put("dislike", List.of("!respect_as:friend&disliking"));
        rules.put("lose_control_over", List.of("<!respect_as:leader&rejecting_control"));
        rules.put("flunk", List.of("^reject_for:teacher&flunking"));
        rules.put("underestimate", List.of("!respect_as:rival&underestimating", "!respect_as:threat&underestimating"));

        rewriteStructure = new RewriteStructure(rules);
    }

    @Test
    public void testBasicRewrite() {
        AtlasNode input = new AtlasNode("exercise");
        input.addChild("*athlete");
        input.addChild("muscle");
        
        AtlasNode result = rewriteStructure.rewrite(input).get(0);
        assertEquals("by", result.getLabel());
        assertEquals("exercising", result.getChildren().get(0));
        
        AtlasNode perform = (AtlasNode) result.getChildren().get(1);
        assertEquals("perform", perform.getLabel());
        assertEquals("*athlete", perform.getChildren().get(0));
        assertEquals("exercise", perform.getChildren().get(1));
        assertEquals("of", ((AtlasNode) perform.getChildren().get(2)).getLabel());
    }

    @Test
    public void testRecursiveRewriting() {
        AtlasNode root = new AtlasNode("if");
        AtlasNode can = new AtlasNode("can");
        AtlasNode exercise = new AtlasNode("exercise");
        exercise.addChild("*athlete");
        exercise.addChild("muscle");
        can.addChild(exercise);
        root.addChild(can);
        
        AtlasNode result = rewriteStructure.rewriteRecursively(root).get(0);
        assertEquals("if", result.getLabel());
        assertEquals("can", ((AtlasNode) result.getChildren().get(0)).getLabel());
        
        AtlasNode byNode = (AtlasNode) ((AtlasNode) result.getChildren().get(0)).getChildren().get(0);
        assertEquals("by", byNode.getLabel());
        assertEquals("exercising", byNode.getChildren().get(0));
    }

    @Test
    public void testCombinedModifiersAndMultipleRules() {
        AtlasNode input = new AtlasNode("lose_control_over");
        input.addChild("*captain");
        input.addChild("mutineer");
        
        AtlasNode byNode = rewriteStructure.rewrite(input).get(0);
        assertEquals("by", byNode.getLabel());
        assertEquals("rejecting_control", byNode.getChildren().get(0));
        
        AtlasNode respectNode = getRespectNode(byNode);
        assertEquals("mutineer", respectNode.getChildren().get(0));
        assertEquals("*captain", respectNode.getChildren().get(1));
        
        AtlasNode underest = new AtlasNode("underestimate");
        underest.addChild("*observer");
        underest.addChild("threat");
        assertEquals(2, rewriteStructure.rewrite(underest).size());
    }

    @Test
    public void testImplicitArgumentExplicit() {
        AtlasNode input = new AtlasNode("flunk");
        input.addChild("*student");
        input.addChild("exam");
        
        AtlasNode byNode = rewriteStructure.rewrite(input).get(0);
        assertEquals("by", byNode.getLabel());
        assertEquals("flunking", byNode.getChildren().get(0));
        
        AtlasNode rejectNode = (AtlasNode) byNode.getChildren().get(1);
        assertEquals("reject", rejectNode.getLabel());
        assertEquals("teacher", rejectNode.getChildren().get(0));
        assertEquals("*student", rejectNode.getChildren().get(1));
        assertEquals("for", ((AtlasNode) rejectNode.getChildren().get(2)).getLabel());
    }

    @Test
    public void testEdgeCasesAndErrors() {
        AtlasNode unknown = new AtlasNode("unknown");
        unknown.addChild("arg");
        assertEquals("unknown", rewriteStructure.rewrite(unknown).get(0).getLabel());
        
        assertThrows(IllegalArgumentException.class, () -> rewriteStructure.rewrite(null));
        assertThrows(IllegalArgumentException.class, () -> rewriteStructure.rewriteRecursively(null));
        
        AtlasNode noChildren = new AtlasNode("dislike");
        AtlasNode result = rewriteStructure.rewrite(noChildren).get(0);
        assertEquals("by", result.getLabel());
        assertEquals("disliking", result.getChildren().get(0));
        assertEquals("not", ((AtlasNode) result.getChildren().get(1)).getLabel());
    }
    
    private AtlasNode getRespectNode(AtlasNode result) {
        AtlasNode notNode = (AtlasNode) result.getChildren().get(1);
        return (AtlasNode) notNode.getChildren().get(0);
    }
}