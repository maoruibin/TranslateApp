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

package name.gudong.translate.listener;

import android.animation.Animator;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.View;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import javax.inject.Inject;

import name.gudong.translate.GDApplication;
import name.gudong.translate.listener.view.TipView;
import name.gudong.translate.listener.view.TipViewController;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.presenters.BasePresenter;
import name.gudong.translate.mvp.presenters.ClipboardPresenter;
import name.gudong.translate.mvp.views.ITipFloatView;
import name.gudong.translate.reject.components.DaggerActivityComponent;
import name.gudong.translate.reject.modules.ActivityModule;
import name.gudong.translate.util.SpUtils;


public final class ListenClipboardService extends Service implements ITipFloatView, TipView.ITipViewListener {
    private static final String KEY_FOR_WEAK_LOCK = "weak-lock";
    @Inject
    ClipboardPresenter mPresenter;
    @Inject
    TipViewController mTipViewController;

    BroadcastReceiver mScreenStatusReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Logger.i("锁屏了");
                closeTipCyclic();
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                Logger.i("开屏了 解锁");
                openTipCyclic();
            }
        }
    };

    @Override
    public void onCreate() {
        setUpInject();
        addListener();
        attachView();
        mPresenter.onCreate();
    }

    private void registerScreenReceiver() {
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_USER_PRESENT);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceive, screenStateFilter);
    }

    private void unregisterScreenReceiver(){
        unregisterReceiver(mScreenStatusReceive);
    }

    private void attachView() {
        mPresenter.attachView(this);
    }

    private void addListener() {
        mPresenter.addListener();
    }

    private void setUpInject() {
        DaggerActivityComponent.builder()
                .appComponent(GDApplication.getAppComponent())
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra(KEY_FOR_WEAK_LOCK, false)) {
                BootCompletedReceiver.completeWakefulIntent(intent);
            }
        }
        boolean isOpen = SpUtils.getReciteOpenOrNot(this);
        if(isOpen){
            openTipCyclic();
            registerScreenReceiver();
        }else {
            closeTipCyclic();
            unregisterScreenReceiver();
        }
        return START_STICKY;
    }

    //设置定时显示任务
    private void openTipCyclic(){
        mPresenter.openTipCyclic();
    }

    //取消定时显示任务
    private void closeTipCyclic(){
        mPresenter.removeTipCyclic();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ProcessBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        unregisterScreenReceiver();
    }

    public static void start(Context context) {
        Intent serviceIntent = new Intent(context, ListenClipboardService.class);
        context.startService(serviceIntent);
    }

    public static void startForWeakLock(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, ListenClipboardService.class);
        context.startService(serviceIntent);

        intent.putExtra(ListenClipboardService.KEY_FOR_WEAK_LOCK, true);
        Intent myIntent = new Intent(context, ListenClipboardService.class);

        // using wake lock to start service
        WakefulBroadcastReceiver.startWakefulService(context, myIntent);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void errorPoint(String error) {
        mTipViewController.showErrorInfo(error,this);
    }

    @Override
    public void showResult(Result result, boolean isShowFavorite) {
        mTipViewController.show(result, isShowFavorite, this);
        if(SpUtils.isPlaySoundAuto(this)){
            mPresenter.playSound(result.getMp3FileName(),result.getEnMp3());
        }
    }

    @Override
    public void initWithFavorite(Result result) {
        mTipViewController.setWithFavorite(result);
    }

    @Override
    public void initWithNotFavorite(Result result) {
        mTipViewController.setWithNotFavorite(result);
    }

    @Override
    public void onClickFavorite(View view, Result result) {
        MobclickAgent.onEvent(this, "favorite_service");
        mPresenter.startFavoriteAnim(view, new BasePresenter.AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPresenter.clickFavorite(view,result);
            }
        });
    }

    @Override
    public void onClickPlaySound(View view, Result result) {
        MobclickAgent.onEvent(this, "sound_service");
        mPresenter.playSound(result.getMp3FileName(),result.getEnMp3());
        mPresenter.startSoundAnim(view);
    }

    @Override
    public void onClickTipFrame(View view, Result result) {
        mPresenter.jumpMainActivity(result);
        removeTipView(result);
    }

    @Override
    public void onInitFavorite(ImageView mIvFavorite, Result result) {
        mPresenter.initFavoriteStatus(result);
    }

    @Override
    public void removeTipView(Result result) {
        mTipViewController.removeTipView(result);
    }

    @Override
    public void onRemove() {

    }

    public class ProcessBinder extends Binder {
        /**
         * 获取当前Service的实例
         * @return
         */
        public ListenClipboardService getService(){
            return ListenClipboardService.this;
        }
    }
}