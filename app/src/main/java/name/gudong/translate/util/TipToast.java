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

package name.gudong.translate.util;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;

import me.gudong.translate.R;
import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.entity.Result;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by GuDong on 2/22/16 13:58.
 * Contact with gudong.name@gmail.com.
 */
public class TipToast {
    static TextView mTvSrc;
    static LinearLayout mLlDst;
    static Button mButtonFavorite;
    public static void showWord(Result result){
        View view = LayoutInflater.from(GDApplication.mContext).inflate(R.layout.pop_view,null);
        // display content
        mTvSrc = (TextView) view.findViewById(R.id.tv_pop_src);
        mLlDst = (LinearLayout) view.findViewById(R.id.ll_pop_dst);
        mButtonFavorite = (Button) view.findViewById(R.id.bt_action);
        mButtonFavorite.setVisibility(View.GONE);

        mTvSrc.setText(result.getQuery());
        setResultContent(result);

        Toast toast = new Toast(GDApplication.mContext);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,100);
        toast.setView(view);
        toast.show();
    }

    private static void setResultContent(Result result) {
        mButtonFavorite.setTag(result);
        List<String> temp = result.getExplains();
        if(temp.isEmpty()){
            temp = result.getTranslation();
        }

        Observable.from(temp)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.i(s);
                        addExplain(s);
                    }
                });
    }

    private static void addExplain(String explains){
        mLlDst.addView(ViewUtil.getWordsView(GDApplication.mContext,explains,android.R.color.white));
    }

}
