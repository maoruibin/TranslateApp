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
}
