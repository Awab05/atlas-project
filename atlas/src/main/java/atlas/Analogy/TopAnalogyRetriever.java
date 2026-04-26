package atlas.Analogy;

import atlas.Retrieval.AtlasRetriever;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TopAnalogyRetriever {

    private final AtlasRetriever retriever;
    private final RichAnalogy richAnalogy;

    public TopAnalogyRetriever(AtlasRetriever retriever, RichAnalogy richAnalogy) {
        this.retriever = retriever;
        this.richAnalogy = richAnalogy;
    }

    // firstly we get all the source topics using getSourceConcepts. then we look at each one.
    // we get each one's richest analogy and place its score in a HashMap.
    // we then sort the source topics in descending order using those scores
    // and return the top n topics.
    public List<String> getTopNSourceConcepts(String targetTopic, int n){

        if (targetTopic == null) {
            throw new IllegalArgumentException("Target topic cannot be null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("n cannot be negative");
        }

        HashSet<String> sourceConcepts = retriever.getSourceConcepts(targetTopic);
        HashMap<String, Integer> conceptScores = new HashMap<>();

        for (String sourceConcept : sourceConcepts) {
            HashMap<String, String> bestMapping = richAnalogy.largestRichAnalogy(targetTopic, sourceConcept);
            int score = bestMapping.size();
            conceptScores.put(sourceConcept, score);
        }

        List<String> rankedConcepts = new ArrayList<>(sourceConcepts);
        rankedConcepts.sort((a, b) -> Integer.compare(conceptScores.get(b), conceptScores.get(a)));

        if (n > rankedConcepts.size()) {
            n = rankedConcepts.size();
        }

        List<String> topConcepts = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            topConcepts.add(rankedConcepts.get(i));
        }

        return topConcepts;
    }
}