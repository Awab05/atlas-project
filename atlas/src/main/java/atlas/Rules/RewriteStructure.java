package atlas.Rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import atlas.AtlasNode;

public class RewriteStructure {
    // maps each predicate to its list of rewrite rules
    private final HashMap<String, List<RewriteRule>> rulesMap = new HashMap<>();

    public RewriteStructure(HashMap<String, List<String>> rawRules) {
        // parse raw rule strings into RewriteRule objects organized by predicate
        for (String predicate : rawRules.keySet()) {
            List<RewriteRule> parsedRules = new ArrayList<>();

            for (String ruleString : rawRules.get(predicate)) {
                parsedRules.add(new RewriteRule(predicate, ruleString));
            }

            rulesMap.put(predicate, parsedRules);
        }
    }

    public static RewriteStructure loadFromFile() throws IOException {
        RewriteRuleLoader loader = new RewriteRuleLoader();
        loader.loadRules();
        return new RewriteStructure(loader.getRules());
    }

    public List<AtlasNode> rewrite(AtlasNode node) {
        // apply all matching rewrite rules to a single node; returns original if no rules match
        List<AtlasNode> results = new ArrayList<>();

        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        String predicate = node.getLabel();

        if (!rulesMap.containsKey(predicate)) {
            results.add(node);
            return results;
        }

        for (RewriteRule rule : rulesMap.get(predicate)) {
            results.add(buildRewrittenNode(node, rule.getComponent()));
        }

        return results;
    }

    public List<AtlasNode> rewriteRecursively(AtlasNode node) {
        // recursively rewrite all children first, then generate all combinations and apply rules to each
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }

        List<List<AtlasNode>> childRewrites = new ArrayList<>();

        for (Object child : node.getChildren()) {
            List<AtlasNode> rewrites = new ArrayList<>();
            if (child instanceof AtlasNode) {
                rewrites.addAll(rewriteRecursively((AtlasNode) child));
            } else {
                AtlasNode atom = new AtlasNode((String) child);
                rewrites.add(atom);
            }
            childRewrites.add(rewrites);
        }

        // generate all combinations of child rewrites
        List<List<Object>> combinations = generateCombinations(childRewrites);

        List<AtlasNode> results = new ArrayList<>();

        for (List<Object> combination : combinations) {
            AtlasNode variant = new AtlasNode(node.getLabel());
            for (Object child : combination) {
                variant.addChild(child);
            }

            results.addAll(rewrite(variant));
        }

        return results;
    }

    private AtlasNode buildRewrittenNode(AtlasNode original, RewriteRule.RewriteComponent component) {
        // construct a new node by applying the rewrite component transformations
        List<Object> args = new ArrayList<>(original.getChildren());

        // swap arguments if specified by < modifier
        if (component.argsSwapped && args.size() >= 2) {
            Object temp = args.get(0);
            args.set(0, args.get(1));
            args.set(1, temp);
        }

        AtlasNode verbNode = new AtlasNode(component.verb);

        // implicit arg made explicit (^ modifier) becomes first argument
        if (component.implicitArgMadeExplicit && !component.argument.isEmpty()) {
            verbNode.addChild(component.argument);
        }

        // process original arguments, wrapping last arg in preposition if needed (* or ^ modifier)
        for (int i = 0; i < args.size(); i++) {
            if (i == args.size() - 1 && (component.argumentSwapsPosition || component.implicitArgMadeExplicit) && !component.preposition.isEmpty()) {
                AtlasNode prepNode = new AtlasNode(component.preposition);
                prepNode.addChild(args.get(i));
                verbNode.addChild(prepNode);
            } else {
                verbNode.addChild(args.get(i));
            }
            
            // for * modifier, implicit arg becomes second argument
            if (component.argumentSwapsPosition && !component.argument.isEmpty() && i == 0) {
                verbNode.addChild(component.argument);
            }
        }
        
        // add implicit arg with preposition if neither * nor ^ is used
        if (!component.argumentSwapsPosition && !component.implicitArgMadeExplicit && 
            !component.preposition.isEmpty() && !component.argument.isEmpty()) {
            AtlasNode prepNode = new AtlasNode(component.preposition);
            prepNode.addChild(component.argument);
            verbNode.addChild(prepNode);
        }

        AtlasNode result = verbNode;

        // wrap in negation if ! modifier specified
        if (component.isNegated) {
            AtlasNode notNode = new AtlasNode("not");
            notNode.addChild(result);
            result = notNode;
        }

        // wrap in (by gerund ...) structure if gerund specified
        if (!component.gerund.isEmpty()) {
            AtlasNode byNode = new AtlasNode("by");
            byNode.addChild(component.gerund);
            byNode.addChild(result);
            result = byNode;
        }

        return result;
    }

    private List<List<Object>> generateCombinations(List<List<AtlasNode>> options) {
        // generate cartesian product of all child rewrites to test all child combinations
        if (options.isEmpty()) {
            return new ArrayList<>();
        }

        List<List<Object>> results = new ArrayList<>();
        generateCombinationsHelper(options, 0, new ArrayList<>(), results);
        return results;
    }

    private void generateCombinationsHelper(List<List<AtlasNode>> options, int index, List<Object> current, List<List<Object>> results) {
        // recursive backtracking to build all combinations of the given options
        if (index == options.size()) {
            results.add(new ArrayList<>(current));
            return;
        }

        for (AtlasNode option : options.get(index)) {
            current.add(option);
            generateCombinationsHelper(options, index + 1, current, results);
            current.remove(current.size() - 1);
        }
    }

    public HashMap<String, List<RewriteRule>> getRulesMap() {
        return rulesMap;
    }
}