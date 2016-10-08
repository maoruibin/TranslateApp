/*
 *  Copyright (C) 2015 GuDong <gudong.name@gmail.com>
 *
 *  This file is part of GdTranslate
 *
 *  GdTranslate is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  GdTranslate is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with GdTranslate.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package name.gudong.translate.ui.activitys;

import android.animation.Animator;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.umeng.analytics.MobclickAgent;

import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.gudong.translate.BuildConfig;
import me.gudong.translate.R;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.model.type.EDurationTipTime;
import name.gudong.translate.mvp.model.type.EIntervalTipTime;
import name.gudong.translate.mvp.model.type.ETranslateFrom;
import name.gudong.translate.mvp.presenters.BasePresenter;
import name.gudong.translate.mvp.presenters.MainPresenter;
import name.gudong.translate.mvp.views.IMainView;
import name.gudong.translate.reject.components.AppComponent;
import name.gudong.translate.reject.components.DaggerActivityComponent;
import name.gudong.translate.reject.modules.ActivityModule;
import name.gudong.translate.ui.NavigationManager;
import name.gudong.translate.util.DialogUtil;
import name.gudong.translate.util.InputMethodUtils;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.ViewUtil;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView {
    @BindView(android.R.id.input)
    EditText mInput;
    @BindView(R.id.list_result)
    LinearLayout mList;
    @BindView(R.id.sp_translate_way)
    AppCompatSpinner mSpTranslateWay;

    @BindView(R.id.iv_favorite)
    ImageView mIvFavorite;
    @BindView(R.id.iv_sound)
    ImageView mIvSound;
    @BindView(R.id.iv_paste)
    ImageView mIvPaste;
    @BindView(R.id.tv_clear)
    TextView mTvClear;
    @BindView(R.id.rl_action)
    RelativeLayout mRlAction;
    @BindView(R.id.bt_translate)
    Button mBtTranslate;

    Menu mMenu;

    private boolean isFavorite;

    private BottomSheetBehavior mBottomSheetBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addListener();
        startListenService();
        initSpinner();
        checkTranslateWay();
        checkVersion();
        initConfig();
        checkIntent();
        mPresenter.dayline();
//        View bottomSheet = findViewById( R.id.bottom_sheet);
//        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//        mBottomSheetBehavior.setPeekHeight((int) getResources().getDimension(R.dimen.actionbar_height));
//        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

//        FrameLayout parentThatHasBottomSheetBehavior = (FrameLayout) findViewById(R.id.fl_bottom_sheet);
//        mBottomSheetBehavior = BottomSheetBehavior.from(parentThatHasBottomSheetBehavior);
//
//        final View peakView = findViewById(R.id.drag_me);
//        peakView.post(new Runnable() {
//            @Override
//            public void run() {
//                mBottomSheetBehavior.setPeekHeight(peakView.getHeight());
//            }
//        });
    }

    private void checkIntent() {
        mPresenter.checkIntentFromClickTipView(getIntent());
    }

    @Override
    public void onResume() {
        super.onResume();
        checkSomething();
        if(BuildConfig.DEBUG){
            SpUtils.setAppFront(this,false);
        }else{
            SpUtils.setAppFront(this,true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpUtils.setAppFront(this,false);
    }

    private void checkTranslateWay() {
        mPresenter.prepareTranslateWay();
    }

    private void initConfig() {
        mPresenter.clearSoundCache();
    }

    private void checkVersion() {
        if (BuildConfig.DEBUG) return;
        mPresenter.checkVersionAndShowChangeLog();

    }
    private void checkSomething() {
        if(!mPresenter.hasExtraResult(getIntent())){
            //检查粘贴板有没有英文单词 如果有就查询一次 并且显示给用户
            mPresenter.checkClipboard();
        }
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent, ActivityModule activityModule) {
        DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .activityModule(activityModule)
                .build()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mPresenter.prepareOptionSettings(menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_opinion:
                NavigationManager.gotoSendEmail(this);
                MobclickAgent.onEvent(this,"menu_opinion");
                break;
            case R.id.menu_book:
                //WordsBookActivity.gotoWordsBook(this);
                //MobclickAgent.onEvent(this,"open_book");
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.menu_about:
                DialogUtil.showAbout(this);
                MobclickAgent.onEvent(this,"menu_about");
                closeKeyboard();
                break;
            case R.id.menu_setting:
                MobclickAgent.onEvent(this,"menu_setting");
                closeKeyboard();
                NavigationManager.gotoSetting(this);
                break;
            case R.id.menu_score:
                mPresenter.gotoMarket();
                MobclickAgent.onEvent(this,"menu_score");
                break;
            case R.id.menu_support:
                DialogUtil.showSupport(this);
                MobclickAgent.onEvent(this,"menu_support");
                break;

            case R.id.menu_open_jit_or_nor:
                boolean isOpenJit = item.isChecked();
                SpUtils.setOpenJITOrNot(this, !isOpenJit);
                break;
            case R.id.interval_one_minute:
                selectIntervalTime(item, EIntervalTipTime.ONE_MINUTE.name());
                break;
            case R.id.interval_three_minute:
                selectIntervalTime(item, EIntervalTipTime.THREE_MINUTE.name());
                break;
            case R.id.interval_five_minute:
                selectIntervalTime(item, EIntervalTipTime.FIVE_MINUTE.name());
                break;
            case R.id.interval_ten_minute:
                selectIntervalTime(item, EIntervalTipTime.TEN_MINUTE.name());
                break;
            case R.id.interval_thirty_minute:
                selectIntervalTime(item, EIntervalTipTime.THIRTY_MINUTE.name());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectIntervalTime(MenuItem item, String name) {
        SpUtils.setIntervalTipTime(this, name);
        item.setChecked(true);
        startListenService();
    }

    private void selectEngine(ETranslateFrom way) {
        SpUtils.setTranslateEngine(this, way.name());
        checkInputAndResearch();
    }

    /**
     * 检查输入框是不是已经有输入的内容 如果有自动搜索，
     * 主要是切换搜索引擎时会用到
     */
    private void checkInputAndResearch() {
        String input = mInput.getText().toString().trim();
        if(isEmptyWord(input,false))return;
        //if(StringUtils.isMoreThanOneWord(input))return;
        translate();
    }

    private void addListener() {
        mInput.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                MobclickAgent.onEvent(this,"action_translate_by_keyboard");
                translate();
                return true;
            }
            return false;
        });

        mInput.addTextChangedListener(new TextWatcher() {
            private String mTemp;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTemp = s.toString();
                mTvClear.setVisibility(mTemp.isEmpty()?View.INVISIBLE:View.VISIBLE);
            }
        });

        mSpTranslateWay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectEngine(ETranslateFrom.BAI_DU);
                        MobclickAgent.onEvent(getApplicationContext(),"way_baidu");
                        break;
                    case 1:
                        selectEngine(ETranslateFrom.YOU_DAO);
                        MobclickAgent.onEvent(getApplicationContext(),"way_youdao");
                        break;
                    case 2:
                        selectEngine(ETranslateFrom.JIN_SHAN);
                        MobclickAgent.onEvent(getApplicationContext(),"way_jinshan");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean checkInput(String input){
        if (isEmptyWord(input, true)) return false;
        //不检查输入的字符串是不是超过两个
//        if (StringUtils.isMoreThanOneWord(input)){
//            String msg = getString(R.string.msg_not_support_sentence);
//            DialogUtil.showSingleMessage(this, msg, getString(R.string.action_know));
//            return false;
//        }
        return true;
    }

    private boolean isEmptyWord(String input, boolean withEmptyPoint) {
        if (TextUtils.isEmpty(input)) {
            if(withEmptyPoint){
                Toast.makeText(MainActivity.this, R.string.tip_input_words, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    private void translate() {
        closeKeyboard();
        final String input = mInput.getText().toString().trim();
        if(checkInput(input)){
            mPresenter.executeSearch(input);
        }
    }


    /**
     * set text to EditText view and move curse to last
     *
     * @param text which need to translate
     */
    @Override
    public void onInitSearchText(String text) {
        mInput.setText(text);
        ViewUtil.setEditTextSelectionToEnd(mInput);
        mTvClear.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrepareTranslate() {
        mList.removeAllViews();
        mRlAction.setVisibility(View.GONE);
        mBtTranslate.setText(R.string.action_translating);
        mBtTranslate.setEnabled(false);
        mIvFavorite.setEnabled(false);
        mIvSound.setEnabled(false);
        mIvPaste.setEnabled(false);
    }

    @Override
    public void onError(Throwable e) {
        String msg;
        if (e instanceof JsonSyntaxException) {
            msg = getString(R.string.tip_fail_translate)+ (BuildConfig.DEBUG ? "  "+e.getMessage():"");
        }else if(e instanceof UnknownHostException){
            msg = getString(R.string.tip_unknown_host)+ (BuildConfig.DEBUG ? "  "+e.getMessage():"");
        }else{
            msg = getString(R.string.tip_unknown)+ (BuildConfig.DEBUG ? "  "+e.getMessage():"");
        }
        mList.addView(ViewUtil.getWordsView(MainActivity.this, msg, android.R.color.holo_red_light,false));
        mBtTranslate.setEnabled(true);
        mBtTranslate.setText(R.string.action_translate);
    }

    @Override
    public void addExplainItem(String explain) {
        mList.addView(ViewUtil.getWordsView(MainActivity.this, explain, R.color.color_explain,true));
    }

    @Override
    public void initTranslateEngineSetting(ETranslateFrom from) {
        mSpTranslateWay.setSelection(from.getIndex());
    }

    @Override
    public void initDurationTimeSetting(Menu menu, EDurationTipTime durationTime) {
        switch (durationTime) {
            case ONE_SECOND:
                menu.findItem(R.id.duration_two_second).setChecked(true);
                break;
            case FOUR_SECOND:
                menu.findItem(R.id.duration_four_second).setChecked(true);
                break;
            case SIX_SECOND:
                menu.findItem(R.id.duration_six_second).setChecked(true);
                break;
        }
    }

    @Override
    public void initIntervalTimeSetting(Menu menu, EIntervalTipTime intervalTime) {
        switch (intervalTime) {
            case ONE_MINUTE:
                menu.findItem(R.id.interval_one_minute).setChecked(true);
                break;
            case THREE_MINUTE:
                menu.findItem(R.id.interval_three_minute).setChecked(true);
                break;
            case FIVE_MINUTE:
                menu.findItem(R.id.interval_five_minute).setChecked(true);
                break;
            case TEN_MINUTE:
                menu.findItem(R.id.interval_ten_minute).setChecked(true);
                break;
            case THIRTY_MINUTE:
                menu.findItem(R.id.interval_thirty_minute).setChecked(true);
                break;
        }
    }

    @OnClick(R.id.bt_translate)
    public void onClickTranslate(View view) {
        MobclickAgent.onEvent(getApplicationContext(),"action_translate");
        translate();
    }

    @OnClick(R.id.tv_clear)
    public void onClickClear(View view) {
        MobclickAgent.onEvent(getApplicationContext(),"action_clear");
        resetView();
        InputMethodUtils.openSoftKeyboard(this,mInput);
    }

    @OnClick(R.id.tv_point)
    public void onClickInputPoint(View view) {
        MobclickAgent.onEvent(getApplicationContext(),"action_input_point");
        mInput.requestFocus();
    }

    private void resetView() {
        clearInputContent();
        mPresenter.clearClipboard();
        isFavorite = false;
        mIvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        mIvFavorite.setTag(null);
        mIvSound.setTag(null);
        mList.removeAllViews();
        mRlAction.setVisibility(View.GONE);
    }

    private void clearInputContent() {
        String content = mInput.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            mInput.setText("");
        }
    }

    @OnClick(R.id.iv_favorite)
    public void onClickFavorite(final View view) {
        MobclickAgent.onEvent(getApplicationContext(),"favorite_main");
        mPresenter.startFavoriteAnim(view, new BasePresenter.AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Object obj = view.getTag();
                if (obj != null && obj instanceof Result) {
                    Result entity = (Result) obj;
                    if (isFavorite) {
                        mPresenter.unFavoriteWord(entity);
                        mIvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        isFavorite = false;
                    } else {
                        mPresenter.favoriteWord(entity);
                        mIvFavorite.setImageResource(R.drawable.ic_favorite_pink_24dp);
                        isFavorite = true;
                    }
                }
            }
        });
    }

    @OnClick(R.id.iv_paste)
    public void onClickPaste(View view){
        MobclickAgent.onEvent(getApplicationContext(),"action_paste");
        closeKeyboard();
        Toast.makeText(MainActivity.this, "长按翻译结果可复制", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.iv_sound)
    public void onClickSound(View view){
        MobclickAgent.onEvent(getApplicationContext(),"sound_main_activity");
        Object obj = view.getTag();
        if (obj != null && obj instanceof Result) {
            Result entity = (Result) obj;
            mPresenter.playSound(entity);
        }
        mPresenter.startSoundAnim(view);
    }

    @Override
    public void initReciteSetting(Menu menu, boolean isOpen) {
        menu.findItem(R.id.menu_use_recite_or_not).setChecked(isOpen);
        menu.findItem(R.id.menu_interval_tip_time).setVisible(isOpen);

        menu.findItem(R.id.menu_use_recite_or_not).setVisible(false);
        menu.findItem(R.id.menu_interval_tip_time).setVisible(false);

    }

    @Override
    public void initJITSetting(Menu menu, boolean isOpen) {
        menu.findItem(R.id.menu_open_jit_or_nor).setChecked(isOpen);
    }

    @Override
    public void closeKeyboard() {
        InputMethodUtils.closeSoftKeyboard(mInput);
    }

    @Override
    public void showPlaySound() {
        mIvSound.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePlaySound() {
        mIvSound.setVisibility(View.GONE);
    }

    @Override
    public void addTagForView(Result result) {
        mIvFavorite.setTag(result);
        mIvSound.setTag(result);
    }

    @Override
    public void initWithFavorite() {
        mIvFavorite.setImageResource(R.drawable.ic_favorite_pink_24dp);
        isFavorite = true;
    }

    @Override
    public void initWithNotFavorite() {
        mIvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        isFavorite = false;
    }

    @Override
    public void onTranslateComplete() {
        mBtTranslate.setEnabled(true);
        mBtTranslate.setText(R.string.action_translate);
        mRlAction.setVisibility(View.VISIBLE);

        mIvFavorite.setEnabled(true);
        mIvSound.setEnabled(true);
        mIvPaste.setEnabled(true);
    }

    private void startListenService() {
        mPresenter.startListenClipboardService();
    }


    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.translate_way, R.layout.spinner_drop_list_title);
        adapter.setDropDownViewResource(R.layout.spinner_drop_list_item);
        mSpTranslateWay.setAdapter(adapter);
    }
}
