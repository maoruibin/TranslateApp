package name.gudong.translate.mvp.model;

import name.gudong.translate.mvp.model.entity.translate.JinShanResult;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by GuDong on 2017/1/14 20:10.
 * Contact with gudong.name@gmail.com.
 */

public interface ApiJinShan {


    //http://dict-co.iciba.com/api/dictionary.php?type=json&w=do&key=3BE8E8ACA43FDA088E52EC05FA8FA203
    @GET("api/dictionary.php?")
    Observable<JinShanResult> translateJinShan(
            @Query("w") String q,
            @Query("key") String key,
            @Query("type") String type
    );

}
