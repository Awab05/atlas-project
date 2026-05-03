package atlas.Inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import atlas.AtlasNode;

public class CandidateInferenceCoalescer {

    public static class CoalescedInference {
        private final String sourceTopic;
        private final String targetTopic;
        private final HashMap<String, String> unifiedMapping;
        private final List<AtlasNode> inferences;

        public CoalescedInference(String source, String target, HashMap<String, String> mapping, List<AtlasNode> infs) {
            this.sourceTopic = source;
            this.targetTopic = target;
            this.unifiedMapping = new HashMap<>(mapping);
            this.inferences = new ArrayList<>(infs);
        }

        public String getSourceTopic() { return sourceTopic; }
        public String getTargetTopic() { return targetTopic; }
        public HashMap<String, String> getUnifiedMapping() { return new HashMap<>(unifiedMapping); }
        public List<AtlasNode> getCoalescedInferences() { return new ArrayList<>(inferences); }
        public int getInferenceCount() { return inferences.size(); }
        public int getMappingSize() { return unifiedMapping.size(); }
    }

    public List<CoalescedInference> coalesce(String target, String source, List<AtlasNode> inferences, HashMap<String, String> baseMapping) {

        if (target == null || source == null || inferences == null || baseMapping == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        List<CoalescedInference> groups = new ArrayList<>();
        List<Boolean> processed = new ArrayList<>();

        for (int i = 0; i < inferences.size(); i++) processed.add(false);

        // group the inferences by structural consistency and create coalesced inferences with unified mappings
        for (int i = 0; i < inferences.size(); i++) {
            if (processed.get(i)) continue;

            List<AtlasNode> group = new ArrayList<>();
            group.add(inferences.get(i));
            processed.set(i, true);

            for (int j = i + 1; j < inferences.size(); j++) {
                if (!processed.get(j) && isConsistent(inferences.get(j), group)) {
                    group.add(inferences.get(j));
                    processed.set(j, true);
                }
            }

            groups.add(new CoalescedInference(source, target, baseMapping, group));
        }

        return groups;
    }

    // helper function to check if the new inference is structurally consistent with a group of inferences for coalescing
    private boolean isConsistent(AtlasNode newInference, List<AtlasNode> group) {
        for (AtlasNode existing : group) {
            if (!haveSameStructure(newInference, existing)) return false;
        }
        return true;
    }


    // helper function to check if two inferences have the same structure for coalescing
    private boolean haveSameStructure(AtlasNode n1, AtlasNode n2) {
        if (!n1.getLabel().equals(n2.getLabel())) return false;

        List<Object> c1 = n1.getChildren(), c2 = n2.getChildren();
        if (c1.size() != c2.size()) return false;

        for (int i = 0; i < c1.size(); i++) {
            Object ch1 = c1.get(i), ch2 = c2.get(i);
            if (ch1 instanceof AtlasNode && ch2 instanceof AtlasNode) {
                if (!haveSameStructure((AtlasNode) ch1, (AtlasNode) ch2)) return false;
            } else if (ch1 instanceof String && ch2 instanceof String) {
                if (!ch1.equals(ch2)) return false;
            } else {
                return false;
            }
        }
        return true;
    }
}
