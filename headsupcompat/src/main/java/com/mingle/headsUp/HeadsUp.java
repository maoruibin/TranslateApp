package com.mingle.headsUp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by zzz40500 on 2014/10/9.
 */
public class HeadsUp   {

    private Context context;
    /**
     * 出现时间  单位是 second
     */
    private long duration= 9;
    /**
     *
     */
    private Notification notification;

    private Builder builder;

    private boolean isSticky=false;


    private boolean activateStatusBar=true;


    /**
     *
     */

    private Notification silencerNotification;
    /**
     * 间隔时间
     */
    private long interval=600 ;
    private int code;
    private List<NotificationCompat.Action> actions;
    private CharSequence titleStr;
    private CharSequence msgStr;
    private int icon;
    private View customView;
    private boolean isExpand;
    private HeadsUp(Context context) {
        this.context=context;
    }





    public static class Builder  extends  NotificationCompat.Builder {

        private List<NotificationCompat.Action> actions=new ArrayList<NotificationCompat.Action>();
        private HeadsUp headsUp;

        public Builder(Context context) {
            super(context);
            headsUp=new HeadsUp(context);
        }
        /**
         * 显示全部界面
         * @param isExpand
         */
        public Builder setUsesChronometer(boolean isExpand){
            headsUp.setExpand(isExpand);
            return this;
        }
        /**
         * Set the first line of text in the platform notification template.
         */
        public Builder setContentTitle(CharSequence title) {
            headsUp.setTitle(title);
            super.setContentTitle(title);
            return this;
        }

        /**
         * Set the second line of text in the platform notification template.
         */
        public Builder setContentText(CharSequence text) {
            headsUp.setMessage(text);
            super.setContentText(text);
            return this;
        }
        public Builder setSmallIcon(int icon) {
            headsUp.setIcon(icon);
//            super.setSmallIcon(icon);
            return this;
        }
        protected   Builder setIcon(int icon){
            super.setSmallIcon(icon);
            return this;
        }



        public Builder setSticky(boolean isSticky){
            headsUp.setSticky(isSticky);
            return this;
        }




        @Override
        public Builder addAction(int icon, CharSequence title, PendingIntent intent) {
            NotificationCompat.Action action=new NotificationCompat.Action(icon, title, intent);
            actions.add(action);
            super.addAction(icon, title, intent);
            return this;
        }




        public HeadsUp buildHeadUp(){
            headsUp.setNotification(this.build());
            headsUp.setActions(actions);
            headsUp.setBuilder(this);
            return  headsUp;
        }

        private   Notification silencerNotification(){
            super.setSmallIcon(headsUp.getIcon());
            setDefaults(0);
            return this.build();
        }

        @Override
        public Builder setSmallIcon(int icon, int level) {
            setSmallIcon(icon);

            return this;
        }

        @Override
        public Builder setAutoCancel(boolean autoCancel) {
             super.setAutoCancel(autoCancel);
            return this;

        }

        @Override
        public Builder setColor(int argb) {
             super.setColor(argb);
            return this;
        }

        @Override
        public Builder setDefaults(int defaults) {
             super.setDefaults(defaults);
            return this;
        }

        @Override
        public Builder setFullScreenIntent(PendingIntent intent, boolean highPriority) {
             super.setFullScreenIntent(intent, highPriority);
            return this;
        }

        @Override
        public Builder setOngoing(boolean ongoing) {
             super.setOngoing(ongoing);
            return this;
        }


        @Override
        public Builder setVibrate(long[] pattern) {
             super.setVibrate(pattern);
            return  this;
        }


        @Override
        public Builder setLargeIcon(Bitmap icon) {
             super.setLargeIcon(icon);
            return this;
        }

        @Override
        public Builder setLights(int argb, int onMs, int offMs) {
             super.setLights(argb, onMs, offMs);
            return this;
        }

        @Override
        public Builder setWhen(long when) {
             super.setWhen(when);
            return this;
        }

        @Override
        public Builder setShowWhen(boolean show) {
             super.setShowWhen(show);
            return this;
        }



        @Override
        public Builder setSubText(CharSequence text) {
             super.setSubText(text);
            return this;
        }

        @Override
        public Builder setNumber(int number) {
             super.setNumber(number);
            return  this;
        }

        @Override
        public Builder setContentInfo(CharSequence info) {
             super.setContentInfo(info);
            return this;
        }

        @Override
        public Builder setProgress(int max, int progress, boolean indeterminate) {
             super.setProgress(max, progress, indeterminate);
            return this;
        }

