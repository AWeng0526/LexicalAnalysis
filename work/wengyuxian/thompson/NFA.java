package work.wengyuxian.thompson;

import java.util.ArrayList;

/**
 * NFA
 */
public class NFA {
    public ArrayList<Vertex> states;
    public ArrayList<Trans> transitions;
    // 对于一个正规式而言,只会有一个终态
    public int finalState;

    public NFA() {
        this.states = new ArrayList<Vertex>();
        this.transitions = new ArrayList<Trans>();
        this.finalState = 0;
    }

    public NFA(int size) {
        this.states = new ArrayList<Vertex>();
        this.transitions = new ArrayList<Trans>();
        this.finalState = 0;
        this.setStateSize(size);
    }

    /**
     * 初始化只有一个字符的正规式
     * 
     * @param c 正规式字符
     */
    public NFA(char c) {
        this.states = new ArrayList<Vertex>();
        this.transitions = new ArrayList<Trans>();
        this.setStateSize(2);
        this.finalState = 1;
        this.transitions.add(new Trans(0, 1, c));
    }

    /**
     * 初始化状态集
     * 
     * @param size 状态集容量
     */
    public void setStateSize(int size) {
        for (int i = 0; i < size; i++)
            this.states.add(new Vertex(i));
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (Vertex vertex : states) {
            if (vertex.isFinal) {
                buffer.append(String.format("终态结点: %d %s\n", vertex.state, vertex.type));
            }
        }
        for (Trans t : transitions) {
            buffer.append(String.format("(%3s,%2s ,%3s)\n", t.stateFrom, t.transSymbol, t.stateTo));
        }
        return buffer.toString();
    }
}
