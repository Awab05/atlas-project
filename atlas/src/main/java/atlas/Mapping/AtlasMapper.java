package atlas.Mapping;

import java.util.HashMap;
import java.util.List;

import atlas.AtlasAbstractor;
import atlas.AtlasNode;
import atlas.AtlasPrinter;

public class AtlasMapper {

    //method used to check if 2 stuctures are mappable to one another
    public boolean isMappable(AtlasNode node1, AtlasNode node2) {

        if (node1 == null || node2 == null) {
            throw new IllegalArgumentException("Input nodes cannot be null");
        }

        AtlasAbstractor abstractor = new AtlasAbstractor();
        AtlasNode abstractednode1 = abstractor.abstractTree(node1);
        AtlasNode abstractednode2 = abstractor.abstractTree(node2);

        AtlasPrinter printer = new AtlasPrinter();

        String printednode1 = printer.toPrettyString(abstractednode1);
        String printednode2 = printer.toPrettyString(abstractednode2);

        return printednode1.equals(printednode2);

    }

    //method to map the 2 structures diffrent non predictaes together
    public HashMap<String, String> getMappings(AtlasNode node1, AtlasNode node2) {
        if (node1 == null || node2 == null) {
        throw new IllegalArgumentException("Input nodes cannot be null");}

        if (!isMappable(node1, node2)) {
            throw new IllegalArgumentException("Structures are not mappable");
        }

        HashMap<String, String> map = new HashMap<>();
        compareChildren(node1, node2, map);
        return map;

    }

    //method that compares the children of the 2 structures
    private void compareChildren(AtlasNode node1, AtlasNode node2, HashMap<String, String> map) {

        if (!node1.getLabel().equals(node2.getLabel())) {
            throw new IllegalArgumentException("Node labels do not match");
        }

        List<Object> children1 = node1.getChildren();
        List<Object> children2 = node2.getChildren();

        if (children1.size() != children2.size()) {
            throw new IllegalArgumentException("Child counts do not match");
        }

        for  (int i = 0; i < children1.size(); i++) {
        Object child1 = children1.get(i);
        Object child2 = children2.get(i);

        if (child1 instanceof AtlasNode && child2 instanceof AtlasNode) {
            compareChildren((AtlasNode) child1, (AtlasNode) child2, map);

        }
        else if (child1 instanceof String && child2 instanceof String) {
            addMapping((String)child1, (String)child2, map);
        }
        else{
            throw new IllegalArgumentException("Child types do not match");
        }

        }
    }

    private void addMapping(String value1, String value2, HashMap<String, String> map) {
        boolean value1Starred = value1.startsWith("*");
        boolean value2Starred = value2.startsWith("*");

        if (value1Starred != value2Starred) {
            throw new IllegalArgumentException("Starred values must map only to starred values");
        }

        if (map.containsKey(value2)) {
            if (!map.get(value2).equals(value1)) {
                throw new IllegalArgumentException("Inconsistent mapping found");
            }
        } else {
            map.put(value2, value1);
        }
    }
    }



