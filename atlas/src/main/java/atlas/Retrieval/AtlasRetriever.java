package atlas.Retrieval;

import atlas.AtlasNode;
import atlas.Mapping.AtlasMapper;

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

}
