package atlas;

import java.util.ArrayList;
import java.util.List;

public class AtlasNode {
    private String label;
    private List<Object> children = new ArrayList<>();

    public AtlasNode(String label) {
        this.label = label;
    }

    public void addChild(Object child) {
        if (!(child instanceof String || child instanceof AtlasNode)) {
            throw new IllegalArgumentException("child must be a string or an AtlasNode");
        }
        children.add(child);
    }

    public String getLabel() { return label; }
    public List<Object> getChildren() { return children; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(label);

        for (Object child : children) {
            sb.append(" ");
            sb.append(child.toString());
        }

        sb.append(")");
        return sb.toString();
    }
}