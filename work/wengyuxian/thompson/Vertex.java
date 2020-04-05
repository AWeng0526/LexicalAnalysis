package work.wengyuxian.thompson;

/**
 * Vertex
 */
public class Vertex {

    // 顶点状态,应该与顶点索引保持一致
    public int state;
    // 是否为终态
    public boolean isFinal;
    // 类型
    public String type;

    /**
     * 
     * @param aState 顶点状态
     */
    public Vertex(int aState) {
        state = aState;
        isFinal = false;
        type = null;
    }

    /**
     * 
     * @param aState   顶点状态
     * @param aIsFinal 是否终态
     * @param aType    类型
     */
    public Vertex(int aState, boolean aIsFinal, String aType) {
        state = aState;
        isFinal = aIsFinal;
        type = aType;
    }
}