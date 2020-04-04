package work.wengyuxian.dfa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import work.wengyuxian.thompson.*;
import work.wengyuxian.util.Alpahas;

public class DFA {
    public LinkedList<DVertex> Dstates = new LinkedList<>();
    public ArrayList<Trans> Dtrans = new ArrayList<>();
    public ArrayList<ArrayList<Trans>> transitions;

    public DFA(NFA nfa) {
        int vertexNum = nfa.states.size();
        transitions = new ArrayList<>(vertexNum);
        for (int i = 0; i < vertexNum; i++) {
            transitions.add(new ArrayList<Trans>());
        }
        for (Trans t : nfa.transitions) {
            transitions.get(t.stateFrom).add(t);
        }

        int cnt = 0;
        DVertex first = new DVertex(cnt++);
        LinkedList<Integer> tmpList = new LinkedList<>();
        tmpList.add(0);
        first.states = getEpsilonTrans(tmpList);
        Dstates.add(first);

        ListIterator<DVertex> iterator = Dstates.listIterator();
        while (iterator.hasNext()) {
            DVertex vertex = iterator.next();
            for (Character c : Alpahas.alpahas) {
                HashSet<Integer> transStates = getEpsilonTrans(smove(vertex, c));
                if (transStates.size() > 0) {
                    DVertex newDVertex = new DVertex(cnt, transStates);
                    if (!Dstates.contains(newDVertex)) {
                        HashSet<Integer> finalStates = new HashSet<>();
                        finalStates.addAll(newDVertex.states);
                        finalStates.retainAll(nfa.finalStates);
                        iterator.add(newDVertex);
                        iterator.previous();
                        Dtrans.add(new Trans(vertex.id, cnt, c));
                        cnt++;
                    }
                }
            }
        }

        for (int i = 0; i < Dstates.size(); i++) {
            DVertex dVertex = Dstates.get(i);
            for (int j : dVertex.states) {
                if (nfa.finalStates.contains(j)) {
                    dVertex.isFinal = true;
                    dVertex.type = dVertex.type == null ? nfa.states.get(j).type
                            : dVertex.type + nfa.states.get(j).type;
                }
            }

        }
        // System.out.println(Dstates);
        // System.out.println(Dtrans);
    }

    public LinkedList<Integer> smove(DVertex dVertex, char c) {
        LinkedList<Integer> ans = new LinkedList<Integer>();
        for (int i : dVertex.states) {
            for (Trans t : transitions.get(i)) {
                if (t.transSymbol == c) {
                    ans.add(t.stateTo);
                }

            }
        }
        return ans;

    }

    public HashSet<Integer> getEpsilonTrans(LinkedList<Integer> states) {
        ListIterator<Integer> iterator = states.listIterator();
        while (iterator.hasNext()) {
            int tmp = iterator.next();
            for (Trans t : transitions.get(tmp)) {
                if (t.transSymbol == '#') {
                    iterator.add(t.stateTo);
                    iterator.previous();
                }
            }
        }
        return new HashSet<>(states);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("结点:\n");
        for (DVertex dVertex : Dstates) {
            buffer.append(String.format("id:%3d  %10s 状态集:%s\n", dVertex.id,
                    dVertex.isFinal ? "终态:" + dVertex.type : "非终态", dVertex.states.toString()));
        }
        buffer.append("状态转移:\n");
        for (Trans t : Dtrans) {
            buffer.append(t.toString());
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        NFA nfa = Thompson.analyzeRe();
        DFA dfa = new DFA(nfa);
        System.out.println(nfa.finalStates);
        System.out.println(dfa);
    }
}