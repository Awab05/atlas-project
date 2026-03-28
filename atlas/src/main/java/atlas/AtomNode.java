package atlas;

public class AtomNode extends Node{

    private String atom;
    public AtomNode(String atom){
        this.atom = atom;
    }
@Override
public String nodeToString() {
return atom;

}

}
