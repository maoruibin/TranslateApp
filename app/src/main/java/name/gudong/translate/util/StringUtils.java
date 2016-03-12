package name.gudong.translate.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GuDong on 3/7/16 13:43.
 * Contact with gudong.name@gmail.com.
 */
public class StringUtils {
    public static boolean isContainChinese(String str) {
        Pattern p=Pattern.compile("[u4e00-u9fa5]");
        Matcher m=p.matcher(str);
        if(m.find())return true;
        return false;
    }

    /**
     * 检查输入的单词是不是超过一个
     * @param input 输入的字符串
     * @return true 如果输入的字符超过一个
     */
    public static boolean isMoreThanOneWord(String input) {
        String[] result1 = input.split(" ");
        String[] result2 = input.split(",");
        String[] result3 = input.split(".");
        String[] result4 = input.split("，");
        String[] result5 = input.split("。");
        String[] result6 = input.split("？");
        if (isMoreThanOne(result1) || isMoreThanOne(result2) || isMoreThanOne(result3) ||
                isMoreThanOne(result4) || isMoreThanOne(result5) || isMoreThanOne(result6) ) {
            return true;
        }
        return false;
    }

    private static boolean isMoreThanOne(String[] result) {
        return result.length > 1;
    }
}
