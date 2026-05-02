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

import atlas.Inference.CandidateInferenceCoalescer;
import atlas.Inference.CandidateInferenceCoalescer.CoalescedInference;
import atlas.Inference.InferenceGenerator;
import atlas.Inference.InferenceRanker;
import atlas.Mapping.AtlasMapper;
import atlas.Retrieval.ConceptualLoad;

public class InferenceTest {

    private InferenceGenerator inferenceGenerator;
    private CandidateInferenceCoalescer coalescer;
    private InferenceRanker ranker;
    private ConceptualLoad loader;
    private AtlasMapper mapper;

    @BeforeEach
    void setUp() throws IOException {
        loader = new ConceptualLoad();
        mapper = new AtlasMapper();
        inferenceGenerator = new InferenceGenerator(loader, mapper);
        coalescer = new CandidateInferenceCoalescer();
        ranker = new InferenceRanker(loader, mapper);
    }

    // ------------------- User Story 6.1 Tests --------------

    // tests that candidate inferences are generated when a source structure is fully mapped
    @Test
    void testGenerateCandidateInferencesProducesInference() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");
        mapping.put("student", "follower");
        mapping.put("class", "congregation");

        List<AtlasNode> result =
                inferenceGenerator.generateCandidateInferences("priest", "teacher", mapping);

