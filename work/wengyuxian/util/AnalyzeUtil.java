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

    public static void analyze() {
        FileUtil fileUtil = new FileUtil();
        // 默认路径为 ./reg.txt
        ArrayList<NFA> nfas = Thompson.analyzeRe(fileUtil);
        ArrayList<MinDFA> minDFAs = new ArrayList<>();
        int num = 0;
        for (NFA nfa : nfas) {
            minDFAs.add(new DFA(nfa).minimize());
            num += minDFAs.size() - 1;
        }
        MinDFA finalDfa = MinDFA.union(minDFAs, num);
        StringBuffer buffer = new StringBuffer();
        fileUtil.readFile(buffer);

        DVertex start = finalDfa.Dstates.get(finalDfa.inital);
        ArrayList<HashMap<Character, Integer>> trans = finalDfa.transMaps;
        DVertex curr = start;
        String token = "";
        ArrayList<TokenNode> tokens = new ArrayList<>();
        System.out.println(finalDfa);
        for (char c : buffer.toString().toCharArray()) {
            int nextId = trans.get(curr.id).getOrDefault(c, -1);
            if (nextId < 0) {
                if (curr.isFinal) {
                    tokens.add(new TokenNode(curr.type, token));
                    nextId = trans.get(start.id).get(c);
                    if (nextId < 0) {
                        Logger.getGlobal().warning("error:" + token.toString());
                        token = "";
                        curr = start;
                    } else {
                        token = String.valueOf(c);
                        curr = finalDfa.Dstates.get(nextId);
                    }
                } else {
                    Logger.getGlobal().warning("error:" + token.toString());
                    token = "";
                    curr = start;
                }
            } else {
                token += c;
                curr = finalDfa.Dstates.get(nextId);
            }
        }

        fileUtil.writeFile(tokens);

    }

    public static void main(String[] args) {
        analyze();
    }

}