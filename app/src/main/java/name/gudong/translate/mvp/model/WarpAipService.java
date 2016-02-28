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

package name.gudong.translate.mvp.model;

import com.orhanobut.logger.Logger;
import me.gudong.translate.BuildConfig;
import name.gudong.translate.mvp.model.entity.AbsResult;
import name.gudong.translate.mvp.model.entity.BaiDuResult;
import name.gudong.translate.mvp.model.entity.JinShanResult;
import name.gudong.translate.mvp.model.entity.YouDaoResult;
import name.gudong.translate.mvp.model.type.ETranslateFrom;
import name.gudong.translate.util.SignUtils;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by GuDong on 1/22/16 10:37.
 * Contact with gudong.name@gmail.com.
 */
public class WarpAipService {

    static ApiService mApiService;

    public WarpAipService(ApiService apiService) {
        mApiService = apiService;
    }

    public Observable<AbsResult> translate(ETranslateFrom way, String query) {
        Observable<AbsResult> resultObservable = null;
        query = query.toLowerCase();
        switch (way){
            case YOU_DAO:
                resultObservable = mApiService.translateYouDao(
                        query,
                        BuildConfig.YOUDAO_USERNAME,
                        BuildConfig.YOUDAO_KEY,
                        BuildConfig.YOUDAO_TYPE,
                        BuildConfig.RESULT_JSON,
                        BuildConfig.YOUDAO_VERSION)
                        .flatMap(new Func1<YouDaoResult, Observable<AbsResult>>() {
                            @Override
                            public Observable<AbsResult> call(YouDaoResult youDaoResult) {
                                return Observable.just(youDaoResult);
                            }
                        });
                break;
            case JIN_SHAN:
                resultObservable = mApiService.translateJinShan(
                        query,
                        //JINSHAN_FANYI_KEY
                        BuildConfig.ICIBA_KEY,
                        BuildConfig.RESULT_JSON)
                        .flatMap(new Func1<JinShanResult, Observable<AbsResult>>() {
                            @Override
                            public Observable<AbsResult> call(JinShanResult result) {
                                return Observable.just(result);
                            }
                        });
                break;
            case BAI_DU:
                String salt = SignUtils.getRandomInt(10);
                String sign = SignUtils.getSign(BuildConfig.BAIDU_APP_ID, query, salt, BuildConfig.BAIDU_SCREAT_KEY);
                Logger.i(salt);
                Logger.i(sign);
                resultObservable = mApiService.translateBaiDu(
                        query,
                        BuildConfig.LANGUAGE_AUTO,
                        BuildConfig.LANGUAGE_AUTO,
                        BuildConfig.BAIDU_APP_ID,
                        salt,
                        sign)
                        .flatMap(new Func1<BaiDuResult, Observable<AbsResult>>() {
                            @Override
                            public Observable<AbsResult> call(BaiDuResult result) {
                                return Observable.just(result);
                            }
                        });
                break;
        }
        return resultObservable;
    }
}
