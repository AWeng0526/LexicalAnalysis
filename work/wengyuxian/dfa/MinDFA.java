package work.wengyuxian.dfa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinDFA {

    public int inital;// 初态
    public ArrayList<DVertex> Dstates = new ArrayList<>();
    public ArrayList<HashMap<Character, Integer>> transMaps = new ArrayList<>();

    /**
     * 
     * @param aStart    偏移量
     * @param vertexNum 顶点数目
     */
    public MinDFA(int vertexNum) {
        setTransMaps(vertexNum);
    }

    /**
     * 初始化transMap
     * @param vertexNum 顶点个数
     */
    public void setTransMaps(int vertexNum) {
        for (int i = 0; i < vertexNum; i++) {
            transMaps.add(new HashMap<>());
        }
    }

    /**
     * 求多个MinDFA的并集
     * 
     * @param minDFAs   MinDFA集合
     * @param vertexNum 所有顶点的数目
     * @return 合并后的dfa
     */
    public static MinDFA union(List<MinDFA> minDFAs, int vertexNum) {
        // 最终DFA会多一个初态
        MinDFA ans = new MinDFA(vertexNum + 1);
        ans.inital = 0;
        // 添加初态
        ans.Dstates.add(new DVertex(0));
        // 记录当前偏移量
        int curr = 1;
        for (MinDFA minDFA : minDFAs) {
            // 合并顶点
            for (DVertex dVertex : minDFA.Dstates) {
                // 所有DFA的初态会被合并至一个
                if (dVertex.id != minDFA.inital) {
                    DVertex newNode;
                    // 编号大于初态的结点,由于初态被合并,所以偏移量要减一
                    int newId = curr + (dVertex.id > minDFA.inital ? (dVertex.id - 1) : dVertex.id);
                    if (dVertex.isFinal) {
                        newNode = new DVertex(newId, true, dVertex.type);
                    } else {
                        newNode = new DVertex(newId);
                    }
                    ans.Dstates.add(newNode);
                }
            }
            // 合并状态转移
            for (int i = 0; i < minDFA.transMaps.size(); i++) {
                HashMap<Character, Integer> map = minDFA.transMaps.get(i);
                for (Map.Entry<Character, Integer> entry : map.entrySet()) {
                    // 同上,所有的初态会被合并至一个初态
                    // 所有结点编号大于初态的结点偏移量需要减一
                    Character transSymbol = entry.getKey();
                    Integer transTo = entry.getValue();
                    Integer transFrom = i;
                    if (transTo > minDFA.inital) {
                        transTo--;
                    } else if (transTo == minDFA.inital) {
                        transTo = -curr;
                    }
                    if (transFrom > minDFA.inital) {
                        transFrom--;
                    } else if (transFrom == minDFA.inital) {
                        transFrom = -curr;
                    }
                    ans.transMaps.get(transFrom + curr).put(transSymbol, transTo + curr);
                }
            }
            // 计算偏移量
            curr += minDFA.Dstates.size() - 1;
        }
        return ans;
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
                buffer.append(String.format("(%3d ,%3c ,%3d)\n", i, set.getKey(), set.getValue()));
            }
        }
        return buffer.toString();
    }
}