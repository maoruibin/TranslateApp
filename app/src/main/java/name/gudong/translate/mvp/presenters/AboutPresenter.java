package name.gudong.translate.mvp.presenters;

import android.content.Context;

import com.litesuits.orm.LiteOrm;

import java.util.List;

import javax.inject.Inject;

import me.drakeet.multitype.Items;
import me.drakeet.support.about.Card;
import me.drakeet.support.about.Category;
import me.drakeet.support.about.Contributor;
import me.drakeet.support.about.License;
import me.drakeet.support.about.Recommended;
import name.gudong.translate.R;
import name.gudong.translate.mvp.model.SingleRequestService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.model.entity.RecommendedResponse;
import name.gudong.translate.mvp.views.IAboutView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by GuDong on 2017/10/25 00:09.
 * Contact with gudong.name@gmail.com.
 */

public class AboutPresenter extends BasePresenter<IAboutView> {
    @Inject
    public AboutPresenter(LiteOrm liteOrm, WarpAipService apiService, SingleRequestService singleRequestService, Context context) {
        super(liteOrm, apiService, singleRequestService, context);
    }

    public void getLinkApps(final Items items) {
        mSingleRequestService.app_recommend("https://recommend.wetolink.com/api/v2/app_recommend/pull?limit=50&package_name=name.gudong.translate")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RecommendedResponse>() {
                    @Override
                    public void call(RecommendedResponse apps) {
                        List<Recommended> appList = apps.data;
                        formatItems(appList, items);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //call onError to stop crashing the app
                        //TODO error handling
                        formatItems(null, items);
                    }
                });
    }

    private void formatItems(List<Recommended> appList, Items items) {
        items.add(new Category("介绍与帮助"));
        items.add(new Card(getString(R.string.card_content)));

        items.add(new Category("Main Developer"));
        items.add(new Contributor(R.drawable.profile_circle_for_donate, "gudong", "Developer & designer", "http://weibo.com/maoruibin"));
//        items.add(new Contributor(R.drawable.header, "TonyLOfficial", "designer", "http://weibo.com/u/2795793021"));
//        items.add(new Contributor(R.drawable.header, "chenyingsunny", "Developer", "https://github.com/chenyingsunny"));
//        items.add(new Contributor(R.drawable.header, "leizhiyuan", "Developer", "https://github.com/leizhiyuan"));
//        items.add(new Contributor(R.drawable.header, "kymjs", "Developer", "https://github.com/kymjs"));
//        items.add(new Contributor(R.drawable.header, "WonShaw", "Developer", "https://github.com/WonShaw"));
//        items.add(new Contributor(R.drawable.header, "LevineLiu", "Developer", "https://github.com/LevineLiu"));
//        items.add(new Contributor(R.drawable.header, "LostKe", "Developer", "https://github.com/LostKe"));

        if (appList != null && !appList.isEmpty()) {
            items.add(new Category("应用推荐"));
            items.addAll(appList);
        }
        items.add(new Category("Open Source Licenses"));
        items.add(new License("RxJava", "ReactiveX", License.APACHE_2, "https://github.com/ReactiveX/RxJava"));
        items.add(new License("RxAndroid", "ReactiveX", License.APACHE_2, "https://github.com/ReactiveX/RxAndroid"));
        items.add(new License("Gson", "Google", License.APACHE_2, "https://github.com/google/gson"));
        items.add(new License("Retrofit 2", "square", License.APACHE_2, "https://github.com/square/retrofit"));
        items.add(new License("Butter Knife", "JakeWharton", License.APACHE_2, "https://github.com/JakeWharton/butterknife"));
        items.add(new License("MultiType", "drakeet", License.APACHE_2, "https://github.com/drakeet/MultiType"));
        items.add(new License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"));

        items.add(new Category("什么是划词翻译"));
        items.add(new LinkItem("划词翻译介绍"));

        mView.update();
    }

    private String getString(int card_content) {
        return mContext.getString(card_content);
    }

    public class LinkItem {
        public LinkItem(String introduce) {
        }
    }
}
