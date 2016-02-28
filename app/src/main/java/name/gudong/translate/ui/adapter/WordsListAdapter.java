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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import me.gudong.translate.R;
import name.gudong.translate.mvp.model.entity.Result;
import name.gudong.translate.util.ViewUtil;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by GuDong on 12/29/15 18:29.
 * Contact with gudong.name@gmail.com.
 */
public class WordsListAdapter extends RecyclerView.Adapter<WordsListAdapter.ViewHolder> {
    private Context mContext;
    private List<Result>mList;

    private OnClick mOnClickListener;

    public WordsListAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setOnClickListener(OnClick onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void update(List<Result> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void removeItem(Result entity){
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
                        holder.llDst.addView(ViewUtil.getWordsView(mContext,s,R.color.gray_deep));
                    }
                });
        holder.btAction.setText("删除");
        holder.btAction.setOnClickListener(v->{
            if(mOnClickListener!=null){
                mOnClickListener.onClickItem(v,entity);
            }
        });

        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout mRootView;
        private TextView tvSrc;
        private LinearLayout llDst;
        private Button btAction;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = (RelativeLayout) itemView;
            llDst = ButterKnife.findById(itemView,R.id.ll_pop_dst);
            tvSrc = ButterKnife.findById(itemView,R.id.tv_pop_src);
            btAction = ButterKnife.findById(itemView,R.id.bt_action);
        }
    }

    public interface OnClick{
        void onClickItem(View view,Result entity);
    }
}
