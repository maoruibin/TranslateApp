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

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import jonathanfinerty.once.Once;
import name.gudong.translate.BuildConfig;
import name.gudong.translate.listener.ListenClipboardService;
import name.gudong.translate.listener.clipboard.ClipboardManagerCompat;
import name.gudong.translate.mvp.model.SingleRequestService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.dayline.JinshanDayLineEntity;
import name.gudong.translate.mvp.model.entity.translate.AbsResult;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.model.type.ETranslateFrom;
import name.gudong.translate.mvp.views.IMainView;
import name.gudong.translate.ui.activitys.MainActivity;
import name.gudong.translate.util.DialogUtil;
import name.gudong.translate.util.LocalDicHelper;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.Utils;
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
    public static final String KEY_RESULT = "RESULT";
    public static final int KEY_REQUEST_CODE_FOR_NOTI = 100;
    @Inject
    ClipboardManagerCompat mClipboardWatcher;


    // 可以看到在使用@Inject进行注入时，构造注入和成员变量注入两种方式可以共存
    @Inject
    public MainPresenter(LiteOrm liteOrm, WarpAipService apiService, SingleRequestService singleRequestService, Context context) {
        super(liteOrm, apiService, singleRequestService, context);
    }

    public void checkIntentFromClickTipView(Intent intent) {
        if (hasExtraResult(intent)) {
            Result result = (Result) intent.getSerializableExtra(KEY_RESULT);
            if (result != null) {
                mView.onInitSearchText(result.getQuery());
                executeSearch(result.getQuery());
            }
        }
    }

    public void analysisLocalDic() {
        makeObservable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return LocalDicHelper.getLocalDic(mContext);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        mView.attachLocalDic(strings);
                    }
                });
    }
    public boolean hasExtraResult(Intent intent) {
        return intent.hasExtra(KEY_RESULT);
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
            trigDbUpdate();
        }
    }

    public void trigDbUpdate() {
        List<Result> results = mLiteOrm.query(Result.class);
        for (Result result : results) {
            if (!result.isMake_done_once()) {
                result.setMake_done_once(false);
                mLiteOrm.update(result);
            }
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
                        result.setCreate_time(System.currentTimeMillis());
                        result.setUpdate_time(System.currentTimeMillis());
                        if (mView == null) return null;
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
                        if (!temp.isEmpty() && !TextUtils.isEmpty(phAm)) {
                            temp.add(0, "[" + phAm + "]");
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
                        if (mView != null) {
                            mView.onError(e);
                        }
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
        //,Result.COL_QUERY,new String[]{result.getQuery()
        WhereBuilder builder = WhereBuilder.create(Result.class).andEquals(Result.COL_QUERY, result.getQuery());
        mLiteOrm.delete(builder);
    }

    public void startListenClipboardService() {
        ListenClipboardService.start(getContext());
    }

    /**
     * 去评分
     */
    public void gotoMarket() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(mContext, "没有找到合适的应用商店", Toast.LENGTH_SHORT).show();
        }
    }

    public void prepareTranslateWay() {
        ETranslateFrom from = SpUtils.getTranslateEngineWay(getContext());
        mView.initTranslateEngineSetting(from);
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
                        if (aBoolean) {
                            //Toast.makeText(getContext(), "清除缓存成功", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(getContext(), "无缓存需要清除", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void dayline() {
        mSingleRequestService.dayline("http://open.iciba.com/dsapi/")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<JinshanDayLineEntity>() {
                    @Override
                    public void call(JinshanDayLineEntity jinshanDayLineEntity) {
                        if (jinshanDayLineEntity != null) {
                            mView.fillDayline(jinshanDayLineEntity);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //call onError to stop crashing the app
                        //TODO error handling
                    }
                });
    }

    public static void jumpMainActivityFromClickTipView(Context context, Result result) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_RESULT, result);
        context.startActivity(intent);
    }

    /**
     * 触发 Android M 上的浮窗权限
     */
    public void triggerDrawOverlaysPermission() {
        if (Utils.isAndroidM()) {
            if (!Settings.canDrawOverlays(getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                getContext().startActivity(intent);
            }
        }
    }
}


