package atlas;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import atlas.Rules.RewriteStructure;

public class RulesRewriteTest {

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