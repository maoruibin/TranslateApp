package name.gudong.translate.mvp.model;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

/**
 * Created by GuDong on 4/3/16 21:27.
 * Contact with gudong.name@gmail.com.
 */
public interface SingleRequestService {
    @GET
    Call<ResponseBody> downloadSoundFile(@Url String soundUrl);
}
