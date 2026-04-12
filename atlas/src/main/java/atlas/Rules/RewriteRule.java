package atlas.Rules;

public class RewriteRule {
    private final String originalPredicate;
    private final RewriteComponent component;

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

    public RewriteRule(String originalPredicate, String ruleString) {
        this.originalPredicate = originalPredicate;
        this.component = parseRule(ruleString);
    }

    private RewriteComponent parseRule(String rule) {
        rule = rule.trim();

        boolean isNegated = false;
        boolean argsSwapped = false;
        boolean implicitArgMadeExplicit = false;
        boolean changed = true;

        // check for modifier flags: !, <, ^
        while (changed && !rule.isEmpty()) {
            changed = false;

            if (rule.startsWith("!")) {
                isNegated = true;
                rule = rule.substring(1);
                changed = true;
            }
            if (rule.startsWith("<")) {
                argsSwapped = true;
                rule = rule.substring(1);
                changed = true;
            }
            if (rule.startsWith("^")) {
                implicitArgMadeExplicit = true;
                rule = rule.substring(1);
                changed = true;
            }
        }

        String[] ampParts = rule.split("&", 2);
        String leftPart = ampParts[0].trim();
        String gerund = ampParts.length > 1 ? ampParts[1].trim() : "";

        String[] colonParts = leftPart.split(":", 2);
        String verbPart = colonParts[0].trim();
        String argument = colonParts.length > 1 ? colonParts[1].trim() : "";

        boolean argumentSwapsPosition = argument.endsWith("*");
        if (argumentSwapsPosition) {
            argument = argument.substring(0, argument.length() - 1);
        }

        String verb;
        String preposition = "";

        int underscoreIndex = verbPart.lastIndexOf("_");
        if (underscoreIndex >= 0) {
            verb = verbPart.substring(0, underscoreIndex).trim();
            preposition = verbPart.substring(underscoreIndex + 1).trim();
        } else {
            verb = verbPart;
        }

        return new RewriteComponent(isNegated, argsSwapped, implicitArgMadeExplicit,verb, preposition, argument, argumentSwapsPosition, gerund);
    }

    public String getOriginalPredicate() {
        return originalPredicate;
    }

    public RewriteComponent getComponent() {
        return component;
    }
}
