
package work.wengyuxian.thompson;

import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Logger;
import javafx.util.Pair;
import work.wengyuxian.util.*;

public class Thompson {

    public static final String digit = "(0|1|2|3|4|5|6|7|8|9)";
    public static final String letter = "(a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z|_)";

    /**
     * 判断字符是否在字母表中,#代表空串
     * 
     * @param c 需判断的字符
     * @return
     */
    public static boolean alphabet(char c) {
        return Alphabets.alphabets.contains(c);
    }

    /**
     * 判断字符是否是运算符
     * 
     * @param c 需判断的字符
     * @return
     */
    public static boolean regexOperator(char c) {
        return c == '(' || c == ')' || c == '*' || c == '|' || c == '+';
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
        if (regex.isEmpty()) {
            return false;
        }
        for (char c : regex.toCharArray())
            if (!validRegExChar(c)) {
                Logger.getGlobal().warning("error char:" + c);
                return false;
            }
        return true;
    }

    /**
     * *闭包
     * 
     * @param n 操作数
     * @return 返回*闭包的结果
     */
    public static NFA kleene(NFA n) {
        // 构造新NFA,因为增添新初态终态,故容量+2
        NFA result = new NFA(n.states.size() + 2);
        // 添加新初态
        result.transitions.add(new Trans(0, 1, '#'));

        // 复制已有状态转移
        for (Trans t : n.transitions) {
            result.transitions.add(new Trans(t.stateFrom + 1, t.stateTo + 1, t.transSymbol));
        }

        // 将原来终态指向新终态
        result.transitions.add(new Trans(n.states.size(), n.states.size() + 1, '#'));

        // 添加剩余两条状态转移
        result.transitions.add(new Trans(n.states.size(), 1, '#'));
        result.transitions.add(new Trans(0, n.states.size() + 1, '#'));

        // 设置新的终态结点索引 n.states.size()+2-1
        result.finalState = n.states.size() + 1;
        return result;
    }

    /**
     * 正闭包
     * 
     * @param n 操作数
     * @return 正闭包的结果
     */
    public static NFA positive(NFA n) {
        // 构造新NFA,因为增添新初态终态,故容量+2
        NFA result = new NFA(n.states.size() + 2);
        // 添加新初态
        result.transitions.add(new Trans(0, 1, '#'));

        // 复制已有状态转移
        for (Trans t : n.transitions) {
            result.transitions.add(new Trans(t.stateFrom + 1, t.stateTo + 1, t.transSymbol));
        }

        // 将原来终态指向新终态
        result.transitions.add(new Trans(n.states.size(), n.states.size() + 1, '#'));

        // 添加剩余一条状态转移
        result.transitions.add(new Trans(n.states.size(), 1, '#'));

        // 设置新的终态结点索引 n.states.size()+2-1
        result.finalState = n.states.size() + 1;
        return result;
    }

    /**
     * NFA的连接运算
     * 
     * @param n 操作数
     * @param m 操作数
     * @return 返回n.m的结果
     */
    public static NFA concat(NFA n, NFA m) {
        // 删除m的初态(与n的终态合并)
        m.states.remove(0);

        // 复制m中状态转移
        for (Trans t : m.transitions) {
            n.transitions
                    .add(new Trans(t.stateFrom + n.states.size() - 1, t.stateTo + n.states.size() - 1, t.transSymbol));
        }

        // 复制m中顶点,state需加上一个偏移量,偏移量=n.finalState
        for (Vertex s : m.states) {
            n.states.add(new Vertex(s.state + n.finalState));
        }

        // 计算新的终态索引
        n.finalState = n.finalState + m.finalState;
        return n;
    }

