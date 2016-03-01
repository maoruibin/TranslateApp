package name.gudong.translate.util;

import android.support.v7.app.AppCompatActivity;

import me.gudong.translate.R;
import name.gudong.translate.widget.WebDialog;

/**
 * TODO 根据 http://www.jianshu.com/p/af6499abd5c2 这篇文章优化 DialogFragment
 * Created by GuDong on 3/1/16 14:47.
 * Contact with gudong.name@gmail.com.
 */
public class DialogUtil {
    public static void showAbout(AppCompatActivity activity){
        WebDialog.show(activity, activity.getSupportFragmentManager(), "关于", "about.html", "about", R.color.colorAccent);
    }

    public static void showChangelog(AppCompatActivity activity){
        WebDialog.show(activity, activity.getSupportFragmentManager(), "更新日志", "changelog.html", "changelog", R.color.colorAccent);
    }
}
