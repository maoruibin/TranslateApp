package name.gudong.translate.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import name.gudong.translate.ui.activitys.MainActivity;
import name.gudong.translate.util.HeadsUps;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.R;


/**
 * Created by drakeet on 7/1/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SpUtils.isNotifyDayline(context)) {
            HeadsUps.show(context, MainActivity.class,
                    context.getString(R.string.app_name),
                    "还没有查看今天的每日一句吧,我已经准备好了。",
                    R.mipmap.ic_launcher,
                    R.drawable.icon_notification,
                    123123);


//                Intent resultIntent = new Intent(mContext, MainActivity.class);
//                resultIntent.putExtra("flag",1);
//
//                PendingIntent resultPendingIntent = PendingIntent.getActivity(
//                        mContext,
//                        0,
//                        resultIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(mContext)
//                                .setSmallIcon(R.drawable.icon_notification)
//                                .setContentIntent(resultPendingIntent)
//                                .setContentTitle("")
//                                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
//                                .setVibrate(new long[]{0l})
//                                .setPriority(Notification.PRIORITY_HIGH)
//                                .setContentText("还没有查看今天的每日一句吧,我已经准备好了。");
//
//                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//                Notification note = mBuilder.build();
//                mNotificationManager.notify(this.hashCode(), note);
            }

    }
}
