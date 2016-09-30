/*
 *  Copyright (C) 2015 GuDong <gudong.name@gmail.com>
 *
 *  This file is part of GdTranslate
 *
 *  GdTranslate is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  GdTranslate is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with GdTranslate.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package name.gudong.translate.listener.view;

import android.animation.Animator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.WindowManager;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import me.gudong.translate.R;
import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.ui.activitys.MainActivity;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.Utils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class TipViewController{
    private WindowManager mWindowManager;
    private Context mContext;
    /**
     * cache mul tip view
     */
    private Map<Result,TipView>mMapTipView = new WeakHashMap<>();

    //顶部提示框
//    private TipView mTipView;

    public TipViewController(Context application) {
        mContext = application;
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showErrorInfo(String error){
        TipView tipView = new TipView(mContext);
        mWindowManager.addView(tipView, getPopViewParams());
        tipView.startWithAnim();
        tipView.error(error);
        closeTipViewCountdown(tipView);
    }

    private void closeTipViewCountdown(final TipView tipView) {
        int duration = SpUtils.getDurationTimeWay(GDApplication.mContext).getDurationTime();
        Observable.timer(duration, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        tipView.closeWithAnim(new TipView.OnAnimListener() {
                            @Override
                            public void onCloseAnimEnd(Animator animation) {
                                mWindowManager.removeView(tipView);
                            }
                        });
                        return null;
                    }
                })
                .subscribe();
    }

    public void show(Result result,boolean isShowFavoriteButton,TipView.IOperateTipView mListener) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isSettingUseSystemNotification = sharedPreferences.getBoolean("preference_show_float_view_use_system",false);
        if(Utils.isSDKHigh5() && isSettingUseSystemNotification){
            StringBuilder sb = new StringBuilder();
            for(String string:result.getExplains()){
                sb.append(string).append("\n");
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.drawable.icon_notification)
                            .setContentTitle(result.getQuery())
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                            .setVibrate(new long[]{0l})
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentText(sb.toString());

             /* Add Big View Specific Configuration */
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            // Moves events into the big view
            for (String string:result.getExplains()) {
                inboxStyle.addLine(string);
            }

            mBuilder.setStyle(inboxStyle);

            Intent resultIntent = new Intent(mContext, MainActivity.class);
            resultIntent.putExtra("data",result);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                            mContext,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );


            mBuilder.addAction(R.drawable.ic_favorite_border_grey_24dp,"收藏",resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note = mBuilder.build();
            mNotificationManager.notify(result.getQuery().hashCode(), note);

        }else{
            TipView tipView = new TipView(mContext);
            mMapTipView.put(result,tipView);
            tipView.setListener(mListener);
            mWindowManager.addView(tipView, getPopViewParams());
            tipView.startWithAnim();
            tipView.setContent(result, isShowFavoriteButton);
            //向 WindowManager 添加浮动窗
            closeTipViewCountdown(tipView);
        }
    }

    public void setWithFavorite(Result result){
        TipView tipView = mMapTipView.get(result);
        if(tipView != null){
            tipView.setFavoriteBackground(R.drawable.ic_favorite_pink_24dp);
        }
    }

    public void setWithNotFavorite(Result result){
        TipView tipView = mMapTipView.get(result);
        if(tipView != null){
            tipView.setFavoriteBackground(R.drawable.ic_favorite_border_white_24dp);
        }
    }


    private WindowManager.LayoutParams getPopViewParams() {
        int w = WindowManager.LayoutParams.MATCH_PARENT;
        int h = WindowManager.LayoutParams.WRAP_CONTENT;

        int flags = 0;
        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(w, h, type, flags, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        layoutParams.gravity = Gravity.CENTER | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        return layoutParams;
    }
}