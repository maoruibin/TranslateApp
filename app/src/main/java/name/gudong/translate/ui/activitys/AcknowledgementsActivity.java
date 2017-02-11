package name.gudong.translate.ui.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import name.gudong.translate.R;
import name.gudong.translate.mvp.model.entity.Acknowledgement;
import name.gudong.translate.mvp.presenters.AcknowledgementPresenter;
import name.gudong.translate.injection.components.AppComponent;
import name.gudong.translate.injection.modules.ActivityModule;
import name.gudong.translate.ui.adapter.AcknowledgementAdapter;
import name.gudong.translate.ui.common.DefaultItemDecoration;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AcknowledgementsActivity extends BaseActivity<AcknowledgementPresenter> implements AcknowledgementAdapter.AcknowledgementItemClickListener {

    private static final String TAG = "Acknowledgements";
    private static final String DATA_PATH = "thirdparty/list.json";
    private static final String LICENSE_PATH = "thirdparty/";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    AcknowledgementAdapter mAdapter;
    Map<String, String> mLicenseCache = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgements);
        ButterKnife.bind(this);


        mAdapter = new AcknowledgementAdapter(this, null);
        mAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DefaultItemDecoration(
                ContextCompat.getColor(this, R.color.bg_pop_white),
                ContextCompat.getColor(this, R.color.ff_divider),
                getResources().getDimensionPixelSize(R.dimen.ff_padding_large)
        ));

        loadAcknowledgements();
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent, ActivityModule activityModule) {
//        DaggerActivityComponent.builder()
//                .activityModule(activityModule)
//                .appComponent(appComponent)
//                .build()
//                .inject(this);
    }

    // OnItemClickListener

    @Override
    public void onItemClick(View view, int position) {
        String url = mAdapter.getItem(position).url;
        //WebViewHelper.openUrl(this, url);
    }

    @Override
    public void openLicense(int position) {
        openLicense(mAdapter.getItem(position));
    }

    // Acknowledgements and License

    private void loadAcknowledgements() {

        Subscription subscription = Observable.just(DATA_PATH)
                .flatMap(new Func1<String, Observable<List<Acknowledgement>>>() {
                    @Override
                    public Observable<List<Acknowledgement>> call(String path) {
                        try {
                            InputStream in = getAssets().open(path);
                            List<Acknowledgement> acknowledgements = new Gson().fromJson(
                                    new InputStreamReader(in),
                                    new TypeToken<List<Acknowledgement>>() {
                                    }.getType()
                            );
                            return Observable.just(acknowledgements);
                        } catch (IOException e) {
                            return Observable.error(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Acknowledgement>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Acknowledgement> acknowledgements) {
                        mAdapter.setData(acknowledgements);
                        mAdapter.notifyDataSetChanged();
                    }
                });
        new CompositeSubscription().add(subscription);

    }

    private void openLicense(final Acknowledgement acknowledgement) {
        final String licensePath = LICENSE_PATH + acknowledgement.licensePath;
        if (mLicenseCache.containsKey(licensePath)) {
            showLicenseDialog(acknowledgement.name, mLicenseCache.get(licensePath));
            return;
        }
        Observable.just(licensePath)
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String path) {
                        BufferedReader reader = null;
                        try {
                            StringBuilder stringBuilder = new StringBuilder();
                            InputStream in = getAssets().open(path);
                            reader = new BufferedReader(new InputStreamReader(in));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                                stringBuilder.append('\n');
                            }
                            return Observable.just(stringBuilder.toString());
                        } catch (IOException e) {
                            return Observable.error(e);
                        } finally {
                            try {
                                if (reader != null)
                                    reader.close();
                            } catch (IOException e) {
                                Log.e(TAG, "While reading license at: " + licensePath, e);
                            }
                        }
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String licenseContent) {
                        mLicenseCache.put(licensePath, licenseContent);
                        showLicenseDialog(acknowledgement.name, licenseContent);
                    }
                });
    }

    private void showLicenseDialog(String title, String content) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(R.string.close, null)
                .show();
    }
}
