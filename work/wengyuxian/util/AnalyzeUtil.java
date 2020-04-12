package work.wengyuxian.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import work.wengyuxian.dfa.DFA;
import work.wengyuxian.dfa.DVertex;
import work.wengyuxian.dfa.MinDFA;
import work.wengyuxian.thompson.NFA;
import work.wengyuxian.thompson.Thompson;

public class AnalyzeUtil {

    /**
     * 主调函数
     */
    public static void analyze() {
        FileUtil fileUtil = new FileUtil();
        // 默认路径为 ./reg.txt ./input.txt ./output.txt
        ArrayList<NFA> nfas = Thompson.analyzeRe(fileUtil);
        ArrayList<MinDFA> minDFAs = new ArrayList<>();
        int num = 0;
        for (NFA nfa : nfas) {
            minDFAs.add(new DFA(nfa).minimize());
            num += minDFAs.size() - 1;
        }
        // 合并dfa
        MinDFA finalDfa = MinDFA.union(minDFAs, num);
        StringBuffer buffer = new StringBuffer();
        fileUtil.readFile(buffer);
        DVertex start = finalDfa.Dstates.get(finalDfa.inital);
        ArrayList<HashMap<Character, Integer>> trans = finalDfa.transMaps;
        DVertex curr = start;
        String token = "";
        ArrayList<TokenNode> tokens = new ArrayList<>();
        for (char c : buffer.toString().toCharArray()) {
            int nextId = trans.get(curr.id).getOrDefault(c, -1);
            if (nextId < 0) {// 如果状态转移为空
                if (curr.isFinal) {// 如果为终态,记录信息
                    tokens.add(new TokenNode(curr.type, token));
                    nextId = trans.get(start.id).getOrDefault(c, -1);
                    if (nextId < 0) {
                        Logger.getGlobal().warning("error:" + c);
                        token = "";
                        curr = start;
                    } else {
                        token = String.valueOf(c);
                        curr = finalDfa.Dstates.get(nextId);
                    }
                } else {// 非终态,报错
                    Logger.getGlobal().warning("error:" + token.toString() + c);
                    token = "";
                    curr = start;
                }
            } else {// 继续分析
                token += c;
                curr = finalDfa.Dstates.get(nextId);
            }
        }

        for (TokenNode tokenNode : tokens) {
            // 识别关键字
            if (Alphabets.keyWords.contains(tokenNode.getValue())) {
                tokenNode.setType("KEYWORD");
            }
            // 屏蔽不需要的token例如空格
            if (Alphabets.shield.contains(tokenNode.getType())) {
                continue;
            }
        }
        fileUtil.writeFile(tokens);

    }

    public static void main(String[] args) {
        long a = System.currentTimeMillis();// 获取当前系统时间(毫秒)
        analyze();
        System.out.print("程序执行时间为：");
        System.out.println(System.currentTimeMillis() - a + "毫秒");
    }

}