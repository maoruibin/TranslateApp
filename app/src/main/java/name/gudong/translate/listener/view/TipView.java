package name.gudong.translate.listener.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import me.gudong.translate.R;
import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.util.ViewUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by GuDong on 3/15/16 18:26.
 * Contact with gudong.name@gmail.com.
 */
public class TipView extends LinearLayout {
    private static final int DURATION_TIME = 300;

    private RelativeLayout mContentView;
    private TextView mTvSrc;
    private TextView mTvPhonetic;
    private LinearLayout mLlDst;
    private LinearLayout mLlSrc;
    private TextView mFavorite;
    private TextView mTvPoint;

    public TipView(Context context) {
        this(context,null);
    }

    public TipView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TipView view = (TipView) View.inflate(context, R.layout.pop_view, this);
        mTvSrc = (TextView) view.findViewById(R.id.tv_pop_src);
        mTvPoint = (TextView) view.findViewById(R.id.tv_point);
        mTvPhonetic = (TextView) view.findViewById(R.id.tv_pop_phonetic);
        mLlSrc = (LinearLayout) view.findViewById(R.id.ll_pop_src);
        mLlDst = (LinearLayout) view.findViewById(R.id.ll_pop_dst);
        mFavorite = (TextView) view.findViewById(R.id.bt_action);
        mContentView = (RelativeLayout) view.findViewById(R.id.pop_view_content_view);
    }

    public void error(String error){
        mFavorite.setVisibility(INVISIBLE);
        mLlDst.setVisibility(INVISIBLE);
        mLlSrc.setVisibility(INVISIBLE);
        mTvPoint.setVisibility(VISIBLE);
        mTvPoint.setText(error);
    }

    public void setContent(Result result, boolean isShowFavoriteButton) {
        setUpFavorite(result,isShowFavoriteButton);
        setQuery(result.getQuery());
        setPhonetic(result.getPhAm());

        List<String> temp = result.getExplains();
        if (temp.isEmpty() ) {
            temp = result.getTranslation();
            if(temp == null){
                temp = new ArrayList<>();
            }
        }

        if(!temp.isEmpty()){
            Observable.from(temp)
                    .subscribe((s)->addExplain(s));
        }else{
            error(getContext().getString(R.string.tip_explain_empty));
        }
    }

    public void startWithAnim(){
        //设置显示动画
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(mContentView, "translationY", -700, 0);
        translationAnim.setDuration(DURATION_TIME);
        translationAnim.start();
    }

    public void closeWithAnim(@NonNull OnAnimListener listener){
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(mContentView, "translationY", 0, -700);
        translationAnim.start();
        translationAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                listener.onCloseAnimEnd(animation);
            }
        });
    }

    public interface OnAnimListener{
        void onCloseAnimEnd(Animator animation);
    }
    //为每个释义设置内容
    private void addExplain(String explains) {
        mLlDst.addView(ViewUtil.getWordsView(getContext(), explains, android.R.color.white,false));
    }


    //设置收藏按钮的隐藏显示 以及对应的点击事件处理
    private void setUpFavorite(Result result,boolean isShowFavoriteButton){
        if(mFavorite == null)return;
        mFavorite.setVisibility(isShowFavoriteButton ? View.VISIBLE : View.GONE);
        mFavorite.setOnClickListener((v)->{
            MobclickAgent.onEvent(getContext(),"favorite_service");
            QueryBuilder queryBuilder = new QueryBuilder(Result.class);
            queryBuilder = queryBuilder.whereEquals("query ", result.getQuery());
            if (GDApplication.getAppComponent().getLiteOrm().query(queryBuilder).isEmpty()) {
                long res = GDApplication.getAppComponent().getLiteOrm().insert(result);
                showToast(res > 0 ? "收藏成功" : "收藏失败");
            } else {
                showToast("'" + result.getQuery() + "' 已存在于单词本！");
            }
        });
    }

    //设置单词名称
    private void setQuery(String title) {
        mTvSrc.setText(title);
    }

    //设置单词解释
    private void setPhonetic(String phonetic) {
        if(!TextUtils.isEmpty(phonetic)){
            mTvPhonetic.setVisibility(View.VISIBLE);
            mTvPhonetic.setText("["+phonetic+"]");
        }else{
            mTvPhonetic.setVisibility(View.GONE);
        }
    }

    private void showToast(String msg) {
        Observable.just(msg)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.i("show toast");
                        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
