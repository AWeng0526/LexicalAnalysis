package work.wengyuxian.dfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

import work.wengyuxian.thompson.*;
import work.wengyuxian.util.Alpahas;

public class DFA {
    public ArrayList<Character> alphas = new ArrayList<Character>() {
        {
            add('a');
        }
    };
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
                        iterator.add(newDVertex);
                        Dtrans.add(new Trans(vertex.id, cnt, c));
                        cnt++;
                    }
                }
            }
        }
        System.out.println(Dstates);
        System.out.println(Dtrans);
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
        HashSet<Integer> ans = new HashSet<>(states);
        Iterator<Integer> iterator = states.iterator();
        while (iterator.hasNext()) {
            int tmp = iterator.next();
            for (Trans t : transitions.get(tmp)) {
                if (t.transSymbol == '#') {
                    ans.add(t.stateTo);
                }
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        NFA nfa = Thompson.analyzeRe();
        DFA dfa = new DFA(nfa);
        System.out.println(nfa);
    }
}