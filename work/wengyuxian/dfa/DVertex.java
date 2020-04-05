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

    public DVertex(int aId) {
        id = aId;
    }

    public DVertex(int aId, HashSet<Integer> aStates) {
        id = aId;
        states = aStates;
    }

    @Override
    public boolean equals(Object obj) {
        DVertex dVertex = (DVertex) obj;
        return states.equals(dVertex.states);
    }

}