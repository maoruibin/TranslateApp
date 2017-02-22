package name.gudong.translate.mvp.model;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * Created on 2017/2/22
 *
 * @author Levine
 */

public interface ApiGoogle {

    /**
     * http://translate.google.cn/translate_a/single?client=gtx&sl=en&tl=zh-CN&dt=t&q=google
     */
    @Streaming
    @GET("translate_a/single?client=gtx&dt=t&ie=UTF-8&oe=UTF-8&sl=auto")
    Observable<ResponseBody> translateGoogle(
            @Query("q") String q,
            @Query("tl") String targetLanguage
    );
}
