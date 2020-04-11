package work.wengyuxian.dfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MinDFA {

    public int start;// 偏移量
    public int inital;// 初态
    public LinkedList<DVertex> Dstates = new LinkedList<>();
    public ArrayList<HashMap<Character, Integer>> transMaps = new ArrayList<>();

    /**
     * 
     * @param aStart 偏移量
     * @param vertexNum 顶点数目
     */
    public MinDFA(int aStart, int vertexNum) {
        start = aStart;
        for (int i = 0; i < vertexNum; i++) {
            transMaps.add(new HashMap<>());
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("结点信息:\n");
        buffer.append(String.format("初态:  %3d\n", inital));
        for (DVertex dVertex : Dstates) {
            buffer.append(String.format("结点编号: %3d   %-12s \n", dVertex.id, dVertex.isFinal ? dVertex.type : "---"));
        }
        buffer.append("状态转移:\n");
        for (int i = 0; i < transMaps.size(); i++) {
            for (Map.Entry<Character, Integer> set : transMaps.get(i).entrySet()) {
                buffer.append(String.format("(%3d ,%3c ,%3d)\n", i + start, set.getKey(), set.getValue()));
            }
        }
        return buffer.toString();
    }
}