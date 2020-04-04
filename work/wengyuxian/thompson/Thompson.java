
package work.wengyuxian.thompson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Logger;

import javafx.util.Pair;
import work.wengyuxian.util.*;

public class Thompson {

    /*
     * kleene() - Highest Precedence regular expression operator. Thompson algoritm
     * for kleene star.
     */
    public static NFA kleene(NFA n) {
        NFA result = new NFA(n.states.size() + 2);
        result.transitions.add(new Trans(0, 1, '#')); // new trans for q0

        // copy existing transisitons
        for (Trans t : n.transitions) {
            result.transitions.add(new Trans(t.stateFrom + 1, t.stateTo + 1, t.transSymbol));
        }

        // add empty transition from final n state to new final state.
        result.transitions.add(new Trans(n.states.size(), n.states.size() + 1, '#'));

        // Loop back from last state of n to initial state of n.
        result.transitions.add(new Trans(n.states.size(), 1, '#'));

        // Add empty transition from new initial state to new final state.
        result.transitions.add(new Trans(0, n.states.size() + 1, '#'));

        result.finalState = n.states.size() + 1;
        return result;
    }

    /*
     * concat() - Thompson algorithm for concatenation. Middle Precedence.
     */
    public static NFA concat(NFA n, NFA m) {
        /// *
        m.states.remove(0); // delete m's initial state

        // copy NFA m's transitions to n, and handles connecting n & m
        for (Trans t : m.transitions) {
            n.transitions
                    .add(new Trans(t.stateFrom + n.states.size() - 1, t.stateTo + n.states.size() - 1, t.transSymbol));
        }

        // take m and combine to n after erasing inital m state
        for (Vertex s : m.states) {
            n.states.add(new Vertex(s.state + n.finalState));
        }

        n.finalState = n.states.size() + m.states.size() - 2;
        return n;
    }

    public static NFA union(NFA m, NFA n) {
        if (m.finalState == 0) {
            return n;
        }
        if (n.finalState == 0) {
            return m;
        }
        NFA result = new NFA(n.states.size() + m.states.size() + 2);

        // 多个NFA合并时要保留终态

        for (int i : n.finalStates) {
            Vertex vertex = n.states.get(i);
            int newIdx = vertex.state + 1;
            Vertex newFinal = result.states.get(newIdx);
            result.finalStates.add(newIdx);
            newFinal.isFinal = true;
            newFinal.type = vertex.type;
        }
        for (int i : m.finalStates) {
            Vertex vertex = m.states.get(i);
            int newIdx = vertex.state + n.finalState + 2;
            Vertex newFinal = result.states.get(newIdx);
            result.finalStates.add(newIdx);
            newFinal.isFinal = true;
            newFinal.type = vertex.type;
        }

        // 时间复杂度高
        // for (Vertex vertex : n.states) {
        // if (vertex.isFinal && vertex.type != null) {
        // Vertex newNFinal = result.states.get(vertex.state + 1);
        // newNFinal.isFinal = true;
        // newNFinal.type = vertex.type;
        // }
        // }

        // for (Vertex vertex : m.states) {
        // if (vertex.isFinal && vertex.type != null) {
        // Vertex newMFinal = result.states.get(vertex.state + n.finalState + 2);
        // newMFinal.isFinal = true;
        // newMFinal.type = vertex.type;
        // }
        // }

        // 新增起点
        result.transitions.add(new Trans(0, 1, '#'));

        // 复制n中状态转移
        for (Trans t : n.transitions) {
            result.transitions.add(new Trans(t.stateFrom + 1, t.stateTo + 1, t.transSymbol));
        }

        // 将n中终态转移至新终态
        result.transitions.add(new Trans(n.states.size(), n.states.size() + m.states.size() + 1, '#'));

        // 新起点连接至m起点
        result.transitions.add(new Trans(0, n.states.size() + 1, '#'));

        // 复制m中状态转移,所有编号需要偏移,偏移量为n中数量+1
        int offset = n.states.size() + 1;
        for (Trans t : m.transitions) {
            result.transitions.add(new Trans(t.stateFrom + offset, t.stateTo + offset, t.transSymbol));
        }

        // m的终态转移至新终态
        result.transitions
                .add(new Trans(m.states.size() + n.states.size(), n.states.size() + m.states.size() + 1, '#'));

        // 计算终态
        result.finalState = n.states.size() + m.states.size() + 1;
        return result;
    }

    /**
     * 判断字符是否在字母表中,#代表空串
     * 
     * @param c 需判断的字符
     * @return
     */
    public static boolean alphabet(char c) {
        return Alpahas.alpahas.contains(c);
    }

    /**
     * 判断字符是否是正规式表达式
     * 
     * @param c 需判断的字符
     * @return
     */
    public static boolean regexOperator(char c) {
        return c == '(' || c == ')' || c == '*' || c == '|';
    }

    /**
     * 判断字符是否是合法字符
     * 
     * @param c 需判断的字符
     * @return
     */
    public static boolean validRegExChar(char c) {
        return alphabet(c) || regexOperator(c);
    }

