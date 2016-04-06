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

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import jonathanfinerty.once.Once;
import me.gudong.translate.BuildConfig;
import name.gudong.translate.listener.ListenClipboardService;
import name.gudong.translate.listener.clipboard.ClipboardManagerCompat;
import name.gudong.translate.manager.FileManager;
import name.gudong.translate.mvp.DownloadService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.AbsResult;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.mvp.model.type.EDurationTipTime;
import name.gudong.translate.mvp.model.type.EIntervalTipTime;
import name.gudong.translate.mvp.model.type.ETranslateFrom;
import name.gudong.translate.mvp.views.IMainView;
import name.gudong.translate.util.DialogUtil;
import name.gudong.translate.util.SpUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by GuDong on 12/27/15 16:52.
 * Contact with gudong.name@gmail.com.
 */
public class MainPresenter extends BasePresenter<IMainView> {
    @Inject
    ClipboardManagerCompat mClipboardWatcher;
    FileManager mFileManager = new FileManager();

    // 可以看到在使用@Inject进行注入时，构造注入和成员变量注入两种方式可以共存
    @Inject
    public MainPresenter(LiteOrm liteOrm, WarpAipService apiService, DownloadService downloadService, Activity activity) {
        super(liteOrm, apiService, downloadService, activity);
    }

    public void checkClipboard() {
        CharSequence sequence = mClipboardWatcher.getText();
        // 感谢 V 友提供的bug反馈
        if (sequence == null) return;
        String text = sequence.toString();
        if (TextUtils.isEmpty(text)) return;
        // 使用正则判断粘贴板中的字符是不是单词
        String patternWords = "[a-zA-Z1-9 ]{1,}";
        Pattern r = Pattern.compile(patternWords);
        Matcher m = r.matcher(text);
        if (m.matches()) {
            mView.onInitSearchText(text);
            executeSearch(text);
            mView.closeKeyboard();
        }
    }

    public void clearClipboard() {
        CharSequence sequence = mClipboardWatcher.getText();
        if (!TextUtils.isEmpty(sequence)) {
            ClipboardManager clipService = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("", "");
            clipService.setPrimaryClip(clipData);
        }
    }

    public void checkVersionAndShowChangeLog() {
        String showWhatsNew = "showWhatsNewTag";
        if (!Once.beenDone(Once.THIS_APP_VERSION, showWhatsNew)) {
            DialogUtil.showChangelog((AppCompatActivity) mActivity);
            Once.markDone(showWhatsNew);
        }
    }

    public void executeSearch(String keywords) {
        mView.onPrepareTranslate();
        Observable<AbsResult> observable = mWarpApiService.translate(SpUtils.getTranslateEngineWay(mActivity), keywords);
        if (observable == null) {
            Logger.e("Observable<AbsResult> is null");
            return;
        }

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<AbsResult, Boolean>() {
                    @Override
                    public Boolean call(AbsResult result) {
                        return result.wrapErrorCode() == 0;
                    }
                })
                .map(new Func1<AbsResult, List<String>>() {
                    @Override
                    public List<String> call(AbsResult absResult) {
                        mView.onGetDataSuccess(absResult);
                        List<String> temp = absResult.wrapExplains();
                        if (!temp.isEmpty()) {
                            return temp;
                        }
                        return absResult.wrapTranslation();
                    }
                })
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> strings) {
                        if (strings == null) {
                            return Observable.error(new Exception(("啥也没有翻译出来!")));
                        }
                        return Observable.from(strings);
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        mView.onTranslateComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                        mView.onError(e);
                    }

                    @Override
                    public void onNext(String s) {
                        mView.addExplainItem(s);
                    }
                });
    }

    /**
     * check the word is favorite or not
     *
     * @param word checked word
     * @return true if word has been favorite else return false
     */
    public boolean isFavorite(String word) {
        QueryBuilder queryBuilder = new QueryBuilder(Result.class);
        queryBuilder = queryBuilder.whereEquals("query ", word);
        return !mLiteOrm.query(queryBuilder).isEmpty();
    }

    public void favoriteWord(Result result) {
        mLiteOrm.insert(result);
    }

    public void unFavoriteWord(Result result) {
        mLiteOrm.delete(result);
    }

    public void startListenClipboardService() {
        ListenClipboardService.start(mActivity);
    }

    /**
     * 去评分
     */
    public void gotoMarket() {
        Uri uri = Uri.parse("market://details?id=" + mActivity.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
    }

    public void prepareTranslateWay() {
        ETranslateFrom from = SpUtils.getTranslateEngineWay(mActivity);
        mView.initTranslateEngineSetting(from);
    }

    public void prepareOptionSettings(Menu menu) {
        EIntervalTipTime intervalTime = SpUtils.getIntervalTimeWay(mActivity);
        EDurationTipTime durationTime = SpUtils.getDurationTimeWay(mActivity);
        boolean reciteFlag = SpUtils.getReciteOpenOrNot(mActivity);
        boolean openJIT = SpUtils.getOpenJITOrNot(mActivity);

        mView.initIntervalTimeSetting(menu, intervalTime);
        mView.initDurationTimeSetting(menu, durationTime);
        mView.initReciteSetting(menu, reciteFlag);
        mView.initJITSetting(menu, openJIT);
    }

    public void playSound(AbsResult entity) {
        if (entity == null) {
            return;
        }
        Result result = entity.getResult();
        if (result == null) {
            return;
        }
        String amMp3Url = result.getEnMp3();
        if (TextUtils.isEmpty(amMp3Url)) {
            return;
        }

        if (!amMp3Url.startsWith(DownloadService.KEY_URL)) {
            return;
        }

        String url = amMp3Url.replace(DownloadService.KEY_URL + "resource/amp3/", "");
        String[] param = url.split("/");
        if (param.length != 5) {
            return;
        }
        //http://res.iciba.com/resource/amp3/oxford/0/24/8a/248a2aa9259a98ecb7a1ff677a0feed2.mp3
//        @GET("resource/amp3/{first}/{second}/{third}/{forth}/{name}")
        Logger.i(param.toString());
        Call<ResponseBody> call = mDownloadService.downloadFileWithDynamicUrlSync(param[0], param[1], param[2], param[3], param[4]);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    try {
                        mFileManager.cacheFileOnDisk(mActivity,amMp3Url,response.body().bytes());
                        Logger.i("获取数据成功 大小为 " + response.body().bytes().length / 1000 + " kb");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Logger.e(t.getMessage());
                t.printStackTrace();
            }
        });
    }
}
