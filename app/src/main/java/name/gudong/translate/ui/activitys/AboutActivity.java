package name.gudong.translate.ui.activitys;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import me.drakeet.multitype.Items;
import me.drakeet.support.about.AbsAboutActivity;
import me.drakeet.support.about.Card;
import me.drakeet.support.about.provided.PicassoImageLoader;
import name.gudong.translate.BuildConfig;
import name.gudong.translate.GDApplication;
import name.gudong.translate.R;
import name.gudong.translate.injection.components.AppComponent;
import name.gudong.translate.injection.components.DaggerActivityComponent;
import name.gudong.translate.injection.modules.ActivityModule;
import name.gudong.translate.mvp.presenters.AboutPresenter;
import name.gudong.translate.mvp.views.IAboutView;
import name.gudong.translate.ui.LinkCardViewBinder;

public class AboutActivity extends AbsAboutActivity implements IAboutView {
    @Inject
    protected AboutPresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActivityComponent(GDApplication.getAppComponent(),new ActivityModule(this));
        mPresenter.attachView(this);
        setImageLoader(new PicassoImageLoader());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.getAdapter().register(Card.class, new LinkCardViewBinder());
    }

    protected void setupActivityComponent(AppComponent appComponent, ActivityModule activityModule) {
        DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .activityModule(activityModule)
                .build()
                .inject(this);
    }

    @Override
    protected void onCreateHeader(@NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version) {
        icon.setImageResource(R.mipmap.ic_launcher);
        slogan.setText(R.string.app_name);
        version.setText("v" + BuildConfig.VERSION_NAME);
    }
    @Override
    protected void onItemsCreated(@NonNull Items items) {
        mPresenter.getLinkApps(items);
    }

    @Override
    public void update() {
        getAdapter().notifyDataSetChanged();
    }
}