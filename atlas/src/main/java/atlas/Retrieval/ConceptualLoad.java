package atlas.Retrieval;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import atlas.AtlasNode;
import atlas.AtlasParser;

public class ConceptualLoad {
    HashMap<String, List<AtlasNode>> topicRetrievalMap;
    
    

    public ConceptualLoad() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("knowledge base.txt");
        if (input == null) {
            throw new IOException();
        }
        this.topicRetrievalMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            AtlasParser parser = new AtlasParser();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                AtlasNode node = parser.parse(line);
                HashSet<String> topics = extractTopics(node);

                if (topics.isEmpty()) {
                    System.out.println("No topics found for node: " + node);
                    continue;
                }

                for (String topic : topics) {
                    topicRetrievalMap.putIfAbsent(topic, new java.util.ArrayList<>());
                    topicRetrievalMap.get(topic).add(node);
                }
            }
        }
    }

    private HashSet<String> extractTopics(AtlasNode node) {
        String label = node.getLabel();
        HashSet<String> topics = new HashSet<>();

        if (!label.isEmpty() && label.charAt(0) == '*') {
            topics.add(label.substring(1));
        }

        for (Object child : node.getChildren()) {
            if (child instanceof String) {
                String childString = (String) child;
                if (!childString.isEmpty() && childString.charAt(0) == '*') {
                    topics.add(childString.substring(1));
                }
            } else if (child instanceof AtlasNode) {
                topics.addAll(extractTopics((AtlasNode) child));
            }
        }
        return topics;
    }

    public List<AtlasNode> retrieveStructures(String topic) {
        if (!topicRetrievalMap.containsKey(topic)) {
            System.err.println("Topic not found: " + topic);
            return new java.util.ArrayList<>();
        }
        return topicRetrievalMap.get(topic);
    }

    public HashMap<String, List<AtlasNode>> getTopicRetrievalMap() {
        return topicRetrievalMap;
    }

}