    /**
     * 判断正规式是否为合法的正规式
     * 
     * @param regex 正规式
     * @return
     */
    public static boolean validRegEx(String regex) {
        if (regex.isEmpty())
            return false;
        for (char c : regex.toCharArray())
            if (!validRegExChar(c))
                return false;
        return true;
    }

    /**
     * 根据正规式翻译出NFA
     * 
     * @param regex 正规式
     * @return 翻译失败时返回null
     */
    public static NFA compile(String regex) {
        if (!validRegEx(regex)) {
            Logger.getGlobal().warning("Invalid Regular Expression Input.");
            return null;
        }

        Stack<Character> operators = new Stack<Character>();// 操作符栈
        Stack<NFA> operands = new Stack<NFA>();// 操作数栈
        Stack<NFA> concatStack = new Stack<NFA>();// 连接栈
        boolean concatFlag = false; // 连接标识符
        char op, c; // 当前字符
        int unpairedBracketCount = 0;// 用于计算未配对的括号符号
        boolean escape = false;// 是否被转义
        NFA nfa1, nfa2;

        for (int i = 0; i < regex.length(); i++) {
            c = regex.charAt(i);
            if (escape) {
                if (!alphabet(c)) {
                    Logger.getGlobal().warning("错误字符:" + c);
                    return null;
                }
                operands.push(new NFA(c));
                if (concatFlag) {
                    operators.push('$'); // '$'为连接标识符
                } else {
                    concatFlag = true;
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (!regexOperator(c) && alphabet(c)) {
                operands.push(new NFA(c));
                if (concatFlag) {
                    operators.push('$'); // '$'为连接标识符
                } else {
                    concatFlag = true;
                }
            } else {
                if (c == ')') {
                    concatFlag = false;
                    if (unpairedBracketCount == 0) {
                        System.out.println("Error: More end paranthesis " + "than beginning paranthesis");
                        System.exit(1);
                    } else {
                        unpairedBracketCount--;
                    }
                    // process stuff on stack till '('
                    while (!operators.empty() && operators.peek() != '(') {
                        op = operators.pop();
                        if (op == '$') {
                            nfa2 = operands.pop();
                            nfa1 = operands.pop();
                            operands.push(concat(nfa1, nfa2));
                        } else if (op == '|') {
                            nfa2 = operands.pop();

                            if (!operators.empty() && operators.peek() == '$') {

                                concatStack.push(operands.pop());
                                while (!operators.empty() && operators.peek() == '$') {

                                    concatStack.push(operands.pop());
                                    operators.pop();
                                }
                                nfa1 = concat(concatStack.pop(), concatStack.pop());
                                while (concatStack.size() > 0) {
                                    nfa1 = concat(nfa1, concatStack.pop());
                                }
                            } else {
                                nfa1 = operands.pop();
                            }
                            operands.push(union(nfa1, nfa2));
                        }
                    }
                } else if (c == '*') {
                    operands.push(kleene(operands.pop()));
                    concatFlag = true;
                } else if (c == '(') {
                    operators.push(c);
                    unpairedBracketCount++;
                } else if (c == '|') {
                    operators.push(c);
                    concatFlag = false;
                }
            }
        }
        while (operators.size() > 0) {
            if (operands.empty()) {
                Logger.getGlobal().warning("括号不匹配");
                return null;
            }
            op = operators.pop();
            if (op == '$') {
                nfa2 = operands.pop();
                nfa1 = operands.pop();
                operands.push(concat(nfa1, nfa2));
            } else if (op == '|') {
                nfa2 = operands.pop();
                if (!operators.empty() && operators.peek() == '$') {
                    concatStack.push(operands.pop());
                    while (!operators.empty() && operators.peek() == '$') {
                        concatStack.push(operands.pop());
                        operators.pop();
                    }
                    nfa1 = concat(concatStack.pop(), concatStack.pop());
                    while (concatStack.size() > 0) {
                        nfa1 = concat(nfa1, concatStack.pop());
                    }
                } else {
                    nfa1 = operands.pop();
                }
                operands.push(union(nfa1, nfa2));
            }
        }
        return operands.pop();
    }

    public static NFA analyzeRe() {
        NFA nfa = new NFA();
        FileUtil f = new FileUtil();
        ArrayList<Pair<String, String>> res;
        res = f.readNfa();
        for (Pair<String, String> pair : res) {
            NFA tmp = compile(pair.getValue());
            if (tmp == null) {
                Logger.getGlobal().warning(String.format("NFA %s 解析结果异常", pair.getValue()));
                return null;
            }
            Vertex finalState = tmp.states.get(tmp.finalState);
            finalState.isFinal = true;
            finalState.type = pair.getKey();
            tmp.finalStates.add(tmp.finalState);
            nfa = union(nfa, tmp);
        }
        nfa.finalStates.add(nfa.finalState);
        return nfa;
    }

    public static void main(String[] args) {
        NFA nfa = Thompson.analyzeRe();
        System.out.println(nfa);
    }
}
