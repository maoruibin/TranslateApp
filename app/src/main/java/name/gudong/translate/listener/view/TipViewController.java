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
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.util.SpUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class TipViewController{
    private WindowManager mWindowManager;
    private Context mContext;

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

    public void show(Result result,boolean isShowFavoriteButton) {
        TipView tipView = new TipView(mContext);
        mWindowManager.addView(tipView, getPopViewParams());
        tipView.startWithAnim();
        tipView.setContent(result, isShowFavoriteButton);
        //向 WindowManager 添加浮动窗
        closeTipViewCountdown(tipView);
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