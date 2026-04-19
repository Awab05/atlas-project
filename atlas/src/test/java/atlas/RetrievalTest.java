package atlas;

import atlas.Mapping.AtlasMapper;
import atlas.Retrieval.AtlasRetriever;
import atlas.Retrieval.ConceptualLoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RetrievalTest {

    private AtlasRetriever retriever;

    @BeforeEach
    void setUp() throws IOException {
        ConceptualLoad loader = new ConceptualLoad();
        AtlasMapper mapper = new AtlasMapper();
        retriever = new AtlasRetriever(loader, mapper);
    }

    // -------------------------Story 4.3 Tests----------------------------

    @Test
    void testGetSourceConceptsReturnsValidSourcesForPriest() {
        HashSet<String> sources = retriever.getSourceConcepts("priest");

        assertTrue(sources.contains("teacher"));
        assertTrue(sources.contains("soldier"));
        assertTrue(sources.contains("doctor"));
        assertTrue(sources.contains("artist"));
        assertTrue(sources.contains("captain"));

        assertFalse(sources.contains("rival"));
        assertFalse(sources.contains("priest"));
    }

    @Test
    void testGetSourceConceptsDoesNotIncludeTargetTopic() {
        HashSet<String> sources = retriever.getSourceConcepts("priest");

        assertFalse(sources.contains("priest"));
    }

    @Test
    void testGetSourceConceptsEmptyWhenNoMatches() {
        HashSet<String> sources = retriever.getSourceConcepts("rival");

        assertTrue(sources.isEmpty());
    }

    // ------------------------Story 4.4 Tests-----------------------

    @Test
    void testRankSourceConceptsContainsOnlyValidSources() {
        List<String> ranked = retriever.rankSourceConcepts("priest");

        assertTrue(ranked.contains("teacher"));
        assertTrue(ranked.contains("soldier"));
        assertTrue(ranked.contains("doctor"));
        assertTrue(ranked.contains("artist"));
        assertTrue(ranked.contains("captain"));

        assertFalse(ranked.contains("rival"));
        assertFalse(ranked.contains("priest"));
    }

    @Test
    void testRankSourceConceptsPutsRicherAnalogiesAboveWeakerOnes() {
        List<String> ranked = retriever.rankSourceConcepts("priest");

        int teacherIndex = ranked.indexOf("teacher");
        int soldierIndex = ranked.indexOf("soldier");
        int doctorIndex = ranked.indexOf("doctor");
        int artistIndex = ranked.indexOf("artist");
        int captainIndex = ranked.indexOf("captain");

        assertTrue(teacherIndex < artistIndex);
        assertTrue(teacherIndex < captainIndex);

        assertTrue(soldierIndex < artistIndex);
        assertTrue(soldierIndex < captainIndex);

        assertTrue(doctorIndex < artistIndex);
        assertTrue(doctorIndex < captainIndex);
    }

    @Test
    void testRankSourceConceptsReturnsEmptyWhenNoSourcesExist() {
        List<String> ranked = retriever.rankSourceConcepts("rival");

        assertTrue(ranked.isEmpty());
    }
}