package atlas.Inference;

import atlas.AtlasNode;
import atlas.Mapping.AtlasMapper;
import atlas.Retrieval.ConceptualLoad;

import java.util.HashMap;
import java.util.List;

public class InferenceGenerator {

    private final ConceptualLoad loader;
    private final AtlasMapper mapper;

    public InferenceGenerator(ConceptualLoad loader, AtlasMapper mapper) {
        this.loader = loader;
        this.mapper = mapper;
    }

    // main method for generating candidate inferences.
    // firstly we get the source and target structures.
    // then for each source structure, if all the non-predicate symbols are mapped,
    // we build the inferred version and add it if it doesnt already exist in the target.
    public List<AtlasNode> generateCandidateInferences(
            String targetTopic,
            String sourceTopic,
            HashMap<String, String> compositeMapping) {

        if (targetTopic == null || sourceTopic == null) {
            throw new IllegalArgumentException("targetTopic or sourceTopic is null");
        }

        if (compositeMapping == null) {
            throw new IllegalArgumentException("compositeMapping is null");
        }

        List<AtlasNode> sourceStructures = loader.retrieveStructures(sourceTopic);
        List<AtlasNode> targetStructures = loader.retrieveStructures(targetTopic);
        List<AtlasNode> candidateInferences = new java.util.ArrayList<>();

        for (AtlasNode sourceStructure : sourceStructures) {
            if (isFullyMapped(sourceStructure, compositeMapping)) {
                AtlasNode mappedInference = applyMapping(sourceStructure, compositeMapping);

                if (!alreadyExistsInTarget(mappedInference, targetStructures)) {
                    candidateInferences.add(mappedInference);
                }
            }
        }

        return candidateInferences;

    }

    // checks if all the non-predicate strings in the source structure are in the composite mapping.
    // if one is missing, then we cant map that structure over properly.
    private boolean isFullyMapped(AtlasNode sourceStructure, HashMap<String, String> compositeMapping) {
        for (Object child : sourceStructure.getChildren()) {
            if (child instanceof String) {
                String symbol = (String) child;
                if (!compositeMapping.containsKey(symbol)) {
                    return false;
                }
            } else if (child instanceof AtlasNode) {
                if (!isFullyMapped((AtlasNode) child, compositeMapping)) {
                    return false;
                }
            }
        }
        return true;
    }

    // applies the composite mapping to the source structure and builds the inferred version.
    // the predicate stays the same, but the non-predicate symbols get replaced using the mapping.
    // if theres a nested structure, we just do the same thing again recursively.
    private AtlasNode applyMapping(AtlasNode sourceStructure, HashMap<String, String> compositeMapping) {
        AtlasNode mappedNode = new AtlasNode(sourceStructure.getLabel());

        for (Object child : sourceStructure.getChildren()) {
            if (child instanceof String) {
                String symbol = (String) child;
                mappedNode.addChild(compositeMapping.get(symbol));
            } else if (child instanceof AtlasNode) {
                mappedNode.addChild(applyMapping((AtlasNode) child, compositeMapping));
            }
        }

        return mappedNode;
    }

    // checks if the inferred structure already matches something in the target topic.
    // if it does, then we dont keep it as a candidate inference.
    private boolean alreadyExistsInTarget(AtlasNode mappedInference, List<AtlasNode> targetStructures) {
        for (AtlasNode targetStructure : targetStructures) {
            if (mapper.isMappable(mappedInference, targetStructure)) {
                return true;
            }
        }
        return false;
    }
}