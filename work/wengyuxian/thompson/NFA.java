package work.wengyuxian.thompson;

import java.util.ArrayList;
import java.util.HashSet;

import work.wengyuxian.thompson.*;

/*
 * NFA - serves as the graph that represents the Non-Deterministic Finite
 * Automata. Will use this to better combine the states.
 */
public class NFA {
    public ArrayList<VertexType> states;
    public ArrayList<Trans> transitions;
    public int finalState;

    public NFA() {
        this.states = new ArrayList<VertexType>();
        this.transitions = new ArrayList<Trans>();
        this.finalState = 0;
    }

    public NFA(int size) {
        this.states = new ArrayList<VertexType>();
        this.transitions = new ArrayList<Trans>();
        this.finalState = 0;
        this.setStateSize(size);
    }

    public NFA(char c) {
        this.states = new ArrayList<VertexType>();
        this.transitions = new ArrayList<Trans>();
        this.setStateSize(2);
        this.finalState = 1;
        this.transitions.add(new Trans(0, 1, c));
    }

    public void setStateSize(int size) {
        for (int i = 0; i < size; i++)
            this.states.add(i);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (Trans t : transitions) {
            buffer.append(String.format("(%3s,%2s ,%3s)\n", t.stateFrom, t.transSymbol, t.stateTo));
        }
        return buffer.toString();
    }
}
