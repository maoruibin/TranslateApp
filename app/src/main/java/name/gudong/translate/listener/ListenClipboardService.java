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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;

import javax.inject.Inject;

import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.presenters.ClipboardPresenter;
import name.gudong.translate.mvp.views.IClipboardService;
import name.gudong.translate.reject.components.DaggerServiceComponent;
import name.gudong.translate.reject.modules.ServiceModule;


public final class ListenClipboardService extends Service implements IClipboardService{
    private static final String KEY_FOR_WEAK_LOCK = "weak-lock";
    @Inject
    ClipboardPresenter mPresenter;

    @Override
    public void onCreate() {
        setUpInject();
        addListener();
        attachView();
        mPresenter.onCreate();
    }

    private void attachView() {
        mPresenter.attachView(this);
    }

    private void addListener() {
        mPresenter.addListener();

    }

    private void setUpInject() {
        DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(this))
                .appComponent(GDApplication.getAppComponent())
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
        //设置定时显示任务
        mPresenter.controlShowTipCyclic();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
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
}