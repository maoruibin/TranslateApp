package name.gudong.translate.mvp.presenters;

import android.app.Activity;

import com.litesuits.orm.LiteOrm;

import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.views.IAcknowledgementView;

/**
 * Created by GuDong on 9/11/16 22:56.
 * Contact with gudong.name@gmail.com.
 */
public class AcknowledgementPresenter extends BasePresenter<IAcknowledgementView> {
    public AcknowledgementPresenter(LiteOrm liteOrm, WarpAipService apiService, Activity activity) {
        super(liteOrm, apiService, activity);
    }
}
