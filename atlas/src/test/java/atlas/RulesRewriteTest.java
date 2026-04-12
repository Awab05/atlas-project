package atlas;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
}
