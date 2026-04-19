
package atlas;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import atlas.Mapping.AtlasMapper;
import atlas.Retrieval.AtlasRetriever;
import atlas.Retrieval.ConceptualLoad;

public class RetrievalTest {

    private AtlasRetriever retriever;
    private ConceptualLoad loader;
    private AtlasMapper mapper;

    @BeforeEach
    void setUp() throws IOException {
        loader = new ConceptualLoad();
        mapper = new AtlasMapper();
        retriever = new AtlasRetriever(loader, mapper);
    }
    // ---------User Story 4.1 Tests---------

    @Test
    void testLoadAndIndexKnowledgeBase() {
        // checks that the knowledge base loads and is indexes successfully
        assertNotNull(loader);
        HashMap<String, List<AtlasNode>> index = loader.getTopicRetrievalMap();
        assertNotNull(index);
        assertFalse(index.isEmpty(), "Index should be populated with topics from knowledge base");

        assertTrue(index.containsKey("athlete"), "Index should contain 'athlete' topic");
        assertTrue(index.containsKey("rival"), "Index should contain 'rival' topic");
        assertTrue(index.containsKey("captain"), "Index should contain 'captain' topic");
        assertTrue(index.containsKey("mentor"), "Index should contain 'mentor' topic");
        assertTrue(index.containsKey("student"), "Index should contain 'student' topic");
    
    }

    // ---------User Story 4.2 Tests---------
    @Test
    void testRetrieveStructuresByTopic() {
        
        List<AtlasNode> athleteStructures = loader.retrieveStructures("athlete");
        assertNotNull(athleteStructures);
        assertFalse(athleteStructures.isEmpty(), "Should retrieve structures for topic 'athlete'");
        
       
        for (AtlasNode structure : athleteStructures) {
            assertTrue(containsTopicMarker(structure, "athlete"),
                "Structure should contain topic marker *athlete");
        }
    }

    @Test
    void testRetrieveNonExistentTopicReturnsEmpty() {
        List<AtlasNode> structures = loader.retrieveStructures("nonexistenttopic");
        assertNotNull(structures, "Should return empty list, not null");
        assertTrue(structures.isEmpty(), "Should return empty list for non-existent topic");
    }

    //helper method to check for topics
    private boolean containsTopicMarker(AtlasNode node, String topic) {
        if (node.getLabel().equals("*" + topic)) {
            return true;
        }
        for (Object child : node.getChildren()) {
            if (child instanceof String && child.equals("*" + topic)) {
                return true;
            } else if (child instanceof AtlasNode && containsTopicMarker((AtlasNode) child, topic)) {
                return true;
            }
        }
        return false;
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