package work.wengyuxian.dfa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import work.wengyuxian.thompson.*;
import work.wengyuxian.util.Alphabets;
import work.wengyuxian.util.FileUtil;

public class DFA {
    public ArrayList<DVertex> Dstates = new ArrayList<>();
    public ArrayList<Trans> Dtrans = new ArrayList<>();
    // 每个顶点的状态转移集的集合,即transitions.get(i)为顶点i的状态转移
    public ArrayList<ArrayList<Trans>> transitions;
    // 类似transitions,transMaps为每个顶点的状态转移映射的集合
    public ArrayList<HashMap<Character, Integer>> transMaps;
    // 每个DFA顶点对应的组号
    public HashMap<Integer, Integer> groups;

    /**
     * 
     * @param nfa 需要转化为DFA的NFA
     */
    public DFA(NFA nfa) {
        // 初始化
        int vertexNum = nfa.states.size();
        transMaps = new ArrayList<>(vertexNum);
        transitions = new ArrayList<>(vertexNum);
        groups = new HashMap<>();
        for (int i = 0; i < vertexNum; i++) {
            transitions.add(new ArrayList<Trans>());
            transMaps.add(new HashMap<>());
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
                if (c != '#' && transStates.size() > 0) {
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
        // 为transMap赋值
        for (Trans t : Dtrans) {
            transMaps.get(t.stateFrom).put(t.transSymbol, t.stateTo);
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
                }
            }
        }
        return new HashSet<>(states);
    }

    /**
     * 最小化DFA
     * 
     * @return 最小化后的minDFA
     */
    public MinDFA minimize() {
        // 各个组的成员
        ArrayList<ArrayList<Integer>> groupMember = new ArrayList<>();
        // 第一次划分的初态集
        ArrayList<Integer> generalSet = new ArrayList<>();
        // 第一次划分的终态集
        ArrayList<Integer> finalSet = new ArrayList<>();
        for (DVertex dVertex : Dstates) {// 遍历所有顶点,终态和非终态放入对应组
            if (dVertex.isFinal) {// 终态为group0,因为需要不断划分出非终态
                groups.put(dVertex.id, 0);
                finalSet.add(dVertex.id);
            } else {
                groups.put(dVertex.id, 1);
                generalSet.add(dVertex.id);
            }
        }
        // 记录当前的组数
        int groupNum = 1;
        // 由于终态为第0组,故先添加终态
        groupMember.add(finalSet);
        if (generalSet.size() > 0) {// 可能某些DFA只有终态,导致generalSet为空
            groupMember.add(generalSet);
            groupNum++;
        }
        for (int i = 1; i < groupNum; i++) {// 终态无需划分,故可以从1开始
            ArrayList<Integer> members = groupMember.get(i);// 获取改组中所有成员
            int newGroupIdx = groupNum;// 新的组别索引
            int restart = 0;// 如果该组被划分,需要从第1组重新开始检查
            for (int j = 1; j < members.size(); j++) {// 将该组中与members.get(0)不同组的所有结点添加到一个新组中
                int baseState = members.get(0);
                int compareState = members.get(j);
                if (!inSameGroup(baseState, compareState)) {
                    if (newGroupIdx == groupNum) {// 检查新的组别是否创建
                        groupMember.add(new ArrayList<>());
                        newGroupIdx++;
                    }
                    ArrayList<Integer> insertGroup = groupMember.get(groupNum);
                    groupMember.get(i).removeIf(a -> a == compareState);// 在旧的组别中删除元素
                    insertGroup.add(compareState);
                    groups.put(compareState, groupNum);// 更新映射
                    groupNum++;
                    restart = -i;// 设置重新检查
                }
            }
            i += restart;// 若发现新的组别,i=0.由于i++,会从第1组重新开始扫描
        }

        // 构造MinDFA
        MinDFA minDFA = new MinDFA(groupMember.size());
        for (int i = 0; i < groupMember.size(); i++) {// 每组选第一个顶点作为代表
            DVertex behalf = Dstates.get(groupMember.get(i).get(0));
            DVertex newDVertex = new DVertex(i, behalf.isFinal, behalf.type);
            minDFA.Dstates.add(newDVertex);
        }
        for (Trans t : Dtrans) {// 重新添加状态转移
            int newFromId = groups.get(t.stateFrom);
            int newToId = groups.get(t.stateTo);
            minDFA.transMaps.get(newFromId).put(t.transSymbol, newToId);
        }
        // 设置新初态
        minDFA.inital = groups.get(0);
        return minDFA;
    }

    /**
     * 判断两个DFA结点是否同一组
     * 
     * @param first  结点1的索引(id)
     * @param second 结点2的索引(id)
     * @return
     */
    public boolean inSameGroup(int first, int second) {
        // 获取二者的状态转移集
        HashMap<Character, Integer> firstTrans = transMaps.get(first);
        HashMap<Character, Integer> secondTrans = transMaps.get(second);
        boolean res = true;
        for (Character c : Alphabets.alphabets) {// 遍历字母表
            // 为二者设置默认值,两个默认值为两不相同的复数
            int firstEnd = firstTrans.getOrDefault(c, -1);
            int secondEnd = secondTrans.getOrDefault(c, -2);
            if (groups.get(firstEnd) != groups.get(secondEnd)) {
                res = false;
                break;
            }
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("结点信息:\n");
        for (DVertex dVertex : Dstates) {
            buffer.append(String.format("id:%3d\t%-12s\t状态集数目:%3d\t状态集:%s\n", dVertex.id,
                    dVertex.isFinal ? dVertex.type : "---", dVertex.states.size(), dVertex.states));
        }
        buffer.append("状态转移:\n");
        for (Trans t : Dtrans) {
            buffer.append(t.toString());
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        Logger.getGlobal().setLevel(Level.INFO);
        ArrayList<NFA> nfas = Thompson.analyzeRe(new FileUtil());
        ArrayList<MinDFA> dfas = new ArrayList<>();
        int num = 0;
        for (NFA nfa : nfas) {
            // System.out.println(nfa);
            DFA dfa = new DFA(nfa);
            // System.out.println(nfa.finalState);
            // System.out.println(dfa);
            MinDFA tmp = dfa.minimize();
            num += tmp.Dstates.size();
            dfas.add(tmp);
            System.out.println(tmp);
            break;
        }
        // System.out.println(MinDFA.union(dfas, num));

    }
}