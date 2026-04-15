package atlas.Retrieval;

import atlas.AtlasNode;
import atlas.Mapping.AtlasMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AtlasRetriever {

    private ConceptualLoad loader;
    private AtlasMapper mapper;

    public AtlasRetriever(ConceptualLoad loader, AtlasMapper mapper) {
        this.loader = loader;
        this.mapper = mapper;
    }

    public HashSet<String> getSourceConcepts(String targetTopic) {
        HashSet<String> topics = new HashSet<>();
        List<AtlasNode> targetStructures = loader.retrieveStructures(targetTopic);
        for (String candidateTopic : loader.getTopicRetrievalMap().keySet()) {
            if (candidateTopic.equals(targetTopic)) {
                continue;
            }
            List<AtlasNode> candidateStructures = loader.retrieveStructures(candidateTopic);

            if (hasMappablePair(targetStructures, candidateStructures)) {
                topics.add(candidateTopic);
            }
        }
        return topics;
    }

    private boolean hasMappablePair(List<AtlasNode> targetStructures, List<AtlasNode> candidateStructures) {
        for (AtlasNode targetNode : targetStructures) {
            for (AtlasNode candidateNode : candidateStructures) {
                if (mapper.isMappable(targetNode, candidateNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> rankSourceConcepts(String targetTopic){
        HashSet<String> sourceConcepts = getSourceConcepts(targetTopic);
        List<AtlasNode> targetStructures = loader.retrieveStructures(targetTopic);
        HashMap<String, Integer> conceptScores = new HashMap<>();

        for (String concept : sourceConcepts){
            List<AtlasNode> candidateStructures = loader.retrieveStructures(concept);
            int score = totalAnalogyScore(targetStructures, candidateStructures);
            conceptScores.put(concept, score);
        }

        List<String> rankedConcepts = new java.util.ArrayList<>(sourceConcepts);
        rankedConcepts.sort((a, b) -> Integer.compare(conceptScores.get(b), conceptScores.get(a)));

        return rankedConcepts;
    }

    private int totalAnalogyScore(List<AtlasNode> targetStructures, List<AtlasNode> sourceStructures) {
        int totalScore = 0;

        for (AtlasNode targetNode : targetStructures) {
            for (AtlasNode sourceNode : sourceStructures) {
                if (mapper.isMappable(targetNode, sourceNode)) {
                    totalScore += richnessScore(targetNode) + richnessScore(sourceNode);
                }
            }
        }

        return totalScore;
    }

    private int richnessScore(AtlasNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        int score = 1; // count the current node itself

        for (Object child : node.getChildren()) {
            if (child instanceof String) {
                score++;
            } else if (child instanceof AtlasNode) {
                score += richnessScore((AtlasNode) child);
            }
        }

        return score;
    }
}
