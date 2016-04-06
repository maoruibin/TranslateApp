package name.gudong.translate.mvp;

import com.squareup.okhttp.ResponseBody;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by GuDong on 4/3/16 21:27.
 * Contact with gudong.name@gmail.com.
 */
public interface DownloadService {

    String KEY_URL = "http://res.iciba.com/";

    //http://res.iciba.com/resource/amp3/oxford/0/24/8a/248a2aa9259a98ecb7a1ff677a0feed2.mp3
    @GET("resource/amp3/{first}/{second}/{third}/{forth}/{name}")
    Call<ResponseBody> downloadFileWithDynamicUrlSync(
            @Path("first") String first,
            @Path("second") String second,
            @Path("third") String third,
            @Path("forth") String forth,
            @Path("name") String name
    );
}
