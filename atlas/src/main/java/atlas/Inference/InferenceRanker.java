package atlas.Inference;

import java.util.HashMap;
import java.util.List;

import atlas.AtlasNode;
import atlas.Mapping.AtlasMapper;
import atlas.Retrieval.ConceptualLoad;

public class InferenceRanker {

    private final ConceptualLoad loader;
    private final AtlasMapper mapper;
    private static final double BETA = 3.0, ALPHA = 1.0; // parameters for quality calculation based on the paper

    public InferenceRanker(ConceptualLoad loader, AtlasMapper mapper) {
        this.loader = loader;
        this.mapper = mapper;
    }

    public List<CandidateInferenceCoalescer.CoalescedInference> rank(List<CandidateInferenceCoalescer.CoalescedInference> inferences, String target, String source) {

        if (inferences == null || target == null || source == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        HashMap<CandidateInferenceCoalescer.CoalescedInference, Double> scores = new HashMap<>();
        for (var inf : inferences) {
            scores.put(inf, calculateQuality(inf, target, source));
        }

        inferences.sort((a, b) -> Double.compare(scores.get(b), scores.get(a)));
        return inferences;
    }

    public double getQuality(CandidateInferenceCoalescer.CoalescedInference inf, String target, String source) {
        if (inf == null) throw new IllegalArgumentException("CoalescedInference is null");
        return calculateQuality(inf, target, source);
    }

    private double calculateQuality(CandidateInferenceCoalescer.CoalescedInference c, String target, String source) {
        double quality = 0.0;

        for (AtlasNode src : loader.retrieveStructures(source)) {
            for (AtlasNode tgt : loader.retrieveStructures(target)) {
                if (mapper.isMappable(src, tgt)) {
                    quality += Math.pow(calculateRichness(src), BETA);
                }
            }
        }

        for (AtlasNode inf : c.getCoalescedInferences()) {
            quality += ALPHA * Math.pow(calculateRichness(inf), BETA);
        }

        return quality;
    }

    private double calculateRichness(AtlasNode s) {
        int maxDepth = getDepth(s);
        double sum = 0.0;
        for (int i = 0; i <= maxDepth; i++) {
            sum += countAtDepth(s, i) * Math.pow(10, i);
        }
        return Math.log10(sum);
    }

    private int getDepth(AtlasNode s) {
        int max = 0;
        for (Object c : s.getChildren()) {
            if (c instanceof AtlasNode) {
                max = Math.max(max, 1 + getDepth((AtlasNode) c));
            }
        }
        return max;
    }

    private int countAtDepth(AtlasNode s, int d) {
        if (d == 0) return 1;
        int cnt = 0;
        for (Object c : s.getChildren()) {
            if (c instanceof AtlasNode) {
                cnt += countAtDepth((AtlasNode) c, d - 1);
            }
        }
        return cnt;
    }
}

