package name.gudong.translate.mvp.model;


import name.gudong.translate.mvp.model.entity.RecommendedResponse;
import name.gudong.translate.mvp.model.entity.dayline.JinshanDayLineEntity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by GuDong on 4/3/16 21:27.
 * Contact with gudong.name@gmail.com.
 */
public interface SingleRequestService {
    @GET
    Call<ResponseBody> downloadSoundFile(@Url String soundUrl);

    @GET
    Observable<JinshanDayLineEntity> dayline(@Url String daylineUrl);

    @GET
    Observable<RecommendedResponse> app_recommend(@Url String app_recommend_url);
}
