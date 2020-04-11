package work.wengyuxian.util;

/**
 * TokenNode 用于描述Token的类
 */
public class TokenNode {
    // Token类型,例如ID,KEYWORD等
    private String type;
    // Token的值
    private String value;

    /**
     * 构造函数
     * 
     * @param aType  Token类型
     * @param aValue Token值
     */
    public TokenNode(String aType, String aValue) {
        type = aType;
        value = aValue;
    }

    /**
     * 更改Token的类型
     * 
     * @param aType Token类型
     */
    public void setType(String aType) {
        type = aType;
    }

    /**
     * 获取Token类型
     * 
     * @return Token类型
     */
    public String getType() {
        return type;
    }

    /**
     * 获取Token的值
     * 
     * @return Token的值
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%-14s: %-10s\n", type, value);
    }
}