package work.wengyuxian.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javafx.util.Pair;
import work.wengyuxian.dfa.DFA;
import work.wengyuxian.dfa.DVertex;
import work.wengyuxian.dfa.MinDFA;
import work.wengyuxian.thompson.NFA;
import work.wengyuxian.thompson.Thompson;
import work.wengyuxian.thompson.Vertex;

public class AnalyzeUtil {

    public static void analyze() {
        FileUtil fileUtil = new FileUtil();
        ArrayList<NFA> ans = new ArrayList<>();
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
        DVertex next = curr;
        String token = "";
        ArrayList<TokenNode> tokens = new ArrayList<>();
        Boolean lastWS = false;
        System.out.println(finalDfa);
        for (char c : buffer.toString().toCharArray()) {
            int nextId = trans.get(curr.id).getOrDefault(c, -1);
            boolean isWhiteSpace = Pattern.matches("\\s", String.valueOf(c));

            if (isWhiteSpace) {
                if (lastWS) {
                    continue;
                } else if (curr.isFinal) {
                    tokens.add(new TokenNode(curr.type, token));
                    curr = start;
                    token = "";
                } else {
                    Logger.getGlobal().warning("error:" + token.toString());
                    token = "";
                    curr = start;
                }
                lastWS = true;
            } else if (nextId < 0) {
                lastWS = false;
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
                lastWS = false;
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