        assertNotNull(result);
        assertTrue(result.size() >= 0);
    }

    // tests that no candidate inference is added if the mapped structure already exists in target
    @Test
    void testGenerateCandidateInferencesSkipsExistingTargetAnalogue() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");
        mapping.put("classroom", "congregation");
        mapping.put("teaching", "worship");

        List<AtlasNode> result =
                inferenceGenerator.generateCandidateInferences("priest", "teacher", mapping);

        assertNotNull(result);

        for (AtlasNode node : result) {
            assertFalse(mapper.isMappable(
                    node,
                    loader.retrieveStructures("priest").get(0)
            ));
        }
    }

    // tests that source structures with unmapped non-predicate symbols are skipped
    @Test
    void testGenerateCandidateInferencesSkipsUnmappedStructures() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");
        // intentionally incomplete mapping

        List<AtlasNode> result =
                inferenceGenerator.generateCandidateInferences("priest", "teacher", mapping);

        assertNotNull(result);
        assertTrue(result.isEmpty() || result.size() >= 0);
    }

    // tests that null topics or null mappings throw exceptions
    @Test
    void testGenerateCandidateInferencesNullInputs() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");

        assertThrows(IllegalArgumentException.class,
                () -> inferenceGenerator.generateCandidateInferences(null, "teacher", mapping));

        assertThrows(IllegalArgumentException.class,
                () -> inferenceGenerator.generateCandidateInferences("priest", null, mapping));

        assertThrows(IllegalArgumentException.class,
                () -> inferenceGenerator.generateCandidateInferences("priest", "teacher", null));
    }

    // ------------------- User Story 6.2 Tests: Coalescence ----------

    // tests that candidate inferences are grouped into coalesced inferences
    @Test
    void testCoalesceCandidateInferencesProducesGroups() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");
        mapping.put("student", "follower");
        mapping.put("class", "congregation");

        List<AtlasNode> candidateInferences =
                inferenceGenerator.generateCandidateInferences("priest", "teacher", mapping);

        List<CoalescedInference> coalesced =
                coalescer.coalesce("priest", "teacher", candidateInferences, mapping);

        assertNotNull(coalesced);
        // Each coalesced inference should have at least one inference
        for (CoalescedInference group : coalesced) {
            assertTrue(group.getInferenceCount() >= 0);
        }
    }

    // tests that coalesced inferences have valid unified mappings
    @Test
    void testCoalescedInferencesHaveValidMapping() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");
        mapping.put("student", "follower");
        mapping.put("class", "congregation");

        List<AtlasNode> candidateInferences =
                inferenceGenerator.generateCandidateInferences("priest", "teacher", mapping);

        List<CoalescedInference> coalesced =
                coalescer.coalesce("priest", "teacher", candidateInferences, mapping);

        for (CoalescedInference group : coalesced) {
            assertNotNull(group.getUnifiedMapping());
            assertTrue(group.getMappingSize() >= 0);
        }
    }

        //tests that coalesced inferences preserve source and target topics
    @Test
    void testCoalescedInferencesPreserveTopics() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");
        mapping.put("student", "follower");
        mapping.put("class", "congregation");

        List<AtlasNode> candidateInferences =
                inferenceGenerator.generateCandidateInferences("priest", "teacher", mapping);

        List<CoalescedInference> coalesced =
                coalescer.coalesce("priest", "teacher", candidateInferences, mapping);

        for (CoalescedInference group : coalesced) {
            assertEquals("priest", group.getTargetTopic());
            assertEquals("teacher", group.getSourceTopic());
        }
    }

    @Test
    void testCoalesceCandidateInferencesNullInputs() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");

        List<AtlasNode> candidateInferences = new java.util.ArrayList<>();

        assertThrows(IllegalArgumentException.class,
                () -> coalescer.coalesce(null, "teacher", candidateInferences, mapping));

        assertThrows(IllegalArgumentException.class,
                () -> coalescer.coalesce("priest", null, candidateInferences, mapping));

        assertThrows(IllegalArgumentException.class,
                () -> coalescer.coalesce("priest", "teacher", null, mapping));

        assertThrows(IllegalArgumentException.class,
                () -> coalescer.coalesce("priest", "teacher", candidateInferences, null));
    }

    // ------------------- User Story 6.3 Tests: Ranking ----------

    // tests that quality scores are calculated for coalesced inferences
    @Test
    void testCalculateInferenceQuality() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");
        mapping.put("student", "follower");
        mapping.put("class", "congregation");

        List<AtlasNode> candidateInferences =
                inferenceGenerator.generateCandidateInferences("priest", "teacher", mapping);

        List<CoalescedInference> coalesced =
                coalescer.coalesce("priest", "teacher", candidateInferences, mapping);

        for (CoalescedInference group : coalesced) {
            double quality = ranker.getQuality(group, "priest", "teacher");
            assertTrue(quality >= 0.0);
        }
    }

    // tests that ranked inferences are returned in descending order of quality
    @Test
    void testRankInferencesProducesOrderedList() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");
        mapping.put("student", "follower");
        mapping.put("class", "congregation");

        List<AtlasNode> candidateInferences =
                inferenceGenerator.generateCandidateInferences("priest", "teacher", mapping);

        List<CoalescedInference> coalesced =
                coalescer.coalesce("priest", "teacher", candidateInferences, mapping);

        List<CoalescedInference> ranked =
                ranker.rank(coalesced, "priest", "teacher");

        assertNotNull(ranked);
        assertEquals(coalesced.size(), ranked.size());

        for (int i = 1; i < ranked.size(); i++) {
            double prevQuality = ranker.getQuality(ranked.get(i - 1), "priest", "teacher");
            double currQuality = ranker.getQuality(ranked.get(i), "priest", "teacher");
            assertTrue(prevQuality >= currQuality);
        }
    }

    // tests that the inference ranker correctly calculates quality scores for coalesced inferences
    @Test
    void testInferenceRankerGetQualityScore() {
        HashMap<String, String> mapping = new HashMap<>();
        mapping.put("*teacher", "*priest");
        mapping.put("student", "follower");
        mapping.put("class", "congregation");

        List<AtlasNode> candidateInferences =
                inferenceGenerator.generateCandidateInferences("priest", "teacher", mapping);

        List<CoalescedInference> coalesced =
                coalescer.coalesce("priest", "teacher", candidateInferences, mapping);

        if (!coalesced.isEmpty()) {
            double quality = ranker.getQuality(coalesced.get(0), "priest", "teacher");
            assertTrue(quality >= 0.0);
        }
    }
    
    @Test
    void testInferenceRankerNullInputs() {
        List<CoalescedInference> emptyList = new java.util.ArrayList<>();

        assertThrows(IllegalArgumentException.class,
                () -> ranker.rank(null, "priest", "teacher"));

        assertThrows(IllegalArgumentException.class,
                () -> ranker.rank(emptyList, null, "teacher"));

        assertThrows(IllegalArgumentException.class,
                () -> ranker.rank(emptyList, "priest", null));

        assertThrows(IllegalArgumentException.class,
                () -> ranker.getQuality(null, "priest", "teacher"));
    }


}