package name.gudong.translate.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import name.gudong.translate.R;
import moe.feng.alipay.zerosdk.AlipayZeroSdk;
import name.gudong.translate.ui.activitys.MainActivity;
import name.gudong.translate.widget.WebDialog;

/**
 * TODO 根据 http://www.jianshu.com/p/af6499abd5c2 这篇文章优化 DialogFragment
 * Created by GuDong on 3/1/16 14:47.
 * Contact with gudong.name@gmail.com.
 */
public class DialogUtil {
    public static void showAbout(AppCompatActivity activity, String title) {
        int accentColor = activity.getResources().getColor(R.color.colorAccent);
        WebDialog.create(title, "about.html", accentColor)
                .show(activity.getSupportFragmentManager(), "about");
    }

    public static void showAboutDonate(AppCompatActivity activity) {
        int accentColor = activity.getResources().getColor(R.color.colorAccent);
        WebDialog.create("关于捐赠", "about_donate.html", accentColor)
                .show(activity.getSupportFragmentManager(), "about");
    }

    public static void showChangelog(AppCompatActivity activity) {
        int accentColor = activity.getResources().getColor(R.color.colorAccent);
        WebDialog.create("更新日志", "changelog.html", accentColor)
                .show(activity.getSupportFragmentManager(), "changelog");
        ;
    }

    public static void showGuideFloatTranslate(AppCompatActivity activity) {
        showCustomDialogWithTwoAction(activity, activity.getSupportFragmentManager(),
                "什么是划词翻译",
                "about_float_translate.html",
                "introduce",
                "已了解",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SpUtils.setHasShowGuideFlag(activity, true);
                    }
                },
                "查看介绍文章",
                (dialog, which) -> {
                    Utils.openUrl(activity, "http://gudong.name/product/2016/02/26/gudong_translate.html");
                });

    }

    public static void showSupport(AppCompatActivity activity) {
        showCustomDialogWithTwoAction(activity, activity.getSupportFragmentManager(),
                "支持开发者", "donate_ch.html", "donate",
                "关闭", ((dialog1, which1) -> MobclickAgent.onEvent(activity, "menu_support_close")),
                "打开支付宝转账页面", (dialog, which) -> {
                    MobclickAgent.onEvent(activity, "menu_support_click");
                    if (AlipayZeroSdk.hasInstalledAlipayClient(activity)) {
                        AlipayZeroSdk.startAlipayClient(activity, "aex07094cljuqa36ku7ml36");
                    } else {
                        MobclickAgent.onEvent(activity, "menu_support_click_but_have_not_alipay");
                        Toast.makeText(activity, activity.getString(R.string.support_fail_because_not_install), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static void showCustomDialogWithTwoAction(
            Context context, FragmentManager fragmentManager,
            String dialogTitle, String htmlFileName, String tag,
            String positiveText, DialogInterface.OnClickListener positiveListener,
            String neutralText, DialogInterface.OnClickListener neutralListener) {
        int accentColor = context.getResources().getColor(R.color.colorAccent);
        WebDialog.create(dialogTitle, htmlFileName, accentColor, positiveText, positiveListener, neutralText, neutralListener)
                .show(fragmentManager, tag);
    }
}
