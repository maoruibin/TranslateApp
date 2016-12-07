package name.gudong.translate.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import name.gudong.translate.listener.AlarmReceiver;

public class AlarmManagers {

    public static void register(Context context) {
        Calendar today = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY, 8);
        today.set(Calendar.MINUTE, 30);
        today.set(Calendar.SECOND, 30);

        if (now.after(today)) {
            Toast.makeText(context, "return", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context, "normal", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent("name.gudong.translate.alarm");
        intent.setClass(context, AlarmReceiver.class);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 520, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+20*1000, broadcast);
        manager.set(AlarmManager.RTC_WAKEUP, today.getTimeInMillis(), broadcast);
    }
}