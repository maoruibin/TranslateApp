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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.github.anzewei.parallaxbacklayout.ParallaxBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import name.gudong.translate.R;
import name.gudong.translate.injection.components.AppComponent;
import name.gudong.translate.injection.components.DaggerActivityComponent;
import name.gudong.translate.injection.modules.ActivityModule;
import name.gudong.translate.mvp.model.entity.translate.HistoryResult;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.mvp.presenters.BookPresenter;
import name.gudong.translate.mvp.presenters.MainPresenter;
import name.gudong.translate.mvp.views.IBookView;
import name.gudong.translate.ui.adapter.WordsListAdapter;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.Utils;
import name.gudong.translate.widget.DividerItemDecoration;

import static name.gudong.translate.util.SpUtils.isWordBookReciteMode;

@ParallaxBack
public class WordsBookActivity extends BaseActivity<BookPresenter> implements IBookView {
    private static final String KEY_HIST_LIST_FLAG = "isHistList";
    @BindView(R.id.rv_words_list)
    RecyclerView mRvWordsList;

    @BindView(R.id.empty_tip_text)
    TextView emptyTipText;

    private List<Result> mResult = new ArrayList<>();

    private boolean mIsHistList = false;

    WordsListAdapter mAdapter;

    public static void gotoWordsBook(Context context) {
        Intent intent = new Intent(context, WordsBookActivity.class);
        context.startActivity(intent);
    }

    public static void gotoWordsHist(Context context) {
        Intent intent = new Intent(context, WordsBookActivity.class);
        intent.putExtra(KEY_HIST_LIST_FLAG, true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_book);
        ButterKnife.bind(this);
        mIsHistList = getIntent().getBooleanExtra(KEY_HIST_LIST_FLAG, false);
        if(mIsHistList){
            emptyTipText.setText(R.string.empty_hist);
        }else{
            emptyTipText.setText(R.string.empty_tip);
        }
        initActionBar(true, mIsHistList ? "历史查询" : "单词本");
        initListView();
        initData();
        track();
    }

