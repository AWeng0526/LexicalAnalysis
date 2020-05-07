package work.wengyuxian.util;

import java.util.Arrays;
import java.util.HashSet;

public class Alphabets {

    // 字母表
    public static HashSet<Character> alphabets = new HashSet<Character>(Arrays.asList('0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'd', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_', '.', '+', '-', '*', '/', '&',
            '!', '\\', ':', '(', ')', '[', ']', '?', '{', '}', '>', '<', '=', ';', ',', ' ', '\n', '#'));

    // 关键字
    public static HashSet<String> keyWords = new HashSet<>(Arrays.asList("void", "char", "int", "float", "double",
            "boolean", "short", "long", "signed", "unsigned", "struct", "union", "enum", "typedef", "sizeof", "auto",
            "static", "register", "extern", "const", "volatile", "return", "continue", "break", "goto", "if", "else",
            "switch", "case", "default", "for", "do", "while"));

    // 屏蔽字段
    public static HashSet<String> shield = new HashSet<>(Arrays.asList("WHITESPACE", "NEWLINE"));
}