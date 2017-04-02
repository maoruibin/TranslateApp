package name.gudong.translate.ui.activitys;

/**
 * Created by GuDong on 08/10/2016 14:20.
 * Contact with gudong.name@gmail.com.
 */

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import javax.inject.Inject;

import name.gudong.translate.injection.components.AppComponent;
import name.gudong.translate.injection.components.DaggerActivityComponent;
import name.gudong.translate.injection.modules.ActivityModule;
import name.gudong.translate.listener.view.TipView;
import name.gudong.translate.listener.view.TipViewController;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.presenters.BasePresenter;
import name.gudong.translate.mvp.presenters.TipFloatPresenter;
import name.gudong.translate.mvp.views.ITipFloatView;

public class ProcessTextActivity extends BaseActivity<TipFloatPresenter> implements ITipFloatView, TipView.ITipViewListener {
    @Inject
    TipViewController mTipViewController;

    @Override
    protected void onNewIntent(Intent intent) {
        checkText(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkText(getIntent());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    private void checkText(Intent intent) {
        CharSequence text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        boolean readonly =
                getIntent().getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);
        if(!TextUtils.isEmpty(text)){
            mPresenter.search(text.toString());
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent, ActivityModule activityModule) {
        DaggerActivityComponent.builder()
                .activityModule(activityModule)
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    public void errorPoint(String error) {
        mTipViewController.showErrorInfo(error,this);
    }

    @Override
    public void showResult(Result result, boolean isShowFavorite) {
        mTipViewController.show(result, isShowFavorite,false, this);
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
                mPresenter.clickFavorite(view, result);
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
    public void onClickDone(View view, Result result) {

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
        finish();
    }

    @Override
    public void finish() {
        if(!isFinishing()){
            super.finish();
        }
    }
}