    /**
     * |运算
     * 
     * @param m 操作数
     * @param n 操作数
     * @return m|n的运算结果
     */
    public static NFA union(NFA m, NFA n) {
        // 若有空NFA,直接返回另一NFA即可
        if (m.finalState == 0) {
            return n;
        }
        if (n.finalState == 0) {
            return m;
        }

        // |运算生成的新NFA中会新增两个状态(新初态,新终态)
        NFA result = new NFA(n.states.size() + m.states.size() + 2);

        // 新增初态,同时该初态指向n初态
        result.transitions.add(new Trans(0, 1, '#'));

        // 复制n中状态转移
        for (Trans t : n.transitions) {
            result.transitions.add(new Trans(t.stateFrom + 1, t.stateTo + 1, t.transSymbol));
        }

        // 将n中终态指向新终态
        result.transitions.add(new Trans(n.states.size(), n.states.size() + m.states.size() + 1, '#'));

        // 新初态连接至m初态
        result.transitions.add(new Trans(0, n.states.size() + 1, '#'));

        // 复制m中状态转移,所有编号需要偏移,偏移量为n中数量+1
        int offset = n.states.size() + 1;
        for (Trans t : m.transitions) {
            result.transitions.add(new Trans(t.stateFrom + offset, t.stateTo + offset, t.transSymbol));
        }

        // m的终态转移至新终态
        result.transitions
                .add(new Trans(m.states.size() + n.states.size(), n.states.size() + m.states.size() + 1, '#'));

        // 计算新终态索引
        result.finalState = n.states.size() + m.states.size() + 1;
        return result;
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
        Stack<NFA> concatStack = new Stack<NFA>();// 连接辅助栈
        boolean concatFlag = false; // 连接标识符
        char op, c; // 当前字符
        int unpairedBracketCount = 0;// 用于计算未配对的括号符号
        boolean escape = false;// 是否被转义
        NFA nfa1, nfa2;

        for (int i = 0; i < regex.length(); i++) {
            c = regex.charAt(i);
            if (escape) { // 如果转义
                operands.push(new NFA(c));
                // 填充连接运算符
                if (concatFlag) {
                    operators.add('.');
                }
                concatFlag = true;
                // 转义结束
                escape = false;
            } else if (c == '\\') {// 开始转义
                escape = true;
            } else if (!regexOperator(c)) {// 如果是字母表中字符
                operands.push(new NFA(c));// 操作数入栈
                if (concatFlag) {
                    operators.push('.'); // 填充连接标识符
                } else {
                    concatFlag = true;
                }
            } else {// 之前是操作数相关操作,现在开始操作符相关操作
                if (c == '*') {// 操作数出栈,求*闭包后入栈
                    operands.push(kleene(operands.pop()));
                    concatFlag = true;
                } else if (c == '+') {// 正闭包
                    operands.push(positive(operands.pop()));
                    concatFlag = true;
                } else if (c == '(') {// 操作符入栈,计数器+1
                    // 这里还需考虑一种情况:(...)(...)
                    // 当两个括号相连时,需补充连接操作符
                    if (concatFlag) {
                        operators.add('.');
                    }
                    concatFlag = false;
                    operators.push(c);
                    unpairedBracketCount++;
                } else if (c == '|') {// 操作符入栈,停止连接运算
                    operators.push(c);
                    concatFlag = false;
                } else if (c == ')') {// 计算(...)中所有表达式
                    concatFlag = false;// 停止连接运算
                    if (unpairedBracketCount == 0) {// 检查括号是否匹配
                        Logger.getGlobal().warning("括号不匹配");
                        return null;
                    } else {
                        unpairedBracketCount--;
                    }
                    // 将所有操作符出栈,直到"("
                    // 严格来说,不需要判断非空,因为已经完成了"("的检验,所以一定会在栈空之前结束
                    while (!operators.empty() && operators.peek() != '(') {
                        op = operators.pop();// 弹出操作符
                        if (op == '.') {// 连接运算
                            nfa2 = operands.pop();
                            nfa1 = operands.pop();
                            operands.push(concat(nfa1, nfa2));
                        } else if (op == '|') {// |运算
                            // nfa1 nfa2代表|运算左右的操作数,即 nfa1 | nfa2
                            nfa2 = operands.pop();
                            // 由于|运算优先级高于.,所以可能出现 a.b.c.d|e.f.g的情况
                            if (!operators.empty() && operators.peek() == '.') {
                                // 将所有需连接的NFA入栈
                                concatStack.push(operands.pop());
                                while (!operators.empty() && operators.peek() == '.') {
                                    concatStack.push(operands.pop());
                                    operators.pop();
                                }
                                // 进行连接操作
                                nfa1 = concat(concatStack.pop(), concatStack.pop());
                                while (concatStack.size() > 0) {
                                    nfa1 = concat(nfa1, concatStack.pop());
                                }
                            } else {// 取出左操作数
                                nfa1 = operands.pop();
                            }
                            operands.push(union(nfa1, nfa2));
                        }
                    }
                    operators.pop();// 弹出(
                    concatFlag = true;// 重置连接标识符
                }
            }
        }
        // 将所有操作数录入完成后,开始求值
        // 如果觉得这部分内容与处理)部分内容类似过于冗余的话
        // 也可以要求正规式必须用()圈起来,这样就不需要这段代码了
        while (operators.size() > 0) {
            if (operands.empty()) {
                // 如果有操作符而无操作数,记录错误信息
                Logger.getGlobal().warning("符号不匹配");
                return null;
            }
            op = operators.pop();
            // 同之前代码,此处不做注解
            if (op == '.') {
                nfa2 = operands.pop();
                nfa1 = operands.pop();
                operands.push(concat(nfa1, nfa2));
            } else if (op == '|') {
                nfa2 = operands.pop();
                if (!operators.empty() && operators.peek() == '.') {
                    concatStack.push(operands.pop());
                    while (!operators.empty() && operators.peek() == '.') {
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

    /**
     * 主调函数
     * 
     * @param f 文件信息类
     * @return nfa集合
     */
    public static ArrayList<NFA> analyzeRe(FileUtil f) {
        ArrayList<NFA> ans = new ArrayList<>();
        // 默认路径为 ./reg.txt
        ArrayList<Pair<String, String>> res;
        res = f.readReg();
        for (Pair<String, String> pair : res) {
            // 正规式解析
            NFA tmp = compile(pair.getValue().replace("{digit}", digit).replace("{letter}", letter));
            if (tmp == null) {
                Logger.getGlobal().warning(String.format("NFA %s 解析结果异常", pair.getValue()));
                return null;
            }
            // 给该正规式终态补充信息
            Vertex finalState = tmp.states.get(tmp.finalState);
            finalState.isFinal = true;
            finalState.type = pair.getKey();
            ans.add(tmp);
        }

        return ans;
    }

    public static void main(String[] args) {
        ArrayList<NFA> nfas = Thompson.analyzeRe(new FileUtil());
        System.out.println(nfas.get(0));
    }
}
