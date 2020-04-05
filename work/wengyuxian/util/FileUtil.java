package work.wengyuxian.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.Pair;

/**
 * fileUtil 文件工具类,提供读取和输出功能
 */
public class FileUtil {
    private String inputFilePath;
    private String outputFilePath;
    private String regularExpressionPath;
    
    Logger log = Logger.getGlobal();

    /**
     * 默认构造函数,读取路径为./input.txt 输出路径为./output.txt
     */
    public FileUtil() {
        inputFilePath = "./input.txt";
        outputFilePath = "./output.txt";
        regularExpressionPath = "./reg.txt";
    }

    /**
     * 
     * @param input  读取文件路径
     * @param output 输出文件路径
     */
    public FileUtil(String input, String output, String reg) {
        inputFilePath = input;
        outputFilePath = output;
        regularExpressionPath = reg;
    }

    /**
     * 读取文件
     * 
     * @param buffer 存放读取结果的缓冲区
     * @throws FileNotFoundException 读取文件不存在
     * @throws IOException           IO异常
     */
    public void readFile(StringBuffer buffer) throws FileNotFoundException, IOException {
        try (FileReader fileReader = new FileReader(inputFilePath);
                BufferedReader br = new BufferedReader(fileReader);) {
            String temp = null;
            // 读取所有内容,并补齐换行符
            while ((temp = br.readLine()) != null) {
                buffer.append(temp);
                buffer.append("\n");
            }
        }
        // 记录日志,继续抛出异常
        catch (FileNotFoundException e) {
            log.throwing("work.wengyuxian.fileUtil.FileUtil", "readFile", e);
            throw e;
        } catch (IOException e) {
            log.throwing("work.wengyuxian.fileUtil.FileUtil", "readFile", e);
            throw e;
        }
    }

    /**
     * 读取正则表达式,正规式写法:名称 正规式.如果不符合洗发,将不会被读取
     * 
     * @return 所有的正则表达式集合 形式为[<名称,表示式>,<名称,表示式>...]
     */
    public ArrayList<Pair<String, String>> readReg() {
        ArrayList<Pair<String, String>> res = new ArrayList<Pair<String, String>>();
        try (FileReader fileReader = new FileReader(regularExpressionPath);
                BufferedReader br = new BufferedReader(fileReader);) {
            String line = null;
            while ((line = br.readLine()) != null) {// 读取每行
                String pattern = "(.*)\\s+(.*)";// 正规式的写法为:名称 正规式,必须用空格分隔
                Matcher m = Pattern.compile(pattern).matcher(line);
                if (m.find()) {
                    String name = m.group(1);
                    String value = m.group(2);
                    Pair<String, String> re = new Pair<String, String>(name, value);
                    res.add(re);
                }
            }
        }
        // 记录日志
        catch (FileNotFoundException e) {
            log.throwing("work.wengyuxian.fileUtil.FileUtil", "readFile", e);
        } catch (IOException e) {
            log.throwing("work.wengyuxian.fileUtil.FileUtil", "readFile", e);
        }
        return res;
    }
}