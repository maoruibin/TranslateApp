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

import name.gudong.translate.mvp.model.entity.BaiDuResult;
import name.gudong.translate.mvp.model.entity.JinShanResult;
import name.gudong.translate.mvp.model.entity.YouDaoResult;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by GuDong on 12/27/15 16:06.
 * Contact with gudong.name@gmail.com.
 */
public interface ApiService {

    //http://api.fanyi.baidu.com/api/trans/vip/translate?q=apple&from=en&to=zh&appid=2015063000000001&salt=1435660288&sign=f89f9594663708c1605f3d736d01d2d4
    @GET("?")
    Observable<BaiDuResult> translateBaiDu(
            @Query("q") String q,
            @Query("from")String from,
            @Query("to")String to,
            @Query("appid")String appid,
            @Query("salt")String salt,
            @Query("sign")String sign
    );

    //http://fanyi.youdao.com/openapi.do?keyfrom=gudong&key=1235023502&type=data&doctype=json&version=1.1&q=what
    @GET("?")
    Observable<YouDaoResult>translateYouDao(
            @Query("q") String q,
            @Query("keyfrom") String keyfrom,
            @Query("key") String key,
            @Query("type") String type,
            @Query("doctype") String doctype,
            @Query("version") String version
    );

    //http://dict-co.iciba.com/api/dictionary.php?type=json&w=do&key=3BE8E8ACA43FDA088E52EC05FA8FA203
    @GET("?")
    Observable<JinShanResult> translateJinShan(
            @Query("w") String q,
            @Query("key") String key,
            @Query("type") String type
    );

}

