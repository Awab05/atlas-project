package atlas;

public class AtlasAbstractor {

    private int counter;

    public AtlasNode abstractTree(AtlasNode root) {
        //keeps track of the current number
        counter = 0;
        return abstractNode(root);
    }

    private AtlasNode abstractNode(AtlasNode node) {
        //creating a new node and assigning it the predicate string
        AtlasNode abstracted = new AtlasNode(node.getLabel());


        for (Object child : node.getChildren()) {
            //if the child is a nested structure, recursively call abstractNode
            if (child instanceof AtlasNode) {
                abstracted.addChild(abstractNode((AtlasNode) child));
            }
            //otherwise set the non predicate to the current counter number
            else {
                abstracted.addChild(String.valueOf(counter));
                counter++;
            }
        }

        return abstracted;
    }
}