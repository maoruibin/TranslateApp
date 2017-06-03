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

import android.content.ClipData;
import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import jonathanfinerty.once.Once;
import name.gudong.translate.listener.clipboard.ClipboardManagerCompat;
import name.gudong.translate.mvp.model.SingleRequestService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.views.IBookView;
import name.gudong.translate.ui.NavigationManager;
import name.gudong.translate.util.SpUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by GuDong on 2/28/16 17:02.
 * Contact with gudong.name@gmail.com.
 */
public class BookPresenter extends BasePresenter<IBookView> {
    private static final String KEY_TIP_OF_RECITE_OPEN = "TIP_OF_RECITE_OPEN";
    private static final String KEY_RECITE_MODE_SWITCH = "RECITE_MODE_SWITCH";
    private static final String TAG = "BOOK_PRESENTER";
    @Inject
    public BookPresenter(LiteOrm liteOrm, WarpAipService apiService, SingleRequestService singleRequestService, Context context) {
        super(liteOrm, apiService, singleRequestService,context);
    }

    public void getWords() {
        getAllWordsWarpByObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Result>>() {
                    @Override
                    public void call(List<Result> transResultEntities) {
                        boolean isReciteMode = SpUtils.isWordBookReciteMode(getContext());
                        mView.fillData(transResultEntities, isReciteMode);
                        MobclickAgent.onEvent(getContext(),"wordsCount",transResultEntities.size()+"");
                        Map<String,String>param = new HashMap<>();
                        param.put("wordsCount",transResultEntities.size()+"");
                        MobclickAgent.onEventValue(getContext(), "wordpage", param, 100);
                    }
                });
    }

    public void deleteWords(Result entity){
        deleteWordsByObservable(entity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((integer -> {
                    if (integer > 0) {
                        mView.deleteWordSuccess(entity);
                    } else {
                        mView.deleteWordFail();
                    }
                }), throwable -> {
                    mView.onError(throwable);
                });
    }

    private Observable<List<Result>> getAllWordsWarpByObservable() {
        return makeObservable(getAllWordsReal());
    }

    private Observable<Integer> deleteWordsByObservable(Result entity) {
        return makeObservable(deleteWordReal(entity));
    }

    // callable 对象可以作为线程体执行
    private Callable<List<Result>> getAllWordsReal() {
        return new Callable<List<Result>>() {
            @Override
            public List<Result> call() throws Exception {
                QueryBuilder<Result> qb = new QueryBuilder<>(Result.class)
                        .appendOrderDescBy(Result.COL_ID);

                List<Result> results = mLiteOrm.query(qb);
                Logger.i(" results.size() "+results.size());
                return results;
            }
        };
    }

    private Callable<Integer> deleteWordReal(Result entity) {
        return () -> mLiteOrm.delete(entity);
    }

    public String getWordsJsonString(List<Result>results){
        Gson gson = new Gson();
        return gson.toJson(results);
    }

    public void copyText(String text){
        ClipData myClip = ClipData.newPlainText("text", text);
        ClipboardManagerCompat.create(getContext()).setPrimaryClip(myClip);
    }

    public void restoreWordsByText(List<Result> oriList, String text) {
        Gson gson = new Gson();
        List<Result>results = gson.fromJson(text, new TypeToken<List<Result>>(){}.getType());
        int hasExistCount = 0;
        for(Result result:results){
            if(oriList.contains(result)){
                Logger.t(TAG).i(result.getQuery()+" has exist ");
                hasExistCount ++;
                continue;
            }
            mLiteOrm.insert(result);
            Logger.t(TAG).i(result.getQuery()+" restore success ");
        }
        Logger.t(TAG).i("restore finish! total restore count is "+ (results.size()-hasExistCount));
        if(results.size() == hasExistCount){
            mView.showTipDataHaveNoChange();
        }else{
            mView.restoreSuccess(results.size()-hasExistCount);
        }
    }

    public void checkPointRecite(int wordsSize) {
        //当单词数大于 5 个时才提示
        if (!Once.beenDone(KEY_TIP_OF_RECITE_OPEN) && wordsSize>=5) {
            Once.markDone(KEY_TIP_OF_RECITE_OPEN);
            new AlertDialog.Builder(getContext())
                    .setTitle("提示")
                    .setMessage("检测到你的单词本中已经有不少单词了，建议你前往设置页面，开启定时单词提示，用于帮助你背单词。")
                    .setPositiveButton("去开启", ((dialog, which) -> {
                        NavigationManager.gotoSetting(getContext());
                    }))
                    .setNegativeButton("没兴趣",null)
                    .show();
        }
    }

    public void initStatus() {
        Once.toDo(KEY_RECITE_MODE_SWITCH);
        // 第一次点击单词本开关需要给用户一个功能提示框
        Once.toDo(KEY_TIP_OF_RECITE_OPEN);
    }

    public boolean hasShowReciteModeIntroduce() {
        return Once.beenDone(KEY_RECITE_MODE_SWITCH);
    }

    public void makeReciteDone(){
        Once.markDone(KEY_RECITE_MODE_SWITCH);
    }
}
