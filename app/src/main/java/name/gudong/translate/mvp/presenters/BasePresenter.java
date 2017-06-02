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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import name.gudong.translate.manager.FileManager;
import name.gudong.translate.mvp.model.SingleRequestService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.views.IBaseView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by GuDong on 2/27/16 23:19.
 * Contact with gudong.name@gmail.com.
 */
public class BasePresenter<V extends IBaseView> {
    protected V mView;
    protected WarpAipService mWarpApiService;
    protected SingleRequestService mSingleRequestService;
    protected LiteOrm mLiteOrm;
    protected Context mContext;

    protected FileManager mFileManager = new FileManager();

    public BasePresenter(LiteOrm liteOrm, WarpAipService apiService, Context context) {
        mLiteOrm = liteOrm;
        mWarpApiService = apiService;
        mContext = context;
    }

    public BasePresenter(LiteOrm liteOrm, WarpAipService apiService, SingleRequestService singleRequestService, Context context) {
        mLiteOrm = liteOrm;
        mWarpApiService = apiService;
        mSingleRequestService = singleRequestService;
        mContext = context;
    }

    public void onCreate() {
    }

    /**
     * attach IBaseView to Presenter
     *
     * @param view view
     */
    public void attachView(V view) {
        this.mView = view;
    }

    public void onDestroy() {
        this.mView = null;
    }

    protected Context getContext() {
        return mContext;
    }

    /**
     * check the word is favorite or not
     *
     * @param word checked word
     * @return null if word has not been favorite else return the favorite words from db
     */
    public Result isFavorite(String word) {
        QueryBuilder queryBuilder = new QueryBuilder(Result.class);
        queryBuilder = queryBuilder.whereEquals("query ", word);
        List<Result> list = mLiteOrm.query(queryBuilder);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    protected long insertResultToDb(Result entity) {
        return mLiteOrm.insert(entity);
    }

    protected int deleteResultFromDb(Result entity) {
        return mLiteOrm.delete(entity);
    }

    public void playSound(String fileName, String mp3Url) {
        File cacheFile = mFileManager.getCacheFileByUrl(getContext(), fileName);
        if (cacheFile != null && cacheFile.exists()) {
            playSound(cacheFile);
            return;
        }
        Call<ResponseBody> call = mSingleRequestService.downloadSoundFile(mp3Url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        cacheAndPlaySound(getContext(), fileName, response.body().bytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.e(t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void startSoundAnim(View view) {
        addScaleAnim(view, 1000, null);
    }

    public void startFavoriteAnim(View view, AnimationEndListener listener) {
        addScaleAnim(view, 500, listener);
    }

    private void addScaleAnim(View view, long duration, AnimationEndListener listener) {
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.5f, 1f, 1.2f, 1f);
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.5f, 1f, 1.2f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animX, animY);
        animatorSet.setDuration(duration);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }
        });
        animatorSet.start();
    }

    public interface AnimationEndListener {
        void onAnimationEnd(Animator animation);
    }


    private void cacheAndPlaySound(Context context, String fileName, byte[] data) {
        makeObservable(cacheFileObservable(context, fileName, data))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        playSound(file);
                    }
                });
    }

    private void playSound(File file) {
        if (file == null) return;
        Uri myUri = Uri.fromFile(file);
        Logger.i("播放 " + file.getAbsolutePath());
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getContext(), myUri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }


    private Callable<File> cacheFileObservable(Context context, String fileName, byte[] data) {
        return new Callable<File>() {
            @Override
            public File call() throws Exception {
                return mFileManager.cacheFileOnDisk(context, fileName, data);
            }
        };
    }

    /**
     * make a operation observable
     *
     * @param func
     * @param <T>
     * @return the Observable
     */
    protected <T> Observable<T> makeObservable(final Callable<T> func) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(func.call());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
