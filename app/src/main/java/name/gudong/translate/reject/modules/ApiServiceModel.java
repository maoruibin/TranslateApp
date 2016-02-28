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

package name.gudong.translate.reject.modules;

import com.squareup.okhttp.HttpUrl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import name.gudong.translate.mvp.model.ApiService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.util.SpUtils;
import retrofit.BaseUrl;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;


/**
 * Created by GuDong on 12/27/15 16:17.
 * Contact with gudong.name@gmail.com.
 */
@Module
public class ApiServiceModel {

    @Provides
    @Singleton
    ApiService provideApiService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new BaseUrl() {
                    @Override
                    public HttpUrl url() {
                        return HttpUrl.parse(SpUtils.getUrlByLocalSetting());
                    }
                })
                // for RxJava
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiService.class);
    }


    @Provides
    @Singleton
    WarpAipService provideWarpApiService(ApiService apiService){
        return new WarpAipService(apiService);
    }


}
