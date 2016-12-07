package com.mingle.headsUp;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;


import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by zzz40500 on 2014/10/9.
 */
public class HeadsUpManager  {

    private  WindowManager wmOne;
    private FloatView floatView;
    private Queue<HeadsUp> msgQueue;
    private static HeadsUpManager manager;
    private Context context;

    /**
     *
     */
    private boolean isPolling = false;

    private Map<Integer, HeadsUp> map;
    private   NotificationManager notificationManager=null;

    public static HeadsUpManager getInstant(Context c) {

        if (manager == null) {
            manager = new HeadsUpManager(c);

        }
        return manager;

    }

    private HeadsUpManager(Context context) {
        this.context = context;
        map = new HashMap<Integer, HeadsUp>();
        msgQueue = new LinkedList<HeadsUp>();
        wmOne = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public synchronized void notify(HeadsUp headsUp) {


        if (map.containsKey(headsUp.getCode())) {
            msgQueue.remove(map.get(headsUp.getCode()));
        }
        map.put(headsUp.getCode(), headsUp);
        msgQueue.add(headsUp);

        if (!isPolling) {
            poll();
        }
    }
    public synchronized void notify(int code,HeadsUp headsUp) {
        headsUp.setCode(code);
        notify(headsUp);

    }


    public synchronized void cancel(HeadsUp headsUp) {

        cancel(headsUp.getCode());
    }


    private synchronized void poll() {
        if (!msgQueue.isEmpty()) {
            HeadsUp headsUp = msgQueue.poll();
            map.remove(headsUp.getCode());


            if ( Build.VERSION.SDK_INT < 21   ||   headsUp.getCustomView() != null || !headsUp.isActivateStatusBar()){
                isPolling = true;
                show(headsUp);
            }else {
                //当 系统是 lollipop 以上,并且没有自定义布局以后,调用系统自己的 notification
                isPolling = false;
                notificationManager.notify(headsUp.getCode(),headsUp.getBuilder().setIcon(headsUp.getIcon()).build());

            }
        } else {
            isPolling = false;
        }
    }


    private void show(HeadsUp headsUp) {

        floatView = new FloatView(context, 20);
        WindowManager.LayoutParams params = FloatView.winParams;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = -3;
        params.gravity = Gravity.CENTER | Gravity.TOP;
        params.x = floatView.originalLeft;
        params.y = 0;
        params.alpha = 1f;
        wmOne.addView(floatView, params);
        ObjectAnimator a = ObjectAnimator.ofFloat(floatView.rootView, "translationY", -700, 0);
        a.setDuration(600);
        a.start();
        floatView.setNotification(headsUp);
        if(headsUp.getNotification()!=null ){
            notificationManager.notify(headsUp.getCode(), headsUp.getNotification());
        }


    }



    public void cancel(){

        if(floatView !=null && floatView.getParent()!=null) {

            floatView.cancel();
        }


    }

    protected void dismiss() {

        if (floatView.getParent()!=null) {
            wmOne.removeView(floatView);
            floatView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    poll();
                }
            }, 1000);
        }

    }

    protected   void  animDismiss(){

        if(floatView !=null && floatView.getParent()!=null){

            ObjectAnimator a = ObjectAnimator.ofFloat(floatView.rootView, "translationY", 0, -700);
            a.setDuration(700);
            a.start();


            a.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {


                    dismiss();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {


                }
            });
        }



    }

    protected  void  animDismiss(HeadsUp headsUp){


           if(floatView.getHeadsUp().getCode()==headsUp.getCode()){
               animDismiss();
           }

    }


    protected  void silencerNotify(HeadsUp headsUp){
        if(headsUp.getSilencerNotification()!=null){
            notificationManager.notify(headsUp.getCode(), headsUp.getSilencerNotification());
        }
    }

    public void cancel(int code) {
        if (map.containsKey(code)) {
            msgQueue.remove(map.get(code));
        }
        if(floatView!=null && floatView.getHeadsUp().getCode()==code){
            animDismiss();
        }

    }



    public void close() {
        cancelAll();
        manager = null;
    }


    public void cancelAll() {
        msgQueue.clear();
        if (floatView!=null && floatView.getParent()!=null) {
            animDismiss();
        }
    }
}
