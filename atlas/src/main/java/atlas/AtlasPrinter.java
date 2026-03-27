package atlas;

public class AtlasPrinter {
    
    public String AtlasToFlatString(AtlasNode node){
        StringBuilder sb = new StringBuilder();
        buildFlat(node,sb);
        return sb.toString();
    }

    private void buildFlat(AtlasNode node, StringBuilder sb){ 
        sb.append('(').append(node.getLabel());
    
        for (Object child: node.getChildren()){
            sb.append(" ");
            if (child instanceof AtlasNode) {
                buildFlat((AtlasNode) child, sb);
            } else {
                sb.append(child); // string
            }
        }
    
        sb.append(')');
    }
    
    public String toPrettyString(AtlasNode node) {
        StringBuilder sb = new StringBuilder();
        buildPretty(node, sb, 0);
        return sb.toString();
    }

    private void buildPretty(AtlasNode node, StringBuilder sb, int indent) {
        indent(sb, indent);
        sb.append("(").append(node.getLabel());
    
        for (Object child : node.getChildren()) {
            if (child instanceof AtlasNode) {
                sb.append("\n");
                buildPretty((AtlasNode) child, sb, indent + 2);
            } else {
                sb.append(" ").append(child); // inline string
            }
        }
    
        sb.append(")");
    }

    private static void indent(StringBuilder sb, int spaces) {
        for (int i = 0; i < spaces; i++) {
            sb.append(" ");
        }
    }
}

