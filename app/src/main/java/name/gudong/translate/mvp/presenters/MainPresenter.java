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
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;

import com.litesuits.orm.LiteOrm;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import jonathanfinerty.once.Once;
import me.gudong.translate.BuildConfig;
import name.gudong.translate.listener.ListenClipboardService;
import name.gudong.translate.listener.clipboard.ClipboardManagerCompat;
import name.gudong.translate.mvp.model.DownloadService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.AbsResult;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.mvp.model.type.EDurationTipTime;
import name.gudong.translate.mvp.model.type.EIntervalTipTime;
import name.gudong.translate.mvp.model.type.ETranslateFrom;
import name.gudong.translate.mvp.views.IMainView;
import name.gudong.translate.util.DialogUtil;
import name.gudong.translate.util.SpUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by GuDong on 12/27/15 16:52.
 * Contact with gudong.name@gmail.com.
 */
public class MainPresenter extends BasePresenter<IMainView> {
    @Inject
    ClipboardManagerCompat mClipboardWatcher;


    // 可以看到在使用@Inject进行注入时，构造注入和成员变量注入两种方式可以共存
    @Inject
    public MainPresenter(LiteOrm liteOrm, WarpAipService apiService, DownloadService downloadService,Context context) {
        super(liteOrm, apiService, downloadService, context);
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
            ClipboardManager clipService = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("", "");
            clipService.setPrimaryClip(clipData);
        }
    }

    public void checkVersionAndShowChangeLog() {
        String showWhatsNew = "showWhatsNewTag";
        if (!Once.beenDone(Once.THIS_APP_VERSION, showWhatsNew)) {
            DialogUtil.showChangelog((AppCompatActivity) getContext());
            Once.markDone(showWhatsNew);
        }
    }

    public void executeSearch(String keywords) {
        mView.onPrepareTranslate();
        Observable<AbsResult> observable = mWarpApiService.translate(SpUtils.getTranslateEngineWay(getContext()), keywords);
        if (observable == null) {
            Logger.e("Observable<AbsResult> is null");
            return;
        }

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<AbsResult, Boolean>() {
                    @Override
                    public Boolean call(AbsResult absResult) {
                        return absResult != null;
                    }
                })
                .filter(new Func1<AbsResult, Boolean>() {
                    @Override
                    public Boolean call(AbsResult result) {
                        return result.wrapErrorCode() == 0;
                    }
                })
                .map(new Func1<AbsResult, List<String>>() {
                    @Override
                    public List<String> call(AbsResult absResult) {
                        Result result = absResult.getResult();
                        if (result == null) return null;

                        mView.addTagForView(result);

                        if (!TextUtils.isEmpty(result.getEnMp3())) {
                            mView.showPlaySound();
                        } else {
                            mView.hidePlaySound();
                        }

                        if (isFavorite(result.getQuery()) != null) {
                            mView.initWithFavorite();
                        } else {
                            mView.initWithNotFavorite();
                        }

                        List<String> temp = absResult.wrapExplains();
                        //增加音标显示
                        String phAm = absResult.getResult().getPhAm();
                        if (!temp.isEmpty() && !TextUtils.isEmpty(phAm)){
                            temp.add(0,"["+phAm+"]");
                            return temp;
                        }
                        return absResult.wrapTranslation();
                    }
                })
                .filter(new Func1<List<String>, Boolean>() {
                    @Override
                    public Boolean call(List<String> strings) {
                        return strings != null && !strings.isEmpty();
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

    public void favoriteWord(Result result) {
        mLiteOrm.insert(result);
    }

    public void unFavoriteWord(Result result) {
        mLiteOrm.delete(result);
    }

    public void startListenClipboardService() {
        ListenClipboardService.start(getContext());
    }

    /**
     * 去评分
     */
    public void gotoMarket() {
        Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    public void prepareTranslateWay() {
        ETranslateFrom from = SpUtils.getTranslateEngineWay(getContext());
        mView.initTranslateEngineSetting(from);
    }

    public void prepareOptionSettings(Menu menu) {
        EIntervalTipTime intervalTime = SpUtils.getIntervalTimeWay(getContext());
        EDurationTipTime durationTime = SpUtils.getDurationTimeWay(getContext());
        boolean reciteFlag = SpUtils.getReciteOpenOrNot(getContext());
        boolean openJIT = SpUtils.getOpenJITOrNot(getContext());

        mView.initIntervalTimeSetting(menu, intervalTime);
        mView.initDurationTimeSetting(menu, durationTime);
        mView.initReciteSetting(menu, reciteFlag);
        mView.initJITSetting(menu, openJIT);
    }

    /**
     * clear cache file  for play sounds mp3
     */
    public void clearSoundCache() {
        makeObservable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return mFileManager.resetFileCache(getContext());
            }
        }).
        subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if(aBoolean){
                    //Toast.makeText(getContext(), "清除缓存成功", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(getContext(), "无缓存需要清除", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}