        @Override
        public Builder setContent(RemoteViews views) {
             super.setContent(views);
            return this;
        }

        @Override
        public Builder setContentIntent(PendingIntent intent) {
             super.setContentIntent(intent);
            return this;
        }

        @Override
        public Builder setDeleteIntent(PendingIntent intent) {
             super.setDeleteIntent(intent);
            return this;
        }

        @Override
        public Builder setTicker(CharSequence tickerText) {
             super.setTicker(tickerText);
            return this;
        }

        @Override
        public Builder setTicker(CharSequence tickerText, RemoteViews views) {
             super.setTicker(tickerText, views);
            return this;
        }

        @Override
        public Builder setSound(Uri sound) {
            super.setSound(sound);
            return this;
        }

        @Override
        public Builder setSound(Uri sound, int streamType) {
             super.setSound(sound, streamType);
            return this;
        }

        @Override
        public Builder setOnlyAlertOnce(boolean onlyAlertOnce) {
             super.setOnlyAlertOnce(onlyAlertOnce);
            return this;
        }

        @Override
        public Builder setLocalOnly(boolean b) {
             super.setLocalOnly(b);
            return this;
        }

        @Override
        public Builder setCategory(String category) {
             super.setCategory(category);
            return this;
        }

        @Override
        public Builder setPriority(int pri) {
             super.setPriority(pri);
            return  this;
        }

        @Override
        public Builder addPerson(String uri) {
             super.addPerson(uri);
            return  this;
        }

        @Override
        public Builder setGroup(String groupKey) {
             super.setGroup(groupKey);
            return this;
        }

        @Override
        public Builder setGroupSummary(boolean isGroupSummary) {
             super.setGroupSummary(isGroupSummary);
            return this;
        }

        @Override
        public Builder setSortKey(String sortKey) {
             super.setSortKey(sortKey);
            return  this;
        }

        @Override
        public Builder addExtras(Bundle extras) {
             super.addExtras(extras);
            return this;
        }

        @Override
        public Builder setExtras(Bundle extras) {
             super.setExtras(extras);
            return this;
        }

        @Override
        public Builder addAction(NotificationCompat.Action action) {
            actions.add(action);
             super.addAction(action);
            return this;
        }

        @Override
        public Builder setStyle(NotificationCompat.Style style) {
             super.setStyle(style);
            return  this;
        }

        @Override
        public Builder setVisibility(int visibility) {
             super.setVisibility(visibility);
            return this;
        }

        @Override
        public Builder setPublicVersion(Notification n) {
             super.setPublicVersion(n);
            return this;
        }


    }



    protected void setIcon(int dRes) {
        icon = dRes;
    }


    /**
     * 设置消息标题
     *
     * @param titleStr
     */
    protected void setTitle(CharSequence titleStr) {
        this.titleStr = titleStr;
    }

    /**
     * 设置消息内容
     *
     * @param msgStr
     */
    protected void setMessage(CharSequence msgStr) {
        this.msgStr = msgStr;
    }



    public Context getContext() {
        return context;
    }

    public long getDuration() {
        return duration;
    }


    public long getInterval() {
        return interval;
    }




   public CharSequence getTitleStr() {
        return titleStr;
    }

    public CharSequence getMsgStr() {
        return msgStr;
    }

    public int getIcon() {
        return icon;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }




    public Notification getNotification() {
        return notification;
    }

    protected void setNotification(Notification notification) {
        this.notification = notification;
    }


    public View getCustomView() {
        return customView;
    }

    public void setCustomView(View customView) {
        this.customView = customView;
    }

    public int getCode() {
        return code;
    }

    protected void setCode(int code) {
        this.code = code;
    }

    protected List<NotificationCompat.Action> getActions() {
        return actions;
    }

    protected void setActions(List<NotificationCompat.Action> actions) {
        this.actions = actions;
    }

    protected boolean isExpand() {
        return isExpand;
    }

    protected void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    protected Notification getSilencerNotification() {
        return getBuilder().silencerNotification();
    }



    protected Builder getBuilder() {
        return builder;
    }

    private void setBuilder(Builder builder) {
        this.builder = builder;
    }


    public boolean isSticky() {
        return isSticky;
    }

    public void setSticky(boolean isSticky) {
        this.isSticky = isSticky;
    }


    protected boolean isActivateStatusBar() {
        return activateStatusBar;
    }

    public void setActivateStatusBar(boolean activateStatusBar) {
        this.activateStatusBar = activateStatusBar;
    }
}
