package name.gudong.translate.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.presenters.MainPresenter;
import name.gudong.translate.ui.activitys.AboutActivity;
import name.gudong.translate.ui.activitys.MainActivity;
import name.gudong.translate.ui.activitys.SettingActivity;

/**
 * Created by GuDong on 3/9/16 14:01.
 * Contact with gudong.name@gmail.com.
 */
public class NavigationManager {
    public static void gotoSendEmail(Context context) {
        Uri uri = Uri.parse("mailto:gudong.name@gmail.com");
        final Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (infos == null || infos.size() <= 0) {
            return;
        }
        context.startActivity(intent);
    }

    public static void gotoSetting(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public static void gotoMainActivity(Context context, Result entity) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainPresenter.KEY_RESULT, entity);
        context.startActivity(intent);
    }
    public static void gotoAboutActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }
}
