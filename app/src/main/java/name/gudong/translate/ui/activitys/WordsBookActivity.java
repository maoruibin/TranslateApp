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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.gudong.translate.R;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.mvp.presenters.BookPresenter;
import name.gudong.translate.mvp.views.IBookView;
import name.gudong.translate.reject.components.AppComponent;
import name.gudong.translate.reject.components.DaggerActivityComponent;
import name.gudong.translate.reject.modules.ActivityModule;
import name.gudong.translate.ui.adapter.WordsListAdapter;

public class WordsBookActivity extends BaseActivity<BookPresenter> implements WordsListAdapter.OnClick, IBookView {

    @Bind(R.id.rv_words_list)
    RecyclerView mRvWordsList;

    @Bind(R.id.empty_tip_text)
    TextView emptyTipText;

    WordsListAdapter mAdapter;

    public static void gotoWordsBook(Context context) {
        Intent intent = new Intent(context, WordsBookActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_book);
        ButterKnife.bind(this);
        initActionBar(true, "单词本");
        initListView();
        initData();
    }

    private void initData() {
        mPresenter.getWords();
    }

    private void initListView() {
        mAdapter = new WordsListAdapter(this);
        mAdapter.setOnClickListener(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRvWordsList.setLayoutManager(mLayoutManager);
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
    public void onClickItem(View view, Result entity) {
        mPresenter.deleteWords(entity);
    }

    @Override
    public void fillData(List<Result> transResultEntities) {
        //如果查出来的结果为空,那么提示用户没有收藏的单词
        if (transResultEntities == null || transResultEntities.size() == 0) {
            emptyTipText.setVisibility(View.VISIBLE);
        } else {
            emptyTipText.setVisibility(View.GONE);
            mAdapter.update(transResultEntities);
        }
    }

    @Override
    public void deleteWordSuccess(Result entity) {
        mAdapter.removeItem(entity);
        if (mAdapter.getItemCount() == 0) {
            emptyTipText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void deleteWordFail() {
        Toast.makeText(WordsBookActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable error) {
        Toast.makeText(WordsBookActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
