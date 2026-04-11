package atlas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class RewriteRuleLoader {

    private final HashMap<String, List<String>> rules = new HashMap<>();

    public void loadRules() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("rewrite rules.txt");

        if (input == null) {
            throw new IOException("rewrite rules.txt not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;

            //we check line by line, if the line isnt the last line, then we split it into a predicate
            //and a rewrite rule and add them into our map.

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split("\t+", 2);

                if (parts.length < 2) {
                    throw new IOException("Line is invalid");
                }

                String predicate = parts[0].trim();
                String rewritePart = parts[1].trim();

                String[] rewrites = rewritePart.split(",");

                if (!rules.containsKey(predicate)) {
                    rules.put(predicate, new java.util.ArrayList<>());
                }

                for (String rewrite : rewrites) {
                    rules.get(predicate).add(rewrite.trim());
                }

        }

    }
}

    public HashMap<String, List<String>> getRules() {
        return rules;
    }
}