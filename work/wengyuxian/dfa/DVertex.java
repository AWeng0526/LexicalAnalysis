package work.wengyuxian.dfa;

import java.util.HashSet;

/**
 * DVertex DFA中的顶点
 */
public class DVertex {
    public int id;
    public HashSet<Integer> states;
    public boolean isFinal = false;
    public String type = null;

    /**
     * 
     * @param aId DFA顶点编号
     */
    public DVertex(int aId) {
        id = aId;
    }

    /**
     * 
     * @param aId     DFA顶点编号
     * @param aStates 该顶点包括的NFA状态
     */
    public DVertex(int aId, HashSet<Integer> aStates) {
        id = aId;
        states = aStates;
    }

    /**
     * 
     * @param aId 结点编号
     * @param aFinal 是否终态
     * @param aType 类型
     */
    public DVertex(int aId, boolean aFinal, String aType) {
        id = aId;
        isFinal = aFinal;
        type = aType;
    }

    @Override
    public boolean equals(Object obj) {
        DVertex dVertex = (DVertex) obj;
        return states.equals(dVertex.states);
    }

}