    private void track() {
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(getTitle().toString())
                .putContentType(getTitle().toString())
                .putContentId(mIsHistList?"histList":"bookList"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(mIsHistList ? R.menu.hist : R.menu.book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mIsHistList){
            if(mAdapter.getItemCount() > 0){
                menu.findItem(R.id.menu_clear_hist).setEnabled(true);
            }else{
                menu.findItem(R.id.menu_clear_hist).setEnabled(false);
            }
            return super.onPrepareOptionsMenu(menu);
        }
        if (mAdapter.getItemCount() <= 0) {
            menu.findItem(R.id.menu_sort).setVisible(false);
            menu.findItem(R.id.menu_recite_mode).setVisible(false);
        } else {
            menu.findItem(R.id.menu_sort).setVisible(true);
            menu.findItem(R.id.menu_recite_mode).setVisible(true);
        }
        if(mAdapter.getItemCount() > 0){
            menu.findItem(R.id.menu_backup).setEnabled(true);
        }else{
            menu.findItem(R.id.menu_backup).setEnabled(false);
        }
        menu.findItem(R.id.menu_recite_mode).setChecked(isWordBookReciteMode(this));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_recite_mode:
                if (!mPresenter.hasShowReciteModeIntroduce()) {
                    mPresenter.makeReciteDone();
                    new AlertDialog.Builder(this)
                            .setMessage("开启背单词模式后，单词本的单词列表将隐藏单词释义，点击才可以查看。")
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mRvWordsList.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            switchReciteMode(item);
                                        }
                                    }, 300);
                                }
                            })
                            .show();
                } else {
                    switchReciteMode(item);
                }
                break;
            case R.id.menu_export:
                String exportText = mPresenter.getWordsJsonString(mAdapter.getData());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_export)
                        .setMessage(exportText)
                        .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.shareText(WordsBookActivity.this, exportText);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .setNeutralButton("复制", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.copyText(exportText);
                                Toast.makeText(WordsBookActivity.this, "拷贝成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
            case R.id.menu_import:
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.module_lite_input, null);
                EditText editText = (EditText) view.findViewById(R.id.et_import);
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String text = editText.getText().toString();
                                if (TextUtils.isEmpty(text)) {
                                    Toast.makeText(WordsBookActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (!Utils.isJSONFormat(text)) {
                                    Toast.makeText(WordsBookActivity.this, "不是 JSON 格式，请检查。", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                mPresenter.restoreWordsByText(mAdapter.getData(), text);
                            }
                        })
                        .show();
                break;
            case R.id.sort_index_asc:
                item.setChecked(true);
                Collections.sort(mResult, new Comparator<Result>() {
                    @Override
                    public int compare(Result lhs, Result rhs) {
                        return lhs.getQuery().compareToIgnoreCase(rhs.getQuery());
                    }
                });
                mAdapter.update(mResult);
                break;
            case R.id.sort_default:
                item.setChecked(true);
                mPresenter.getWords();
                break;
            case R.id.sort_index_desc:
                item.setChecked(true);
                Collections.sort(mResult, new Comparator<Result>() {
                    @Override
                    public int compare(Result lhs, Result rhs) {
                        return -lhs.getQuery().compareToIgnoreCase(rhs.getQuery());
                    }
                });
                mAdapter.update(mResult);
                break;
            case R.id.menu_clear_hist:
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要清空所有的历史查询记录吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPresenter.clearHist();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchReciteMode(MenuItem item) {
        item.setChecked(!item.isChecked());
        mAdapter.updateReciteMode(item.isChecked());
        SpUtils.setWordBookReciteMode(this, item.isChecked());
    }

    private void initData() {
        mPresenter.initStatus(mIsHistList);
        mPresenter.getWords(mIsHistList);
    }

    private void initListView() {
        mAdapter = new WordsListAdapter(this);
        mAdapter.setPresenter(mPresenter);
        mAdapter.setOnClickListener(new WordsListAdapter.IClickPopupMenuItem() {
            @Override
            public void onClickMenuItem(int itemId, Result entity) {
                switch (itemId) {
                    case R.id.pop_delete:
                        new AlertDialog.Builder(WordsBookActivity.this)
                                .setMessage("确定要删除吗？")
                                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Result temp = entity;
                                        if(mIsHistList){
                                            temp = HistoryResult.toResult(entity);
                                        }
                                        mPresenter.deleteWords(temp);
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                        break;
                    case R.id.pop_research:
                        MainPresenter.jumpMainActivityFromClickTipView(WordsBookActivity.this, entity);
                        finish();
                        break;
                }

            }
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRvWordsList.setLayoutManager(mLayoutManager);
        mRvWordsList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRvWordsList.setAdapter(mAdapter);
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent, ActivityModule activityModule) {
        DaggerActivityComponent.builder()
                .activityModule(activityModule)
                .appComponent(appComponent)
                .build()
                .inject(this);
    }


    @Override
    public void fillData(List<Result> transResultEntities, boolean isReciteMode) {
        if (transResultEntities == null) {
            return;
        }
        //如果查出来的结果为空,那么提示用户没有收藏的单词
        if (transResultEntities.size() == 0) {
            emptyTipText.setVisibility(View.VISIBLE);
        } else {
            emptyTipText.setVisibility(View.GONE);
            mAdapter.update(transResultEntities, isReciteMode);
            mResult = transResultEntities;
        }
        if(!mIsHistList){
            //提示开启背单词开关
            mPresenter.checkPointRecite(transResultEntities.size());
        }
    }

    @Override
    public void deleteWordSuccess(Result entity) {
        mAdapter.removeItem(entity);
        showTip("删除成功");
        if (mAdapter.getItemCount() == 0) {
            emptyTipText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void deleteWordFail() {
        showTip("删除失败");
    }

    @Override
    public void onError(Throwable error) {
        showTip(error.getMessage());
    }

    @Override
    public void restoreSuccess(int count) {
        mPresenter.getWords();
        showTip("成功恢复 " + count + " 个单词。");
    }

    @Override
    public void showTipDataHaveNoChange() {
        showTip("数据没有任何变化");
    }

    @Override
    public void showEmptyList() {
        mAdapter.clear();
        emptyTipText.setVisibility(View.VISIBLE);
        showTip("清除成功");
    }

    /***
     * show delete operation text
     *
     * @param showText
     */
    private void showTip(String showText) {
        Toast.makeText(WordsBookActivity.this, showText, Toast.LENGTH_SHORT).show();
    }
}
