package atlas;

import java.util.Arrays;
import java.util.List;

public class AtlasParser {
    private List<String> tokens;
    private int idx;

    public AtlasNode parse(String input) {
        String clean = input.replace("(", " ( ").replace(")", " ) ");
        tokens = Arrays.asList(clean.trim().split("\\s+"));
        idx = 0;
        return parseNode();
    }

    private AtlasNode parseNode() {
        idx++; // skip '('
    
        String label = tokens.get(idx++);
        AtlasNode node = new AtlasNode(label);
    
        while (idx < tokens.size() && !tokens.get(idx).equals(")")) {
            if (tokens.get(idx).equals("(")) {
                node.addChild(parseNode()); 
            } else {
                node.addChild(tokens.get(idx++)); 
            }
        }
    
        idx++; // skip ')'
        return node;
    }
}