package atlas.Analogy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atlas.AtlasNode;
import atlas.Retrieval.AtlasRetriever;

public class RichAnalogy {
    private final AtlasRetriever retriever;
    
    public RichAnalogy(AtlasRetriever retriever) {
        this.retriever = retriever;
    }

    private class MappingPair {
        AtlasNode targetNode;
        AtlasNode sourceNode;
        HashMap<String, String> mapping;
        
        MappingPair(AtlasNode targetNode, AtlasNode sourceNode, HashMap<String, String> mapping) {
            this.targetNode = targetNode;
            this.sourceNode = sourceNode;
            this.mapping = mapping;
        }
    }

    // produces the largest consistent mapping between two topics by coalescing 
    // all possible combinations of mappable structures from source to target
     
    public HashMap<String, String> largestRichAnalogy(String targetTopic, String sourceTopic) {
        if (targetTopic == null || sourceTopic == null) {
            throw new IllegalArgumentException("Topics cannot be null");
        }

        List<AtlasNode> targetStructures = retriever.getConceptualLoad().retrieveStructures(targetTopic);
        List<AtlasNode> sourceStructures = retriever.getConceptualLoad().retrieveStructures(sourceTopic);

        if (targetStructures.isEmpty() || sourceStructures.isEmpty()) {
            return new HashMap<>();
        }
        
        List<MappingPair> mappablePairs = findAllMappablePairs(targetStructures, sourceStructures);
        if (mappablePairs.isEmpty()) {
            return new HashMap<>(); // if theres no mappable pairs, return an empty mapping
        }
        
        HashMap<String, String> largestMapping = new HashMap<>();
        List<List<Integer>> allSubsets = generateSubsets(mappablePairs.size());
        
        for (List<Integer> subset : allSubsets) {
            HashMap<String, String> combinedMapping = new HashMap<>();
            boolean isConsistent = true;
            
            for (int index : subset) {
                HashMap<String, String> pairMapping = mappablePairs.get(index).mapping;
                
                if (!isMappingConsistent(combinedMapping, pairMapping)) {
                    isConsistent = false;
                    break; // if we find an inconsistency, we can stop checking this subset
                }
                combinedMapping.putAll(pairMapping);
            }
            
            if (isConsistent && combinedMapping.size() > largestMapping.size()) {
                largestMapping = new HashMap<>(combinedMapping); // updates the largest mapping if this one is larger
            } 
        }
        
        return largestMapping;
    }
    
    // finds all mappable pairs of (targetNode, sourceNode) by evaluating each
    // target structure against each source structure.
     
    private List<MappingPair> findAllMappablePairs(List<AtlasNode> targetStructures, List<AtlasNode> sourceStructures) {
        List<MappingPair> pairs = new ArrayList<>();
        
        for (AtlasNode targetNode : targetStructures) {
            for (AtlasNode sourceNode : sourceStructures) {
                if (retriever.getMapper().isMappable(targetNode, sourceNode)) {
                    HashMap<String, String> mapping = retriever.getMapper().getMappings(targetNode, sourceNode);
                    pairs.add(new MappingPair(targetNode, sourceNode, mapping));
                }
            }
        }
        
        return pairs;
    }
    
    // checks if two mappings can be combined without conflict, meaning that
    // if a key exists in both mappings, it must map to the exact same value
    private boolean isMappingConsistent(HashMap<String, String> map1, HashMap<String, String> map2) {
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            if (map2.containsKey(entry.getKey())) {
                if (!map2.get(entry.getKey()).equals(entry.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }
    
    // generates all non empty subsets of indices for a given size.
    private List<List<Integer>> generateSubsets(int size) {
        List<List<Integer>> result = new ArrayList<>();
        generateSubsetsHelper(0, size, new ArrayList<>(), result);
        return result;
    }
    
    private void generateSubsetsHelper(int start, int size, List<Integer> current, List<List<Integer>> result) {
        if (start == size) {
            if (!current.isEmpty()) {
                result.add(new ArrayList<>(current));
            }
            return;
        }
        
        current.add(start);
        generateSubsetsHelper(start + 1, size, current, result);
        
        current.remove(current.size() - 1);
        generateSubsetsHelper(start + 1, size, current, result);
    }
}