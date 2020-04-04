package work.wengyuxian.thompson;

public class Vertex {

    public int state;
    public boolean isFinal;
    public String type;

    public Vertex(int aState) {
        state = aState;
        isFinal = false;
        type = null;
    }

    public Vertex(int aState, boolean aIsFinal, String aType) {
        state = aState;
        isFinal = aIsFinal;
        type = aType;
    }
}