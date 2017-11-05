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
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.umeng.analytics.MobclickAgent;

import java.net.UnknownHostException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import name.gudong.translate.BuildConfig;
import name.gudong.translate.R;
import name.gudong.translate.injection.components.AppComponent;
import name.gudong.translate.injection.components.DaggerActivityComponent;
import name.gudong.translate.injection.modules.ActivityModule;
import name.gudong.translate.mvp.model.entity.dayline.IDayLine;
import name.gudong.translate.mvp.model.entity.translate.JinShanResult;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.model.type.ETranslateFrom;
import name.gudong.translate.mvp.presenters.BasePresenter;
import name.gudong.translate.mvp.presenters.MainPresenter;
import name.gudong.translate.mvp.views.IMainView;
import name.gudong.translate.ui.NavigationManager;
import name.gudong.translate.util.DialogUtil;
import name.gudong.translate.util.InputMethodUtils;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.Utils;
import name.gudong.translate.util.ViewUtil;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView {
    private static final String TAG = "MainActivity";
    @BindView(android.R.id.input)
    AutoCompleteTextView mInput;
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

    @BindView(R.id.tv_dayline)
    TextView mTvDayline;
    @BindView(R.id.tv_dayline_note)
    TextView mTvDaylineNote;
    @BindView(R.id.iv_sound_dayline)
    AppCompatImageView mIvSoundDayline;
    @BindView(R.id.main_content)
    CoordinatorLayout coordinatorLayout;


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
        setUpDayline(false);
        checkIntent();
        boolean needShowGuidePermissionDialog = checkOverPermission();
        if(needShowGuidePermissionDialog){
            showGuidePermissionDialog();
        }else{
            guideCheck();
        }
    }

    private void guideCheck() {
        if (!SpUtils.hasShowGuide(this)) {
            showFloatTranslateExplainDialog();
        }
    }

    private void setUpDayline(boolean isOpenDayLine) {
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet_view);
        if (!isOpenDayLine) {
            bottomSheet.setVisibility(View.GONE);
            return;
        }
        //点击 和拖拽都可以打开bottom sheet
        bottomSheet.setOnClickListener(v -> onClickBottomSheet());
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(getResources().getDimensionPixelOffset(R.dimen.bottom_bar_height));

        mPresenter.dayline();
    }


    private void checkIntent() {
        mPresenter.checkIntentFromClickTipView(getIntent());
        //每日一句
        if (getIntent().getBooleanExtra("from_dayline_remind", false)) {
            onClickBottomSheet();
            MobclickAgent.onEvent(getApplicationContext(), "enter_mainactivity_by_click_notification_dayline");
            mIvSoundDayline.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onClickDaylineSound(findViewById(R.id.iv_sound_dayline));
                }
            }, 1000);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //检查粘贴板和 intent
        checkClipboard();
        addTranslateWaySelectListener();
        if (BuildConfig.DEBUG) {
            SpUtils.setAppFront(this, false);
        } else {
            SpUtils.setAppFront(this, true);
        }
    }

    private boolean checkOverPermission() {
        if (Utils.isAndroidM()) {
            if (!SpUtils.hasGrantDrawOverlays(this) && !Settings.canDrawOverlays(this)) {
                return true;
            } else {
                SpUtils.setDrawOverlays(this, true);
                return false;
            }
        } else {
            SpUtils.setDrawOverlays(this, true);
            return false;
        }
    }

    /**
     * 弹出引导用户打开悬浮权限的 dialog
     */
    private void showGuidePermissionDialog() {
        new AlertDialog.Builder(this).setMessage("检测到你的设备默禁用了浮窗权限，为了保证你可以正常使用咕咚翻译的划词翻译功能，需要你授予浮窗权限。")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        guideCheck();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showFloatTranslateExplainDialog(){
        DialogUtil.showGuideFloatTranslate(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpUtils.setAppFront(this, false);
    }

    private void checkTranslateWay() {
        mPresenter.prepareTranslateWay();
    }

    private void initConfig() {
        //mPresenter.clearSoundCache();
        mPresenter.analysisLocalDic();
    }

    private void checkVersion() {
        //if (BuildConfig.DEBUG) return;
        mPresenter.checkVersionAndShowChangeLog();

    }

    private void checkClipboard() {
        if (!mPresenter.hasExtraResult(getIntent()) && SpUtils.isAutoPasteWords(this)) {
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
        mMenu = menu;
        menu.findItem(R.id.menu_about).setTitle(formatAboutVersion());
        return true;
    }

    private String formatAboutVersion() {
        String about = getString(R.string.menu_about);
        return about.concat("(" + Utils.getVersionName(this) + ")");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_opinion:
                NavigationManager.gotoSendEmail(this);
                MobclickAgent.onEvent(this, "menu_opinion");
                break;
            case R.id.menu_book:
                WordsBookActivity.gotoWordsBook(this);
                MobclickAgent.onEvent(this, "open_book");
                break;
            case R.id.menu_about:
//                DialogUtil.showAbout(this, formatAboutVersion());
                NavigationManager.gotoAboutActivity(this);
                MobclickAgent.onEvent(this, "menu_about");
                closeKeyboard();
                break;
            case R.id.menu_setting:
                MobclickAgent.onEvent(this, "menu_setting");
                closeKeyboard();
                NavigationManager.gotoSetting(this);
                break;
            case R.id.menu_score:
                mPresenter.gotoMarket();
                MobclickAgent.onEvent(this, "menu_score");
                break;
            case R.id.menu_support:
                DialogUtil.showSupport(this);
                MobclickAgent.onEvent(this, "menu_support");
                break;
        }
        return super.onOptionsItemSelected(item);
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
        if (isEmptyWord(input, false)) return;
        //if(StringUtils.isMoreThanOneWord(input))return;
        translate();
    }

    private void addListener() {
        mInput.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                MobclickAgent.onEvent(this, "action_translate_by_keyboard");
                mInput.dismissDropDown();
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
                mTvClear.setVisibility(mTemp.isEmpty() ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    private void addTranslateWaySelectListener() {
        mSpTranslateWay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectEngine(ETranslateFrom.BAI_DU);
                        MobclickAgent.onEvent(getApplicationContext(), "way_baidu");
                        break;
                    case 1:
                        selectEngine(ETranslateFrom.YOU_DAO);
                        MobclickAgent.onEvent(getApplicationContext(), "way_youdao");
                        break;
                    case 2:
                        selectEngine(ETranslateFrom.JIN_SHAN);
                        MobclickAgent.onEvent(getApplicationContext(), "way_jinshan");
                        break;
                    case 3:
                        selectEngine(ETranslateFrom.GOOGLE);
                        MobclickAgent.onEvent(getApplicationContext(), "way_google");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean checkInput(String input) {
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
            if (withEmptyPoint) {
                Toast.makeText(MainActivity.this, R.string.tip_input_words, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    private void translate() {
        Log.i(TAG, "execute translate");
        closeKeyboard();
        final String input = mInput.getText().toString().trim();
        if (checkInput(input)) {
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
            msg = getString(R.string.tip_fail_translate) + (BuildConfig.DEBUG ? "  " + e.getMessage() : "");
        } else if (e instanceof UnknownHostException) {
            msg = getString(R.string.tip_unknown_host) + (BuildConfig.DEBUG ? "  " + e.getMessage() : "");
        } else {
            msg = getString(R.string.tip_unknown) + (BuildConfig.DEBUG ? "  " + e.getMessage() : "");
            e.printStackTrace();
        }
        mList.addView(ViewUtil.getWordsView(MainActivity.this, msg, android.R.color.holo_red_light, false));
        mBtTranslate.setEnabled(true);
        mBtTranslate.setText(R.string.action_translate);
    }

    @Override
    public void addExplainItem(String explain) {
        mList.addView(ViewUtil.getWordsView(MainActivity.this, explain, R.color.color_explain, true));
    }

    @Override
    public void initTranslateEngineSetting(ETranslateFrom from) {
        mSpTranslateWay.setSelection(from.getIndex(), true);
    }

    @OnClick(R.id.bt_translate)
    public void onClickTranslate(View view) {
        MobclickAgent.onEvent(getApplicationContext(), "action_translate");
        translate();
    }

    @OnClick(R.id.tv_clear)
    public void onClickClear(View view) {
        MobclickAgent.onEvent(getApplicationContext(), "action_clear");
        resetView();
        InputMethodUtils.openSoftKeyboard(this, mInput);
    }

    @OnClick(R.id.tv_point)
    public void onClickInputPoint(View view) {
        MobclickAgent.onEvent(getApplicationContext(), "action_input_point");
        mInput.requestFocus();
    }

    private void resetView() {
        clearInputContent();
        mPresenter.clearClipboard();
        resetTranslateResultArea();
    }

    private void resetTranslateResultArea() {
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
        MobclickAgent.onEvent(getApplicationContext(), "favorite_main");
    }

    @OnClick(R.id.iv_paste)
    public void onClickPaste(View view) {
        closeKeyboard();
        Toast.makeText(MainActivity.this, "长按翻译结果可复制", Toast.LENGTH_SHORT).show();
        MobclickAgent.onEvent(getApplicationContext(), "action_paste");
    }

    @OnClick(R.id.iv_sound)
    public void onClickSound(View view) {
        Object obj = view.getTag();
        if (obj != null && obj instanceof Result) {
            Result entity = (Result) obj;
            String fileName = entity.getMp3FileName();
            String mp3Url = entity.getEnMp3();
            mPresenter.playSound(fileName, mp3Url);
        }
        mPresenter.startSoundAnim(view);
        MobclickAgent.onEvent(getApplicationContext(), "sound_main_activity");
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
    public void fillDayline(IDayLine entity) {
        mTvDayline.setText(entity.content());
        mTvDaylineNote.setText(entity.note());
        mIvSoundDayline.setTag(entity);
    }

    @Override
    public void attachLocalDic(List<String> dic) {
        ArrayAdapter<String> wordAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                dic);
        mInput.setAdapter(wordAdapter);
        mInput.setThreshold(1);
        mInput.setOnItemClickListener((parent, view, position, id) -> translate());
    }

    @OnClick(R.id.iv_sound_dayline)
    public void onClickDaylineSound(View view) {
        MobclickAgent.onEvent(getApplicationContext(), "sound_dayline_activity");
        Object obj = view.getTag();
        if (obj != null && obj instanceof IDayLine) {
            IDayLine entity = (IDayLine) obj;
            String fileName = JinShanResult.getFileName(entity.tts());
            String mp3Url = entity.tts();
            mPresenter.playSound(fileName, mp3Url);
        }
        mPresenter.startSoundAnim(view);
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

    public void onClickBottomSheet() {
        if (mBottomSheetBehavior == null) {
            return;
        }
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @OnClick(android.R.id.input)
    public void onClickInput(View view) {

    }

    /**
     * 检查bottom sheet 是否展开 如果是 折叠 返回 true
     *
     * @return
     */
    private boolean checkBottomSheetIsExpandedAndReset() {
        if (mBottomSheetBehavior == null) {
            return false;
        }
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!checkBottomSheetIsExpandedAndReset()) {
            super.onBackPressed();
        }
    }
}
