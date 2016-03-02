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
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import me.gudong.translate.R;
import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.ViewUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class TipViewController implements View.OnClickListener, View.OnTouchListener {
    private static final int DURATION_TIME = 300;

    private WindowManager mWindowManager;

    private Context mContext;
    private ViewDismissHandler mViewDismissHandler;

    //顶部提示框
    private LinearLayout mHeadsUpView;
    private RelativeLayout mContentView;
    private TextView mTvSrc;
    private LinearLayout mLlDst;
    private Button mButtonFavorite;

    public TipViewController(Context application) {
        mContext = application;
        mWindowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);

        LinearLayout view = (LinearLayout) View.inflate(mContext, R.layout.pop_view, null);

        // display content
        mTvSrc = (TextView) view.findViewById(R.id.tv_pop_src);
        mLlDst = (LinearLayout) view.findViewById(R.id.ll_pop_dst);
        mButtonFavorite = (Button) view.findViewById(R.id.bt_action);

        mHeadsUpView = view;
        mContentView = (RelativeLayout) view.findViewById(R.id.pop_view_content_view);

        // event listeners
        mContentView.setOnClickListener(this);
        mButtonFavorite.setOnClickListener(this);
    }

    public void setViewDismissHandler(ViewDismissHandler viewDismissHandler) {
        mViewDismissHandler = viewDismissHandler;
    }

    public void setResultContent(Result result) {
        setQuery(result.getQuery());
        mButtonFavorite.setTag(result);
        List<String> temp = result.getExplains();
        if (temp.isEmpty()) {
            temp = result.getTranslation();
        }
        Observable.from(temp)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        addExplain(s);
                    }
                });
    }

    private void addExplain(String explains) {
        mLlDst.addView(ViewUtil.getWordsView(mContext, explains, android.R.color.white));
    }

    private void resetViewContent() {
        mLlDst.removeAllViews();
    }

    private void setQuery(String title) {
        mTvSrc.setText(title);
    }

    public void show(boolean isShowFavoriteButton) {
        removeHeadsUpView();
        //向 WindowManager 添加浮动窗
        mWindowManager.addView(mHeadsUpView, getPopViewParams());
        mButtonFavorite.setVisibility(isShowFavoriteButton ? View.VISIBLE : View.GONE);
        //设置显示动画
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(mContentView, "translationY", -700, 0);
        translationAnim.setDuration(DURATION_TIME);
        translationAnim.start();

        int duration = SpUtils.getDurationTimeWay(GDApplication.mContext).getDurationTime();
        Logger.i("tip 显示时长为 " + duration);
        Observable.timer(duration, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Func1<Long, Object>() {
                    @Override
                    public Object call(Long aLong) {
                        removePoppedViewAndClear();
                        return null;
                    }
                })
                .subscribe();
    }

    private void removePoppedViewAndClear() {
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(mContentView, "translationY", 0, -700);
        translationAnim.start();
        translationAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeHeadsUpView();
                if (mViewDismissHandler != null) {
                    mViewDismissHandler.onViewDismiss();
                }
                resetViewContent();
            }
        });
    }

    private void removeHeadsUpView() {
        if (mWindowManager != null && mHeadsUpView.isAttachedToWindow()) {
            mWindowManager.removeView(mHeadsUpView);
        }
    }

    /**
     * TODO 添加滑动手势 比如系统通知，可以左滑或者优化 移除悬浮窗 如果有人做 欢迎 PR ~
     * touch the outside of the content view, remove the popped view
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        Rect rect = new Rect();
//        mContentView.getGlobalVisibleRect(rect);
//        if (!rect.contains(x, y)) {
//            removePoppedViewAndClear();
//        }
        return false;
    }

    public interface ViewDismissHandler {
        void onViewDismiss();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_action:
                Result entity = (Result) v.getTag();
                QueryBuilder queryBuilder = new QueryBuilder(Result.class);
                queryBuilder = queryBuilder.whereEquals("query ", entity.getQuery());
                if (GDApplication.getAppComponent().getLiteOrm().query(queryBuilder).isEmpty()) {
                    long res = GDApplication.getAppComponent().getLiteOrm().insert(entity);
                    showToast(res > 0 ? "收藏成功" : "收藏失败");
                } else {
                    showToast("'" + entity.getQuery() + "' 已存在于单词本！");
                }
                removePoppedViewAndClear();
                break;
            default:
                removePoppedViewAndClear();
        }

    }

    private void showToast(String msg) {
        Observable.just(msg)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.i("show toast");
                        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}