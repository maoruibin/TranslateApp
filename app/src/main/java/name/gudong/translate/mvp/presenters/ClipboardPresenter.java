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

import com.litesuits.orm.LiteOrm;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import me.gudong.translate.BuildConfig;
import name.gudong.translate.GDApplication;
import name.gudong.translate.listener.clipboard.ClipboardManagerCompat;
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

    @Inject
    ClipboardManagerCompat mClipboardWatcher;

    /**
     * 循环展示单词结果
     */
    private List<Result> results;

    private int currentIndex = -1;

    private ClipboardManagerCompat.OnPrimaryClipChangedListener mListener = () -> {
        CharSequence content = mClipboardWatcher.getText();
        if(content != null){
            performClipboardCheck(content.toString());
        }
    };


    /**
     * 定时显示 Tip 事件源
     */
    private Subscription mSubscription;
    /**
     * 显示 Tip 的动作
     */
    private Action1 mActionShowTip;


    @Inject
    ClipboardPresenter(LiteOrm liteOrm, WarpAipService apiService, SingleRequestService singleRequestService, Context context) {
        super(liteOrm, apiService, singleRequestService, context);
        results = mLiteOrm.query(Result.class);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        initCountdownSetting();
        if(SpUtils.isShowIconInNotification(getContext())){
            Utils.showNormalNotification(getContext());
        }
    }

    private void initCountdownSetting(){
        mActionShowTip = (t)->{
            Logger.i("====","time is out show words");
            Result result = getResult();
            if(result == null)return;
            mView.showResult(result,false);
        };
    }

    /**
     * 开启背单词
     */
    public void openTipCyclic(){
        EIntervalTipTime tipTime = SpUtils.getIntervalTimeWay(GDApplication.mContext);
        int time = tipTime.getIntervalTime();
        boolean isSecond = tipTime == EIntervalTipTime.THIRTY_SECOND;
        TimeUnit unit = isSecond? TimeUnit.SECONDS:TimeUnit.MINUTES;

        if(mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
        }

        mSubscription = Observable.interval(time,unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mActionShowTip);
    }

    public void removeTipCyclic(){
        if(mSubscription != null && !mSubscription.isUnsubscribed()){
            mSubscription.unsubscribe();
            mSubscription = null;
            Logger.i(KEY_TAG,"移除背单词服务");
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
        Logger.i("粘贴板的单词为 "+queryText);
        if (listQuery.contains(queryText)) {
            return;
        }
        listQuery.add(queryText);

        //只有用户在打开了 划词翻译的情况下 划词翻译才能正常工作
        if(!SpUtils.getOpenJITOrNot(getContext()))return;

        //如果当前界面是 咕咚翻译的主界面 那么也不对粘贴板做监听( Debug 时开启)
        if(!BuildConfig.DEBUG){
            if(SpUtils.getAppFront(getContext()))return;
        }

        // 检查粘贴板的内容是不是单词 以及是不是为空
        if(!checkInput(queryText)){
            Logger.i("粘贴板为空");
            return;
        }
        //查询数据
        search(queryText);
    }

    public void onDestroy() {
        super.onDestroy();
        mClipboardWatcher.removePrimaryClipChangedListener(mListener);
    }


    private Result getResult(){
        int index = getResultIndex();
        Logger.i("index is "+index);
        if(index>=0){
            return results.get(index);
        } else{
            return null;
        }
    }

    private int getResultIndex(){
        if(results.isEmpty()){
            return -1;
        }
        currentIndex = currentIndex+1;
        if(currentIndex == results.size()-1){
            currentIndex = -1;
            return results.size()-1;
        }
        return currentIndex;
    }
}