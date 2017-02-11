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

package name.gudong.translate.ui.adapter;

import android.content.Context;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import name.gudong.translate.R;
import name.gudong.translate.mvp.model.entity.translate.Result;
import name.gudong.translate.util.ViewUtil;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

import static name.gudong.translate.R.id.ll_pop_dst;

/**
 * Created by GuDong on 12/29/15 18:29.
 * Contact with gudong.name@gmail.com.
 */
public class WordsListAdapter extends RecyclerView.Adapter<WordsListAdapter.ViewHolder> {
    private Context mContext;
    private List<Result> mList;

    private IClickPopupMenuItem mOnClickListener;
    private boolean isReciteMode = false;

    public WordsListAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setOnClickListener(IClickPopupMenuItem onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void update(List<Result> list) {
        update(list, false);
    }

    public void update(List<Result> list, boolean isReciteMode) {
        this.mList = list;
        this.isReciteMode = isReciteMode;
        notifyDataSetChanged();
    }

    public void removeItem(Result entity) {
        mList.remove(entity);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Result entity = mList.get(position);
        holder.tvSrc.setText(entity.getQuery());
        if (!TextUtils.isEmpty(entity.getPhAm())) {
            holder.tvPhonetic.setText("[" + entity.getPhAm() + "]");
        }
        Observable.from(entity.getExplains())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        holder.llDst.removeAllViews();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        holder.llDst.addView(ViewUtil.getWordsView(mContext, s, R.color.gray_deep, false));
                    }
                });

        holder.ivMore.setOnClickListener(v -> {
            showPopMenu(entity, v);
        });

        holder.llDst.setVisibility(isReciteMode ? View.GONE : View.VISIBLE);

        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.llDst.isShown()) {
                    holder.llDst.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * @param isReciteMode 是不是背单词模式
     */
    public void updateReciteMode(boolean isReciteMode) {
        this.isReciteMode = isReciteMode;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRootView;
        private TextView tvSrc;
        private TextView tvPhonetic;
        private LinearLayout llDst;
        private ImageView ivMore;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = ButterKnife.findById(itemView, R.id.item_word_view);
            llDst = ButterKnife.findById(itemView, ll_pop_dst);
            tvPhonetic = ButterKnife.findById(itemView, R.id.tv_pop_phonetic);
            tvSrc = ButterKnife.findById(itemView, R.id.tv_pop_src);
            ivMore = ButterKnife.findById(itemView, R.id.iv_over_flow);
        }
    }

    public interface IClickPopupMenuItem {
        void onClickMenuItem(int itemId, Result entity);
    }

    public List<Result> getData() {
        return mList;
    }


    /**
     * 显示弹出式菜单
     *
     * @param entity
     * @param ancho
     */
    private void showPopMenu(final Result entity, View ancho) {
        PopupMenu popupMenu = new PopupMenu(mContext, ancho);
        popupMenu.getMenuInflater().inflate(R.menu.item_pop_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClickMenuItem(item.getItemId(), entity);
                }
                return false;
            }
        });

        makePopForceShowIcon(popupMenu);
        popupMenu.show();
    }

    //使用反射让popupMenu 显示菜单icon
    private void makePopForceShowIcon(PopupMenu popupMenu) {
        try {
            Field mFieldPopup = popupMenu.getClass().getDeclaredField("mPopup");
            mFieldPopup.setAccessible(true);
            MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(popupMenu);
            mPopup.setForceShowIcon(true);
        } catch (Exception e) {

        }
    }

}
