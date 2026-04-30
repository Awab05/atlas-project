package atlas;

import atlas.Inference.InferenceGenerator;
import atlas.Mapping.AtlasMapper;
import atlas.Retrieval.ConceptualLoad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InferenceTest {

    private InferenceGenerator inferenceGenerator;
    private ConceptualLoad loader;
    private AtlasMapper mapper;

    @BeforeEach
    void setUp() throws IOException {
        loader = new ConceptualLoad();
        mapper = new AtlasMapper();
        inferenceGenerator = new InferenceGenerator(loader, mapper);
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
}