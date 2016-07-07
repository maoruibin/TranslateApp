package name.gudong.translate.util;

import android.os.Build;

/**
 * Created by GuDong on 6/11/16 14:19.
 * Contact with ruibin.mao@moji.com.
 */
public class Utils {
    /**
     * 是否是5.0以上系统
     *
     * @return
     */
    public static boolean isSDKHigh5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
