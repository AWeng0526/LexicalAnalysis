package work.wengyuxian.thompson;

/**
 * VertexType
 * 顶点类型的枚举类
 */
public enum VertexType {
    ID, // 标识符
    KEYWORD, // 关键字
    NUMBER, // 数字
    SEPARATOR, // 分隔符
    BRACKET, // 括号
    COMMENT, // 评论
    ARI_OPERATOR, // 算术运算符
    lOG_OPERATOR, // 逻辑运算符
    EQUAL_OPERATOR, // 等于号
    COMP_OPERATOR, // 比较运算符
    WHITESPACE, // 空白符
    NEWLINE, // 换行符
    SIMICOLON, // 分号
    ERROR,// 错误符号

}