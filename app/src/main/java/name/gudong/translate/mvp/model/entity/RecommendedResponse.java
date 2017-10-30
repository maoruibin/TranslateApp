package name.gudong.translate.mvp.model.entity;

import android.support.annotation.Keep;

import java.util.List;

import me.drakeet.support.about.Recommended;

@Keep
public class RecommendedResponse {
    public int code;
    public List<Recommended> data;

    public RecommendedResponse() {
    }
}
