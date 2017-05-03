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

package name.gudong.translate.injection.modules;


import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import name.gudong.translate.BuildConfig;
import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.ApiBaidu;
import name.gudong.translate.mvp.model.ApiGoogle;
import name.gudong.translate.mvp.model.ApiJinShan;
import name.gudong.translate.mvp.model.ApiYouDao;
import name.gudong.translate.mvp.model.SingleRequestService;
import name.gudong.translate.mvp.model.type.ETranslateFrom;
import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by GuDong on 12/27/15 16:17.
 * Contact with gudong.name@gmail.com.
 *
 * Updated by Levine on 2/21/17 add google api
 */
@Module
public class ApiServiceModel {

    @Provides
    @Singleton
    SingleRequestService provideDownloadService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.baidu.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(SingleRequestService.class);
    }

    @Provides
    @Singleton
    ApiBaidu provideApiBaidu() {
        return createService(ETranslateFrom.BAI_DU);
    }

    @Provides
    @Singleton
    ApiYouDao provideApiYouDao() {
        return createService(ETranslateFrom.YOU_DAO);
    }

    @Provides
    @Singleton
    ApiJinShan provideApiJinShan() {
        return createService(ETranslateFrom.JIN_SHAN);
    }

    @Provides
    @Singleton
    ApiGoogle provideApiGoogle(){return createService(ETranslateFrom.GOOGLE);}

    private <S> S createService(ETranslateFrom type) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(HttpUrl.parse(type.getUrl()))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

        builder.client(provideOkHttpClient());
        return (S) builder.build().create(type.getAqiClass());
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        Cache cache = new Cache(GDApplication.get().getCacheDir(), 10240 * 1024);
        
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        builder.addNetworkInterceptor(new CacheInterceptor())
                .cache(cache)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        return builder.build();
    }
    
    private class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    //cache for 30 days
                    .header("Cache-Control", "max-age=" + 3600 * 24 * 30)
                    .build();
        }
    }
}
