package atlas;

import java.util.ArrayList;

public class ListNode extends Node{

    private ArrayList<Node> children = new ArrayList<>();

    @Override
    public String nodeToString() {
        return children.toString();
    }
}
