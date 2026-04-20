package atlas.Retrieval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import atlas.AtlasNode;
import atlas.Mapping.AtlasMapper;

public class AtlasRetriever {

    private final ConceptualLoad loader;
    private final AtlasMapper mapper;

    public AtlasRetriever(ConceptualLoad loader, AtlasMapper mapper) {
        this.loader = loader;
        this.mapper = mapper;
    }

    public AtlasMapper getMapper() {
        return mapper;
    }

    public ConceptualLoad getConceptualLoad() {
        return loader;
    }

    // this method takes a target topic and checks all structures related to it
    //it then checks all the other topics and there structures looking to see
    //if any structure is mappable to a target structure. if so then we add it to our result set.
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

    //helper function that checks if 2 lists of structures contain a mappable pair
    //does this by looping through each list and comparing each structure to all the other structures in the other list
    //if 2 structures are mappable, then we return true
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
        //returns a list of source concepts ranked from strongest to weakest based on a target.
    //does this by firstly getting ll valid source concepts using getSourceConcepts
    //then it scores each source concept against the target topic
    //then at the end it sorts them in descending order of score
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


    //helper function that calculates the total richness score between 2 topics
    //does this by comparing every target structure with every source structure
    //if 2 structures are mappable it adds the richness of both structures to the total
    public int totalAnalogyScore(List<AtlasNode> targetStructures, List<AtlasNode> sourceStructures) {
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

    //calculates the richness of a single structure
    //current node counts as 1 point
    //each child string is 1 point and we check nested Atlas nodes recursively
    //this means deeper structures get a higher richness score.
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
