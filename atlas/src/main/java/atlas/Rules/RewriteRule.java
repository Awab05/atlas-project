package atlas.Rules;

import java.util.ArrayList;
import java.util.List;

public class RewriteRule {
    private String originalPredicate;
    private List<RewriteComponent> components = new ArrayList<>();

    public static class RewriteComponent {
        public final boolean isNegated; // starts with "!"
        public final boolean argsSwapped; // starts with "<"
        public final boolean implicitArgMadeExplicit; // starts with "^"
        public final String verb;
        public final String preposition;
        public final String argument;
        public final boolean argumentSwapsPosition; // argument ends with "*"
        public final String gerund; // specified after "&"

        public RewriteComponent(boolean isNegated, boolean argsSwapped, boolean implicitArgMadeExplicit, String verb, String preposition, String argument, boolean argumentSwapsPosition, String gerund) {
            this.isNegated = isNegated;
            this.argsSwapped = argsSwapped;
            this.implicitArgMadeExplicit = implicitArgMadeExplicit;
            this.verb = verb;
            this.preposition = preposition;
            this.argument = argument;
            this.argumentSwapsPosition = argumentSwapsPosition;
            this.gerund = gerund;
        }
    }

    public RewriteRule(String originalPredicate, String rewriteRuleString) {
        this.originalPredicate = originalPredicate;
        parseRewriteRule(rewriteRuleString);
    }

    private void parseRewriteRule(String rule) {
        rule = rule.trim();

        boolean isNegated = rule.startsWith("!");
        if (isNegated) {
            rule = rule.substring(1);
        }

        boolean argsSwapped = rule.startsWith("<");
        if (argsSwapped) {
            rule = rule.substring(1);
        }

        boolean implicitArgMadeExplicit = rule.startsWith("^");
        if (implicitArgMadeExplicit) {
            rule = rule.substring(1);
        }

        if (rule.startsWith("!")) {
            isNegated = true;
            rule = rule.substring(1);
        }

        String[] parts = rule.split("&");
        String gerund = "";
        if (parts.length > 1) {
            gerund = parts[1].trim();
        }
        String verbPart = parts[0].trim();

        String[] verbPrepositionParts = verbPart.split(":");
        String verbPreposition = verbPrepositionParts[0];
        String argument = "";
        if (verbPrepositionParts.length > 1) {
            argument = verbPrepositionParts[1];
        }

        boolean argumentSwapsPosition = argument.endsWith("*");
        argument = argument.replace("*", "");

        int lastUnderscore = verbPreposition.lastIndexOf("_");
        String verb;
        String preposition;
        
        if (lastUnderscore > 0) {
            verb = verbPreposition.substring(0, lastUnderscore);
            preposition = verbPreposition.substring(lastUnderscore + 1);
        } else {
            verb = verbPreposition;
            preposition = "";
        }

        components.add(new RewriteComponent(isNegated, argsSwapped, implicitArgMadeExplicit,verb, preposition, argument, argumentSwapsPosition, gerund));
    }

    public String getOriginalPredicate() {
        return originalPredicate;
    }

    public List<RewriteComponent> getComponents() {
        return components;
    }
}
