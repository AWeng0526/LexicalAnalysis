package work.wengyuxian.dfa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import work.wengyuxian.thompson.*;
import work.wengyuxian.util.Alphabets;

public class DFA {
    public LinkedList<DVertex> Dstates = new LinkedList<>();
    public ArrayList<Trans> Dtrans = new ArrayList<>();
    // 每个顶点的状态转移集的集合,即transitions.get(i)为顶点i的状态转移
    public ArrayList<HashSet<Trans>> transitions;

    /**
     * 
     * @param nfa 需要转化为DFA的NFA
     */
    public DFA(NFA nfa) {
        // 初始化
        int vertexNum = nfa.states.size();
        transitions = new ArrayList<>(vertexNum);
        for (int i = 0; i < vertexNum; i++) {
            transitions.add(new HashSet<Trans>());
        }
        for (Trans t : nfa.transitions) {
            transitions.get(t.stateFrom).add(t);
        }

        // cnt用于计数,计算Dstates中的元素个数
        int cnt = 0;
        // 求初态的空闭包
        DVertex first = new DVertex(cnt++);
        ArrayList<Integer> tmpList = new ArrayList<>();
        tmpList.add(0);
        first.states = getEpsilonTrans(tmpList);
        Dstates.add(first);
        // 遍历Dstates,这里不能用foreach.因为Dstates会新增元素
        // ListIterator不支持向尾部添加元素,故选用普通的for循环
        // i<Dstates.size()不能改写,因为需要重新计算长度
        for (int i = 0; i < Dstates.size(); i++) {
            DVertex vertex = Dstates.get(i);
            // 对当前顶点,用字母表中所有元素进行状态转移
            for (Character c : Alphabets.alphabets) {
                HashSet<Integer> transStates = getEpsilonTrans(smove(vertex, c));
                // 结果不为空
                if (transStates.size() > 0) {
                    DVertex newDVertex = new DVertex(cnt, transStates);
                    // 由于重写了DVertex的equals方法
                    // 这里判断两个DVertex相等的方法是比较二者的states,不考虑其他属性
                    int idx = Dstates.indexOf(newDVertex);
                    if (idx < 0) {// 如果不存在,即该顶点为新顶点
                        Dstates.add(newDVertex);
                        Dtrans.add(new Trans(vertex.id, cnt, c));
                        cnt++;
                    } else {// 如果存在,只需添加一条状态转移
                        Dtrans.add(new Trans(vertex.id, idx, c));
                    }
                }
            }
        }

        // 检查是否为终态
        for (int i = 0; i < Dstates.size(); i++) {
            DVertex dVertex = Dstates.get(i);
            if (dVertex.states.contains(nfa.finalState)) {
                Logger.getGlobal().fine(dVertex.states.toString());
                dVertex.isFinal = true;
                dVertex.type = nfa.states.get(nfa.finalState).type;
            }
        }
    }

    /**
     * 状态转移函数
     * 
     * @param dVertex 结点
     * @param c       转移条件
     * @return 状态集
     */
    public ArrayList<Integer> smove(DVertex dVertex, char c) {
        ArrayList<Integer> ans = new ArrayList<Integer>();
        for (int i : dVertex.states) {
            for (Trans t : transitions.get(i)) {
                if (t.transSymbol == c) {
                    ans.add(t.stateTo);
                }
            }
        }
        return ans;

    }

    /**
     * 求空闭包
     * 
     * @param states 状态集
     * @return 空闭包结果集
     */
    public HashSet<Integer> getEpsilonTrans(ArrayList<Integer> states) {
        // 理由同上,由于遍历过程中可能新增元素,故采用for循环
        for (int i = 0; i < states.size(); i++) {
            int tmp = states.get(i);
            for (Trans t : transitions.get(tmp)) {
                if (t.transSymbol == '#') {
                    states.add(t.stateTo);
                    Logger.getGlobal()
                    .info(String.format("from %3d to %3d with %s", t.stateFrom, t.stateTo, t.transSymbol));
                }
            }
        }
        return new HashSet<>(states);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("结点信息:\n");
        for (DVertex dVertex : Dstates) {
            buffer.append(String.format("id: %3d   %-12s 状态集:%s\n", dVertex.id, dVertex.isFinal ? dVertex.type : "---",
                    dVertex.states));
        }
        buffer.append("状态转移:\n");
        for (Trans t : Dtrans) {
            buffer.append(t.toString());
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.INFO);
        ArrayList<NFA> nfas = Thompson.analyzeRe();
        for (NFA nfa : nfas) {
            System.out.println(nfa);
            DFA dfa = new DFA(nfa);
            // System.out.println(nfa.finalState);
            System.out.println(dfa);
        }
    }
}