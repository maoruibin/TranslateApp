package name.gudong.translate.mvp.model;

import name.gudong.translate.mvp.model.entity.translate.BaiDuResult;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by GuDong on 2017/1/14 20:10.
 * Contact with gudong.name@gmail.com.
 */

public interface ApiBaidu {

    //http://api.fanyi.baidu.com/api/trans/vip/translate?q=apple&from=en&to=zh&appid=2015063000000001&salt=1435660288&sign=f89f9594663708c1605f3d736d01d2d4
    @GET("api/trans/vip/translate?")
    Observable<BaiDuResult> translateBaiDu(
            @Query("q") String q,
            @Query("from")String from,
            @Query("to")String to,
            @Query("appid")String appid,
            @Query("salt")String salt,
            @Query("sign")String sign
    );
}
