package name.gudong.translate.mvp.model;

import name.gudong.translate.mvp.model.entity.translate.YouDaoResult;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by GuDong on 2017/1/14 20:10.
 * Contact with gudong.name@gmail.com.
 */

public interface ApiYouDao {

    //http://fanyi.youdao.com/openapi.do?keyfrom=gudong&key=1235023502&type=data&doctype=json&version=1.1&q=what
    @GET("openapi.do?")
    Observable<YouDaoResult>translateYouDao(
            @Query("q") String q,
            @Query("keyfrom") String keyfrom,
            @Query("key") String key,
            @Query("type") String type,
            @Query("doctype") String doctype,
            @Query("version") String version
    );
}
