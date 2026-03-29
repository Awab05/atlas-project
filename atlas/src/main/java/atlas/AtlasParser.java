package atlas;

import java.util.Arrays;
import java.util.List;

public class AtlasParser {
    private List<String> tokens;
    private int idx;

    public AtlasNode parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }
        String clean = input.replace("(", " ( ").replace(")", " ) ");
        tokens = Arrays.asList(clean.trim().split("\\s+"));
        idx = 0;
        return parseNode();
    }

    private AtlasNode parseNode() {
        if (idx >= tokens.size()) {
            throw new IllegalArgumentException("unexpected end of input: should be '('");
        }
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

        if (idx >= tokens.size() || !tokens.get(idx).equals(")")) {
            throw new IllegalArgumentException("Missing closing parenthesis for node: " + label);
        }
        idx++; // skip ')'
        return node;
    }
}