package atlas.Rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import atlas.AtlasNode;

public class RewriteStructure {
    private HashMap<String, List<RewriteRule>> rulesMap = new HashMap<>();

    public RewriteStructure(HashMap<String, List<String>> rules) {
        for (String predicate : rules.keySet()) {
            List<RewriteRule> predicateRules = new ArrayList<>();
            for (String ruleString : rules.get(predicate)) {
                predicateRules.add(new RewriteRule(predicate, ruleString));
            }
            rulesMap.put(predicate, predicateRules);
        }
    }

    public List<AtlasNode> rewrite(AtlasNode node) {
        List<AtlasNode> results = new ArrayList<>();
        
        String predicate = node.getLabel();
        if (!rulesMap.containsKey(predicate)) {
            results.add(node);
            return results;
        }

        List<RewriteRule> applicableRules = rulesMap.get(predicate);
        for (RewriteRule rule : applicableRules) {
            List<AtlasNode> rewrites = applyRule(node, rule);
            results.addAll(rewrites);
        }

        return results;
    }

    public List<AtlasNode> rewriteRecursively(AtlasNode node) {
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

        List<List<Object>> combinations = generateCombinations(childRewrites);
        
        List<AtlasNode> nodeVariants = new ArrayList<>();
        for (List<Object> combination : combinations) {
            AtlasNode variant = new AtlasNode(node.getLabel());
            for (Object child : combination) {
                variant.addChild(child);
            }
            nodeVariants.add(variant);
        }

        List<AtlasNode> results = new ArrayList<>();
        for (AtlasNode nodeVariant : nodeVariants) {
            results.addAll(rewrite(nodeVariant));
        }

        return results;
    }

    private List<AtlasNode> applyRule(AtlasNode node, RewriteRule rule) {
        List<AtlasNode> results = new ArrayList<>();
        List<RewriteRule.RewriteComponent> components = rule.getComponents();

        for (RewriteRule.RewriteComponent component : components) {
            AtlasNode rewritten = buildRewrittenNode(node, component);
            results.add(rewritten);
        }

        return results;
    }

    private AtlasNode buildRewrittenNode(AtlasNode node, RewriteRule.RewriteComponent component) {
        List<Object> args = new ArrayList<>(node.getChildren());
        
        if (component.argsSwapped && args.size() >= 2) {
            Object temp = args.get(0);
            args.set(0, args.get(1));
            args.set(1, temp);
        }

        AtlasNode verbNode = new AtlasNode(component.verb);
        
        if (component.implicitArgMadeExplicit && !component.argument.isEmpty()) {
            verbNode.addChild(component.argument);
        }

        for (int i = 0; i < args.size(); i++) {
            if (component.argumentSwapsPosition && i == args.size() - 1) {
                AtlasNode prepNode = new AtlasNode(component.preposition);
                prepNode.addChild(args.get(i));
                verbNode.addChild(prepNode);
            } else if (!component.argumentSwapsPosition && i == args.size() - 1 && !component.preposition.isEmpty()) {
                AtlasNode prepNode = new AtlasNode(component.preposition);
                prepNode.addChild(component.argument);
                verbNode.addChild(args.get(i));
                verbNode.addChild(prepNode);
            } else {
                verbNode.addChild(args.get(i));
            }
        }

        if (component.argumentSwapsPosition && !component.argument.isEmpty()) {
            AtlasNode prepNode = new AtlasNode(component.preposition);
            prepNode.addChild(component.argument);
            verbNode.addChild(prepNode);
        } else if (!component.argumentSwapsPosition && !args.isEmpty() && !component.preposition.isEmpty() && !component.argument.isEmpty()) {
            AtlasNode prepNode = new AtlasNode(component.preposition);
            prepNode.addChild(component.argument);
            verbNode.addChild(prepNode);
        }

        AtlasNode result;

        if (component.isNegated) {
            AtlasNode notNode = new AtlasNode("not");
            notNode.addChild(verbNode);
            verbNode = notNode;
        }

        if (!component.gerund.isEmpty()) {
            AtlasNode byNode = new AtlasNode("by");
            byNode.addChild(component.gerund);
            byNode.addChild(verbNode);
            result = byNode;
        } else {
            result = verbNode;
        }

        return result;
    }

    private List<List<Object>> generateCombinations(List<List<AtlasNode>> options) {
        if (options.isEmpty()) {
            return new ArrayList<>();
        }

        List<List<Object>> results = new ArrayList<>();
        generateCombinationsHelper(options, 0, new ArrayList<>(), results);
        return results;
    }

    private void generateCombinationsHelper(List<List<AtlasNode>> options, int index, List<Object> current, List<List<Object>> results) {
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
}
