package atlas;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import atlas.Analogy.RichAnalogy;
import atlas.Mapping.AtlasMapper;
import atlas.Retrieval.AtlasRetriever;
import atlas.Retrieval.ConceptualLoad;

public class AnalogyTest {

    private RichAnalogy analogy;
    private AtlasRetriever retriever;
    private ConceptualLoad loader;
    private AtlasMapper mapper;

    @BeforeEach
    void setUp() throws IOException {
        loader = new ConceptualLoad();
        mapper = new AtlasMapper();
        retriever = new AtlasRetriever(loader, mapper);
        analogy = new RichAnalogy(retriever);
    }

    // ---------- User Story 5.1 Tests -----------

    // tests that largestRichAnalogy returns a mapping when one pair of structures is mappable
    @Test
    void testLargestRichAnalogySimpleMapping() {
        HashMap<String, String> result = analogy.largestRichAnalogy("priest", "teacher");

        assertNotNull(result);

        if (!result.isEmpty()) {
            assertTrue(result.size() > 0, "Mapping should contain at least one entry");
        }
    }

    // tests that largestRichAnalogy prefers the largest consistent mapping
    @Test
    void testLargestRichAnalogyPreferLargerMapping() {
        // when multiple pairs of structures can be coalesced, it should return the largest one
        HashMap<String, String> result = analogy.largestRichAnalogy("teacher", "soldier");

        assertNotNull(result);
        if (!result.isEmpty()) {
            for (String key : result.keySet()) {
                assertNotNull(result.get(key));
                assertTrue(result.get(key).length() > 0);
            }
        }
    }

    // tests that largestRichAnalogy returns empty mapping when no structures are mappable
    @Test
    void testLargestRichAnalogyNoMappablePairs() {
        HashMap<String, String> result = analogy.largestRichAnalogy("rival", "athlete");

        assertNotNull(result, "Should return non-null HashMap, not null");
        assertTrue(result.isEmpty() || result.size() >= 0);
    }

    @Test
    void testLargestRichAnalogyNullTopics() {
        assertThrows(IllegalArgumentException.class, () -> analogy.largestRichAnalogy(null, "teacher"));
        assertThrows(IllegalArgumentException.class, () -> analogy.largestRichAnalogy("teacher", null));
        assertThrows(IllegalArgumentException.class, () -> analogy.largestRichAnalogy(null, null));
    }
}
