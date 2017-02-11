package name.gudong.translate.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import name.gudong.translate.R;
import name.gudong.translate.ui.activitys.MainActivity;
import name.gudong.translate.util.HeadsUps;
import name.gudong.translate.util.SpUtils;


/**
 * Created by drakeet on 7/1/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SpUtils.isNotifyDayline(context)) {
            Intent target = new Intent(context, MainActivity.class);
            target.putExtra("from_dayline_remind",true);
            HeadsUps.show(context, target,
                    context.getString(R.string.app_name),
                    "还没有查看今天的每日一句吧,我已经准备好了。",
                    R.mipmap.ic_launcher,
                    R.drawable.icon_notification,
                    123123);
            }

    }
}
