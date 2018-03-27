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
import android.view.View;

import com.litesuits.orm.LiteOrm;
import com.orhanobut.logger.Logger;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import name.gudong.translate.BuildConfig;
import name.gudong.translate.mvp.model.SingleRequestService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.translate.AbsResult;
import name.gudong.translate.mvp.model.entity.translate.HistoryResult;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.model.type.ETranslateFrom;
import name.gudong.translate.mvp.views.ITipFloatView;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.StringUtils;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by GuDong on 2/28/16 20:48.
 * Contact with gudong.name@gmail.com.
 */
public class TipFloatPresenter extends BasePresenter<ITipFloatView> {
    private static final String KEY_TAG = "clipboard";
    protected List<String> listQuery = new ArrayList<>();

    @Inject
    public TipFloatPresenter(LiteOrm liteOrm, WarpAipService apiService, SingleRequestService singleRequestService, Context context) {
        super(liteOrm, apiService, singleRequestService, context);
    }
    private String mLastQuery = "";
    private ETranslateFrom  mLastFrom = ETranslateFrom.JIN_SHAN;
    public void search(final String keywords) {
        ETranslateFrom from = SpUtils.getTranslateEngineWay(getContext());
        //去掉重复
        if(mLastQuery.equals(keywords) && mLastFrom == from){
            mView.onComplete();
            return;
        }
        mLastQuery = keywords;
        mLastFrom = from;

        if (!checkInput(keywords)) {
            mView.onComplete();
            return;
        }

        mWarpApiService.translate(from, keywords)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter((result) -> {
                    return result.wrapErrorCode() == 0;
                })
                .subscribe(new Subscriber<AbsResult>() {
                    @Override
                    public void onCompleted() {
                        //清空缓存
                        listQuery.clear();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView == null) return;
                        if (e instanceof SocketTimeoutException) {
                            String msg = "网络请求超时，请稍后重试。";
                            mView.errorPoint(msg);
                            trackTranslateFail(msg);
                        } else {
                            String msg = "网络请求超时，请稍后重试。";
                            if (BuildConfig.DEBUG) {
                                msg = "请求数据异常，您可以试试切换其他引擎。" + e.getMessage();
                                e.printStackTrace();
                            } else {
                                msg = "请求数据异常(source:"+from.getName()+")，您可以试试切换其他引擎。";
                            }
                            mView.errorPoint(msg);
                            trackTranslateFail(msg);
                        }
                    }

                    @Override
                    public void onNext(AbsResult result) {
                        if (mView == null) return;
                        trackTranslate();
                        Result realResult = result.getResult();
                        realResult.setCreate_time(System.currentTimeMillis());
                        realResult.setUpdate_time(System.currentTimeMillis());
                        mView.showResult(realResult, true);
                        recordHistoryWords(realResult);
                    }
                });
    }




    public void initFavoriteStatus(Result result) {
        Result localResult = isFavorite(result.getQuery());
        if (localResult != null) {
            mView.initWithFavorite(result);
        } else {
            mView.initWithNotFavorite(result);
        }
    }

    public void clickFavorite(View view, Result result) {
        Result localResult = isFavorite(result.getQuery());
        if (localResult != null) {
            int index = deleteResultFromDb(localResult);
            if (index > 0) {
                mView.initWithNotFavorite(result);
                Logger.i("删除成功");
            } else {
                Logger.i("删除失败");
            }
        } else {
            long index = insertResultToDb(result);
            if (index > 0) {
                mView.initWithFavorite(result);
                Logger.i("插入成功");
            } else {
                Logger.i("插入失败");
            }
        }
    }

    public void jumpMainActivity(Result result) {
        MainPresenter.jumpMainActivityFromClickTipView(getContext(), result);
    }

    protected boolean checkInput(String input) {
        // empty check
        if (TextUtils.isEmpty(input)) {
            Logger.e("剪贴板为空了");
            trackTranslateFail("剪贴板为空了");
            return false;
        }

        if (StringUtils.isValidEmailAddress(input)) {
            Logger.e(input + " 是一个邮箱");
            trackTranslateFail(input + " 是一个邮箱");
            return false;
        }

        if (StringUtils.isValidUrl(input)) {
            Logger.e(input + " 是一个网址");
            trackTranslateFail(input + " 是一个网址");
            return false;
        }

        if (StringUtils.isValidNumeric(input)) {
            Logger.e(input + " 是一串数字");
            trackTranslateFail(input + " 是一串数字");
            return false;
        }

        // length check
        if (BuildConfig.IS_ADVANCE) {
            return true;
        }

        if (StringUtils.isChinese(input)) {
            Logger.e(input + " 中包含中文字符");
            trackTranslateFail(input + " 中包含中文字符");
            return false;
        }

        if(StringUtils.isMoreThanOneWord(input)){
            mView.errorPoint("咕咚翻译目前不支持划句或者划短语翻译\n多谢理解");
            trackTranslateFail("咕咚翻译目前不支持划句或者划短语翻译\n多谢理解");
            return false;
        }

        return true;
    }
}