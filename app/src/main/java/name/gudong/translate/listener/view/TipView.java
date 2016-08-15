package name.gudong.translate.listener.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.gudong.translate.R;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.util.ViewUtil;
import rx.Observable;

/**
 * Created by GuDong on 3/15/16 18:26.
 * Contact with gudong.name@gmail.com.
 */
public class TipView extends LinearLayout {
    private static final int DURATION_TIME = 300;

    private View mContentView;
    private TextView mTvSrc;
    private TextView mTvPhonetic;
    private LinearLayout mLlDst;
    private LinearLayout mLlSrc;
    private ImageView mIvFavorite;
    private ImageView mIvSound;
    private TextView mTvPoint;

    private IOperateTipView mListener;

    public TipView(Context context) {
        this(context, null);
    }

    public TipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TipView view = (TipView) View.inflate(context, R.layout.pop_view, this);
        mTvSrc = (TextView) view.findViewById(R.id.tv_pop_src);
        mTvPoint = (TextView) view.findViewById(R.id.tv_point);
        mTvPhonetic = (TextView) view.findViewById(R.id.tv_pop_phonetic);
        mLlSrc = (LinearLayout) view.findViewById(R.id.ll_pop_src);
        mLlDst = (LinearLayout) view.findViewById(R.id.ll_pop_dst);
        mIvFavorite = (ImageView) view.findViewById(R.id.iv_favorite);
        mIvSound = (ImageView) view.findViewById(R.id.iv_sound);
        mContentView = view.findViewById(R.id.pop_view_content_view);
    }

    public void error(String error) {
        mIvFavorite.setVisibility(INVISIBLE);
        mLlDst.setVisibility(INVISIBLE);
        mLlSrc.setVisibility(INVISIBLE);
        mTvPoint.setVisibility(VISIBLE);
        mTvPoint.setText(error);
    }

    public void setContent(Result result, boolean isShowFavoriteButton) {
        if (result == null) return;
        initView(isShowFavoriteButton,result);
        addListener(result);

        setQuery(result.getQuery());
        setPhonetic(result.getPhAm());

        List<String> temp = result.getExplains();
        if (temp.isEmpty()) {
            temp = result.getTranslation();
            if (temp == null) {
                temp = new ArrayList<>();
            }
        }

        if (!temp.isEmpty()) {
            Observable.from(temp).subscribe((s) -> addExplain(s));
        } else {
            error(getContext().getString(R.string.tip_explain_empty));
        }
    }

    public void startWithAnim() {
        //设置显示动画
        ObjectAnimator translationAnim = ObjectAnimator.ofFloat(mContentView, "translationY", -700, 0);
        translationAnim.setDuration(DURATION_TIME);
        translationAnim.start();
    }

    public void closeWithAnim(@NonNull OnAnimListener listener) {
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

    public interface OnAnimListener {
        void onCloseAnimEnd(Animator animation);
    }

    //为每个释义设置内容
    private void addExplain(String explains) {
        mLlDst.addView(ViewUtil.getWordsView(getContext(), explains, android.R.color.white, false));
    }

    private void initView(boolean isShowFavoriteButton,Result result){
        mIvFavorite.setVisibility(isShowFavoriteButton ? View.VISIBLE : View.GONE);
        mIvSound.setVisibility(TextUtils.isEmpty(result.getEnMp3()) ? View.GONE : View.VISIBLE);
        mListener.onInitFavorite(mIvFavorite, result);
    }

    private void addListener(Result result){
        mIvFavorite.setOnClickListener((v) -> {
            mListener.onClickFavorite(v,result);
        });

        mIvSound.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClickPlaySound(v, result);
            }
        });
    }

    public void setFavoriteBackground(@DrawableRes int drawableSrc) {
        mIvFavorite.setImageResource(drawableSrc);
    }

    public void setListener(IOperateTipView mListener) {
        this.mListener = mListener;
    }

    public interface IOperateTipView {

        void onClickFavorite(View view, Result result);

        void onClickPlaySound(View view, Result result);

        /**
         * set up favorite view state  base on it change background of favorite view
         * @param mIvFavorite
         * @param result
         */
        void onInitFavorite(ImageView mIvFavorite, Result result);
    }

    //设置单词名称
    private void setQuery(String title) {
        mTvSrc.setText(title);
    }

    //设置单词解释
    private void setPhonetic(String phonetic) {
        if (!TextUtils.isEmpty(phonetic)) {
            mTvPhonetic.setVisibility(View.VISIBLE);
            mTvPhonetic.setText("[" + phonetic + "]");
        } else {
            mTvPhonetic.setVisibility(View.GONE);
        }
    }
}
