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

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import jonathanfinerty.once.Once;
import me.gudong.translate.R;
import name.gudong.translate.mvp.model.entity.AbsResult;
import name.gudong.translate.mvp.model.type.EDurationTipTime;
import name.gudong.translate.mvp.model.type.EIntervalTipTime;
import name.gudong.translate.mvp.model.type.ETranslateFrom;
import name.gudong.translate.mvp.presenters.MainPresenter;
import name.gudong.translate.mvp.views.IMainView;
import name.gudong.translate.reject.components.AppComponent;
import name.gudong.translate.reject.components.DaggerActivityComponent;
import name.gudong.translate.reject.modules.ActivityModule;
import name.gudong.translate.util.DialogUtil;
import name.gudong.translate.util.InputMethodUtils;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.ViewUtil;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView {

    private static final String KEY_TIP_OF_RECITE = "TIP_OF_RECITE";

    @Bind(android.R.id.input)
    AppCompatEditText mInput;
    @Bind(R.id.list_result)
    LinearLayout mList;
    @Bind(R.id.tv)
    TextView mTv;

    TextView mTvResultEngineInfo;
    ImageView mIvFavorite;

    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        addListener();
        startListenService();
        checkSomething();
        checkVersion();
        initConfig();
    }

    private void initConfig() {
        // 第一次点击单词本开关需要给用户一个功能提示框
        Once.toDo(KEY_TIP_OF_RECITE);
    }

    private void checkVersion() {
        mPresenter.checkVersionAndShowChangeLog();
    }

    private void startListenService() {
        mPresenter.startListenClipboardService();
    }

    private void checkSomething(){
        //检查粘贴板有没有英文单词 如果有就查询一次 并且显示给用户
        mPresenter.checkClipboard();
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
            case R.id.menu_book:
                WordsBookActivity.gotoWordsBook(this);
                break;
            case R.id.menu_about:
                DialogUtil.showAbout(this);
                InputMethodUtils.closeSoftKeyboard(this);
                break;
            case R.id.menu_score:
                mPresenter.gotoMarket();
                break;
            case R.id.translate_baidu:
                selectEngine(item, ETranslateFrom.BAI_DU);
                break;
            case R.id.translate_jinshan:
                selectEngine(item, ETranslateFrom.JIN_SHAN);
                break;
            case R.id.translate_youdao:
                selectEngine(item,ETranslateFrom.YOU_DAO);
                break;

            case R.id.menu_open_jit_or_nor:
                boolean isOpenJit = item.isChecked();
                SpUtils.setOpenJITOrNot(this,!isOpenJit);
                break;

            case R.id.menu_use_recite_or_not:
                if (Once.needToDo(KEY_TIP_OF_RECITE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("提示")
                            .setMessage("定时提示生词，是咕咚翻译做的一个帮助用户记住生词的功能。\n\n开启定时单词提醒后，系统会每隔五分钟(时间可以设置)，随机弹出一个提示框，用于随机展示你收藏的生词，帮助你记住这些陌生单词。\n\n我相信再陌生的单词，如果可以不停的在你眼前出现，不一定那一次就记住了，当然这个功能是可以关闭的。\n\n灵感源于贝壳单词，感谢 @drakeet 同学的作品。")
                            .setPositiveButton("知道了", ((dialog, which) ->  Once.markDone(KEY_TIP_OF_RECITE)))
                            .show();
                }
                boolean isCheck = item.isChecked();
                SpUtils.setReciteOpenOrNot(this,!isCheck);
                mMenu.findItem(R.id.menu_interval_tip_time).setVisible(!isCheck);
                startListenService();
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

            case R.id.duration_one_second:
                selectDurationTime(item, EDurationTipTime.ONE_SECOND.name());
                break;
            case R.id.duration_four_second:
                selectDurationTime(item, EDurationTipTime.FOUR_SECOND.name());
                break;
            case R.id.duration_six_second:
                selectDurationTime(item, EDurationTipTime.SIX_SECOND.name());
                break;
            case R.id.duration_ten_second:
                selectDurationTime(item, EDurationTipTime.TEN_SECOND.name());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectDurationTime(MenuItem item, String name) {
        SpUtils.setDurationTipTime(this, name);
        item.setChecked(true);
    }

    private void selectIntervalTime(MenuItem item, String name) {
        SpUtils.setIntervalTipTime(this, name);
        item.setChecked(true);
        startListenService();
    }

    private void selectEngine(MenuItem item, ETranslateFrom way) {
        SpUtils.setTranslateEngine(this, way.name());
        item.setChecked(true);
        shiftEnginePoint(way);
        checkInputAndResearch();
    }

    /**
     * 检查输入框是不是已经有输入的内容 如果有自动搜索，
     * 主要是切换搜索引擎时会用到
     */
    private void checkInputAndResearch(){
        String inputString = mInput.getText().toString();
        if(!inputString.isEmpty()){
            mPresenter.executeSearch(inputString);
        }
    }

    private void shiftEnginePoint(ETranslateFrom eTranslateFrom) {
        String msg = "已切换至 " + eTranslateFrom.getName() + " 翻译引擎";
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void addListener() {
        mInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == 6) {
                final String input = mInput.getText().toString().trim();
                if (!TextUtils.isEmpty(input)) {
                    mPresenter.executeSearch(input);
                    return false;
                } else {
                    Toast.makeText(MainActivity.this, "please input words !", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        });
    }


    private boolean isFavorite;

    /**
     * 生成翻译结果最下面的 view , 用于显示当前翻译结果对应的翻译引擎以及收藏按钮
     * @param result 翻译结果
     * @return 对应的 view
     */
    private View getResultBottomView(AbsResult result) {
        if (result == null) return null;
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.translate_result_bottom, null);
        mIvFavorite = ButterKnife.findById(view, R.id.iv_favorite);
        mTvResultEngineInfo = ButterKnife.findById(view, R.id.tv_result_engine_info);

        mIvFavorite.setTag(result);
        mIvFavorite.setOnClickListener(v -> onClickFavorite(v));
        mTvResultEngineInfo.setText("结果来自 " + SpUtils.getTranslateEngineWay(MainActivity.this).getName() + "翻译");

        if (mPresenter.isFavorite(result.wrapQuery())) {
            mIvFavorite.setImageResource(R.drawable.ic_favorite_pink_24dp);
            isFavorite = true;
        } else {
            mIvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            isFavorite = false;
        }
        return view;
    }

    public void onClickFavorite(View v) {
        AbsResult entity = (AbsResult) v.getTag();
        if (isFavorite) {
            mPresenter.unFavoriteWord(entity.getResult());
            Toast.makeText(MainActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
            mIvFavorite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            isFavorite = false;
        } else {
            mPresenter.favoriteWord(entity.getResult());
            Toast.makeText(MainActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
            mIvFavorite.setImageResource(R.drawable.ic_favorite_pink_24dp);
            isFavorite = true;
        }
    }
    /**
     * set text to EditText view and move curse to last
     * @param text which need to translate
     */
    @Override
    public void onInitSearchText(String text){
        mInput.setText(text);
        ViewUtil.setEditTextSelectionToEnd(mInput);
    }

    @Override
    public void onPrepareTranslate() {
        mList.removeAllViews();
        mList.addView(ViewUtil.getWordsView(MainActivity.this, "正在翻译...", R.color.gray));
    }

    @Override
    public void onClearResultViews() {
        mList.removeAllViews();
    }

    @Override
    public void appendBottomView(AbsResult result) {
        View resultBottomView = getResultBottomView(result);
        if (resultBottomView != null) {
            mList.addView(resultBottomView);
        }
    }

    @Override
    public void onError(Throwable e) {
        mList.addView(ViewUtil.getWordsView(MainActivity.this, e.getMessage(), android.R.color.holo_red_light));
    }

    @Override
    public void addExplainItem(String explain) {
        mList.addView(ViewUtil.getWordsView(MainActivity.this, explain, android.R.color.black));
    }

    @Override
    public void initTranslateEngineSetting(Menu menu,ETranslateFrom from) {
        switch (from) {
            case BAI_DU:
                menu.findItem(R.id.translate_baidu).setChecked(true);
                break;
            case JIN_SHAN:
                menu.findItem(R.id.translate_jinshan).setChecked(true);
                break;
            case YOU_DAO:
                menu.findItem(R.id.translate_youdao).setChecked(true);
                break;
        }
    }

    @Override
    public void initDurationTimeSetting(Menu menu,EDurationTipTime durationTime) {
        switch (durationTime) {
            case ONE_SECOND:
                menu.findItem(R.id.duration_one_second).setChecked(true);
                break;
            case FOUR_SECOND:
                menu.findItem(R.id.duration_four_second).setChecked(true);
                break;
            case SIX_SECOND:
                menu.findItem(R.id.duration_six_second).setChecked(true);
                break;
            case TEN_SECOND:
                menu.findItem(R.id.duration_ten_second).setChecked(true);
                break;
        }
    }

    @Override
    public void initIntervalTimeSetting(Menu menu,EIntervalTipTime intervalTime) {
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

    @Override
    public void initReciteSetting(Menu menu, boolean isOpen) {
        menu.findItem(R.id.menu_use_recite_or_not).setChecked(isOpen);
        menu.findItem(R.id.menu_interval_tip_time).setVisible(isOpen);


        menu.findItem(R.id.menu_use_recite_or_not).setVisible(false);
        menu.findItem(R.id.menu_interval_tip_time).setVisible(false);
        SpUtils.setReciteOpenOrNot(this,false);
    }

    @Override
    public void initJITSetting(Menu menu, boolean isOpen) {
        menu.findItem(R.id.menu_open_jit_or_nor).setChecked(isOpen);
    }
}
