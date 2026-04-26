package atlas.Analogy;

import java.util.HashMap;
import java.util.List;

public class AnalogyRanker {

    private final RichAnalogy richAnalogy;

    public AnalogyRanker(RichAnalogy richAnalogy) {
        this.richAnalogy = richAnalogy;
    }

    // Uses getAllRichAnalogies to retrieve all composite analogies between the target and source
    // topics, then sorts them in descending order of mapping richness to return a ranked list.
    public List<HashMap<String, String>> rankAnalogies(String targetTopic, String sourceTopic){
        if (targetTopic == null || sourceTopic == null) {
            throw new IllegalArgumentException("Topics cannot be null");
        }

        List<HashMap<String, String>> analogies = richAnalogy.getAllRichAnalogies(targetTopic, sourceTopic);
        analogies.sort((a, b) -> Integer.compare(b.size(), a.size()));
        return analogies;
    }
}
