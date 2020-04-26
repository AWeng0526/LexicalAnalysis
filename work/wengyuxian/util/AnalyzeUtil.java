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
        // 默认路径为 ./reg.txt ./input.txt ./output.txt
        FileUtil fileUtil = new FileUtil();
        ArrayList<NFA> nfas = Thompson.analyzeRe(fileUtil);
        ArrayList<MinDFA> minDFAs = new ArrayList<>();
        int num = 0;
        StringBuffer NFAStr = new StringBuffer();
        StringBuffer DFAStr = new StringBuffer();
        StringBuffer MinDFAStr = new StringBuffer();
        for (NFA nfa : nfas) {
            DFA dfa = new DFA(nfa);
            MinDFA minDFA = dfa.minimize();
            minDFAs.add(minDFA);
            num += minDFAs.size() - 1;
            // 保存相应信息
            NFAStr.append(nfa.toString());
            DFAStr.append(dfa.toString());
            MinDFAStr.append(minDFA.toString());
        }
        // 写入
        fileUtil.writeFile("./log/NFA.txt", NFAStr.toString());
        fileUtil.writeFile("./log/DFA.txt", DFAStr.toString());
        fileUtil.writeFile("./log/MinDFA.txt", MinDFAStr.toString());
        // 合并dfa
        MinDFA finalDfa = MinDFA.union(minDFAs, num);
        fileUtil.writeFile("./log/FinalMinDFA.txt", finalDfa.toString());
        StringBuffer buffer = new StringBuffer();
        fileUtil.readFile(buffer);
        DVertex start = finalDfa.Dstates.get(finalDfa.inital);
        ArrayList<HashMap<Character, Integer>> trans = finalDfa.transMaps;
        DVertex curr = start;
        String token = "";
        ArrayList<TokenNode> tokens = new ArrayList<>();
        // 行计数器,字符计数器
        int charCnt = 1;
        int lineCnt = 1;
        for (char c : buffer.toString().toCharArray()) {
            int nextId = trans.get(curr.id).getOrDefault(c, -1);
            if (nextId < 0) {// 如果状态转移为空
                if (curr.isFinal) {// 如果为终态,记录信息
                    tokens.add(new TokenNode(curr.type, token));
                    nextId = trans.get(start.id).getOrDefault(c, -1);
                    if (nextId < 0) {
                        Logger.getGlobal().warning(String.format("第%d行第%d个字符发生错误,错误单词:%s", lineCnt, charCnt, c));
                        token = "";
                        curr = start;
                    } else {
                        token = String.valueOf(c);
                        curr = finalDfa.Dstates.get(nextId);
                    }
                } else {// 非终态,报错
                    Logger.getGlobal()
                            .warning(String.format("第%d行第%d个字符发生错误,错误单词:%s", lineCnt, charCnt, token.toString() + c));
                    token = "";
                    curr = start;
                }
            } else {// 继续分析
                token += c;
                curr = finalDfa.Dstates.get(nextId);
            }
            // 换行后行计数器+1,清空字符计数器
            if (c == '\n') {
                lineCnt++;
                charCnt = 0;
            }
            charCnt++;
        }

        for (TokenNode tokenNode : tokens) {
            // 识别关键字
            if (Alphabets.keyWords.contains(tokenNode.getValue())) {
                tokenNode.setType("KEYWORD");
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