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
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import name.gudong.translate.manager.FileManager;
import name.gudong.translate.mvp.model.DownloadService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.mvp.views.IBaseView;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
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
    protected Activity mActivity;
    protected Service mService;
    protected WarpAipService mWarpApiService;
    protected DownloadService mDownloadService;
    protected LiteOrm mLiteOrm;

    protected FileManager mFileManager = new FileManager();

    public BasePresenter(LiteOrm liteOrm, WarpAipService apiService,Activity activity) {
        mLiteOrm = liteOrm;
        mWarpApiService = apiService;
        mActivity = activity;
    }

    public BasePresenter(LiteOrm liteOrm, WarpAipService apiService, DownloadService downloadService, Activity activity) {
        mLiteOrm = liteOrm;
        mWarpApiService = apiService;
        mDownloadService = downloadService;
        mActivity = activity;
    }

    public BasePresenter(LiteOrm liteOrm, WarpAipService apiService,DownloadService downloadService,Service service) {
        mLiteOrm = liteOrm;
        mWarpApiService = apiService;
        mDownloadService = downloadService;
        mService = service;
    }

    public void onCreate(){}

    /**
     * attach IBaseView to Presenter
     * @param view view
     */
    public void attachView(V view){
        this.mView = view;
    }

    public void onDestroy(){
        this.mView = null;
    }

    protected Context getContext(){
        return mActivity == null? mService:mActivity;
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
        List<Result>list = mLiteOrm.query(queryBuilder);
        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    protected long insertResultToDb(Result entity){
        return mLiteOrm.insert(entity);
    }

    protected int deleteResultFromDb(Result entity){
        return mLiteOrm.delete(entity);
    }

    public void playSound(Result entity) {
        Observable.just(entity)
                .filter(result->{
                    return result != null && !TextUtils.isEmpty(entity.getMp3FileName());
                })
                .subscribe(new Action1<Result>() {
                    @Override
                    public void call(Result entity) {
                        String fileName = entity.getMp3FileName();
                        String mp3Url = entity.getEnMp3();
                        File cacheFile = mFileManager.getCacheFileByUrl(getContext(), fileName);
                        if (cacheFile != null && cacheFile.exists()) {
                            playSound(cacheFile);
                            return;
                        }

                        Call<ResponseBody> call = mDownloadService.downloadSoundFile(mp3Url);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {
                                if (response.isSuccess()) {
                                    try {
                                        cacheAndPlaySound(getContext(), fileName, response.body().bytes());
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
                });
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
        if(file == null)return;
        Uri myUri = Uri.fromFile(file);
        Logger.i("播放 "+file.getAbsolutePath());
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(mActivity, myUri);
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
     * @param func
     * @param <T>
     * @return the Observable
     */
    protected  <T> Observable<T> makeObservable(final Callable<T> func) {
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
