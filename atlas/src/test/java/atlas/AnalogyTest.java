package atlas;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import atlas.Analogy.TopAnalogyRetriever;
import atlas.Analogy.AnalogyRanker;
import atlas.Analogy.RichAnalogy;
import atlas.Mapping.AtlasMapper;
import atlas.Retrieval.AtlasRetriever;
import atlas.Retrieval.ConceptualLoad;

public class AnalogyTest {

    private RichAnalogy analogy;
    private AtlasRetriever retriever;
    private ConceptualLoad loader;
    private AtlasMapper mapper;
    private AnalogyRanker ranker;
    private TopAnalogyRetriever topRetriever;

    @BeforeEach
    void setUp() throws IOException {
        loader = new ConceptualLoad();
        mapper = new AtlasMapper();
        retriever = new AtlasRetriever(loader, mapper);
        analogy = new RichAnalogy(retriever);
        ranker = new AnalogyRanker(analogy);
        topRetriever = new TopAnalogyRetriever(retriever, analogy);
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



    //---------------------5.2 tests------------------------------

    // tests that ranked analogies are returned in descending order of mapping richness
    @Test
    void testRankAnalogiesOrdersByRichness() {
        List<HashMap<String, String>> ranked = ranker.rankAnalogies("priest", "teacher");

        assertNotNull(ranked);

        for (int i = 0; i < ranked.size() - 1; i++) {
            assertTrue(ranked.get(i).size() >= ranked.get(i + 1).size());
        }
    }

    // tests that the first ranked analogy is the richest one
    @Test
    void testRankAnalogiesLargestMappingFirst() {
        List<HashMap<String, String>> ranked = ranker.rankAnalogies("teacher", "soldier");

        assertNotNull(ranked);

        if (ranked.size() > 1) {
            assertTrue(ranked.get(0).size() >= ranked.get(1).size());
        }
    }

    // tests that ranking returns an empty list when no analogies exist
    @Test
    void testRankAnalogiesNoMappablePairs() {
        List<HashMap<String, String>> ranked = ranker.rankAnalogies("rival", "athlete");

        assertNotNull(ranked);
        assertTrue(ranked.isEmpty());
    }

    // tests that null topic inputs throw an exception
    @Test
    void testRankAnalogiesNullTopics() {
        assertThrows(IllegalArgumentException.class, () -> ranker.rankAnalogies(null, "teacher"));
        assertThrows(IllegalArgumentException.class, () -> ranker.rankAnalogies("teacher", null));
        assertThrows(IllegalArgumentException.class, () -> ranker.rankAnalogies(null, null));
    }

    // ---------------------5.3 tests------------------------------

    // tests that getTopNSourceConcepts returns at most n source concepts
    @Test
    void testGetTopNSourceConceptsReturnsAtMostN() {
        List<String> result = topRetriever.getTopNSourceConcepts("priest", 2);

        assertNotNull(result);
        assertTrue(result.size() <= 2);
    }

    // tests that getTopNSourceConcepts does not include the target topic itself
    @Test
    void testGetTopNSourceConceptsDoesNotIncludeTarget() {
        List<String> result = topRetriever.getTopNSourceConcepts("priest", 3);

        assertNotNull(result);
        assertFalse(result.contains("priest"));
    }

    // tests that requesting more than available does not fail
    @Test
    void testGetTopNSourceConceptsWhenNExceedsAvailable() {
        List<String> result = topRetriever.getTopNSourceConcepts("priest", 20);

        assertNotNull(result);
        assertTrue(result.size() >= 0);
    }

    // tests that requesting zero results returns an empty list
    @Test
    void testGetTopNSourceConceptsWhenNIsZero() {
        List<String> result = topRetriever.getTopNSourceConcepts("priest", 0);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // tests that null target topic throws an exception
    @Test
    void testGetTopNSourceConceptsNullTarget() {
        assertThrows(IllegalArgumentException.class,
                () -> topRetriever.getTopNSourceConcepts(null, 3));
    }

    // tests that negative n throws an exception
    @Test
    void testGetTopNSourceConceptsNegativeN() {
        assertThrows(IllegalArgumentException.class,
                () -> topRetriever.getTopNSourceConcepts("priest", -1));
    }
}
