package name.gudong.translate.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class HeadsUps {

    public static void show(Context context, Class<?> targetActivity, String title, String content, int largeIcon, int smallIcon, int code) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 11,
                new Intent(context, targetActivity), PendingIntent.FLAG_UPDATE_CURRENT);

    }
}