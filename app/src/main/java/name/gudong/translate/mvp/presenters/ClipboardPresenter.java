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

package name.gudong.translate.mvp.presenters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import name.gudong.translate.BuildConfig;
import name.gudong.translate.listener.clipboard.ClipboardManagerCompat;
import name.gudong.translate.manager.ReciteModulePreference;
import name.gudong.translate.manager.ReciteWordManager;
import name.gudong.translate.mvp.model.SingleRequestService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.model.type.EIntervalTipTime;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.Utils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * Created by GuDong on 2/28/16 20:48.
 * Contact with gudong.name@gmail.com.
 */
public class ClipboardPresenter extends TipFloatPresenter {
    private static final String KEY_TAG = "clipboard";
    private static final String KEY_TAG_COUNT_DOWN = "Countdown";
    private static final ReciteWordManager mReciteManger = ReciteWordManager.getInstance();
    @Inject
    ClipboardManagerCompat mClipboardWatcher;

    ReciteModulePreference mRecitePreference;

    /**
     * 循环展示单词结果
     */
    private List<Result> results;

    /**
     * 定时显示 Tip 事件源
     */
    private static Subscription mSubscription;
    /**
     * 显示 Tip 的动作
     */
    private static Action1 mActionShowTip;

    private ClipboardManagerCompat.OnPrimaryClipChangedListener mListener = () -> {
        CharSequence content = mClipboardWatcher.getText();
        if (content != null) {
            performClipboardCheck(content.toString());
        }
    };

    @Inject
    ClipboardPresenter(LiteOrm liteOrm, WarpAipService apiService, SingleRequestService singleRequestService, Context context) {
        super(liteOrm, apiService, singleRequestService, context);
        QueryBuilder queryBuilder = new QueryBuilder(Result.class);
        queryBuilder = queryBuilder.whereNoEquals(Result.COL_MARK_DONE_ONCE, true);
        results = mLiteOrm.query(queryBuilder);
        mRecitePreference = new ReciteModulePreference(getContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initCountdownSetting();
        if (SpUtils.isShowIconInNotification(getContext())) {
            Utils.showNormalNotification(getContext());
        }
    }

    public boolean isOpenReciteWords() {
        return mReciteManger.isReciteOpenOrNot();
    }

    public boolean isPlaySoundsAuto() {
        return mReciteManger.isPlaySoundAuto();
    }

    private void initCountdownSetting() {
        mActionShowTip = (t) -> {
            if (isOpenReciteWords()) {
                Logger.t(KEY_TAG_COUNT_DOWN).i("time is to show words");
                Result result = null;
                int index = getResultIndex();
                if (index >= 0) {
                    result = results.get(index);
                } else {
                    return;
                }
                if (result == null) return;
                mView.showResult(result, false);
                //设置下次显示的单词
                if (index != results.size() - 1) {
                    index++;
                }else{
                    //重新循环计数
                    index = 0;
                }
                Result afterResult = results.get(index);
                mRecitePreference.setCurrentCyclicWord(afterResult.getQuery());

            } else {
                Logger.t(KEY_TAG_COUNT_DOWN).i("time is to show words but was close");
            }
        };
    }

    /**
     * 开启背单词
     */
    public void openTipCyclic() {
        EIntervalTipTime tipTime = mReciteManger.getIntervalTimeWay();
        int time = tipTime.getIntervalTime();
        boolean isSecond = tipTime == EIntervalTipTime.THIRTY_SECOND;
        TimeUnit unit = isSecond ? TimeUnit.SECONDS : TimeUnit.MINUTES;

        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }

        mSubscription = Observable.interval(time, unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mActionShowTip);


        Logger.i(KEY_TAG, "开启背单词任务 间隔 " + tipTime.getIntervalTime());
    }

    public void removeTipCyclic() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
            Logger.i(KEY_TAG, "移除背单词服务");
        }
    }

    /**
     * 添加粘贴板变化监听方法
     */
    public void addListener() {
        mClipboardWatcher.addPrimaryClipChangedListener(mListener);
    }


    private void performClipboardCheck(String queryText) {
        //处理缓存 因为粘贴板的回调操作可能触发多次
        if (listQuery.contains(queryText)) {
            return;
        }
        listQuery.add(queryText);

        //只有用户在打开了 划词翻译的情况下 划词翻译才能正常工作
        if (!SpUtils.getOpenJITOrNot(getContext())) return;

        //如果当前界面是 咕咚翻译的主界面 那么也不对粘贴板做监听( Debug 时开启)
        if (!BuildConfig.DEBUG) {
            if (SpUtils.getAppFront(getContext())) return;
        }

        // 检查粘贴板的内容是不是单词 以及是不是为空
        if (!checkInput(queryText)) {
            return;
        }
        //查询数据
        search(queryText);
    }

    public void onDestroy() {
        super.onDestroy();
        mClipboardWatcher.removePrimaryClipChangedListener(mListener);
    }


    private int getResultIndex() {
        int index = -1;
        if (results.isEmpty()) {
            return index;
        }
        /**
         * 上次背单词时的最后一个单词
         */
        String lastQuery = mRecitePreference.getCurrentCyclicWord();
        if (!TextUtils.isEmpty(lastQuery)) {
            Result result = new Result();
            result.setQuery(lastQuery);
            int indexQuery = results.indexOf(result);
            if (indexQuery >= 0) {
                index = indexQuery;
            }
        } else {
            index = 0;
        }
        return index;
    }

    /**
     * 标记已背
     *
     * @param result
     */
    public void markDone(Result result) {
        result.setMake_done_once(true);
        result.setMake_done_once_time(System.currentTimeMillis());
        Logger.i("size " + results.size());
        if (results.remove(result)) {
            Logger.i("remove suc");
        }
        Logger.i("size " + results.size());
        mLiteOrm.update(result);
    }
}