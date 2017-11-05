package name.gudong.translate.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import name.gudong.translate.R;
import name.gudong.translate.ui.activitys.MainActivity;

/**
 * Created by GuDong on 6/11/16 14:19.
 * Contact with ruibin.mao@moji.com.
 */
public class Utils {
    private static final int NOTIFY_ID = 524947901;

    /**
     * 是否是5.0以上系统
     *
     * @return
     */
    public static boolean isSDKHigh5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 是否是5.0以上系统
     *
     * @return
     */
    public static boolean isAndroidM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static void showNormalNotification(Context context) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setContentText("点击打开咕咚翻译");
        if (Utils.isSDKHigh5()) {
            builder.setSmallIcon(R.drawable.icon_notification);
            builder.setColor(Color.rgb(121, 85, 72));
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setOngoing(true);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        PendingIntent contextIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(contextIntent);

        long[] vibrate = {0, 50, 0, 0};
        builder.setVibrate(vibrate);

        Notification notification = builder.build();
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(NOTIFY_ID, notification);
    }

    public static void cancelNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(NOTIFY_ID);
    }

    public static String getVersionName(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
            return null;
        }
    }

    public static void openUrl(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static int[] getCurrentTime() {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        int[] time = new int[3];
        time[0] = now.get(Calendar.HOUR_OF_DAY);
        time[1] = now.get(Calendar.MINUTE);
        time[2] = now.get(Calendar.SECOND);
        return time;
    }

    public static void shareText(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "发送"));
    }

    /**
     * check test string is json format or not
     *
     * @param test content
     * @return true if it is format by json else return false
     */
    public static boolean isJSONFormat(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
