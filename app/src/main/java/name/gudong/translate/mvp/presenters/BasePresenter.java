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

package name.gudong.translate.mvp.presenters;

import android.app.Activity;
import android.app.Service;

import com.litesuits.orm.LiteOrm;

import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.mvp.views.IBaseView;

/**
 * Created by GuDong on 2/27/16 23:19.
 * Contact with gudong.name@gmail.com.
 */
public class BasePresenter<V extends IBaseView> {
    protected V mView;
    protected Activity mActivity;
    protected Service mService;
    protected WarpAipService mWarpApiService;
    protected LiteOrm mLiteOrm;

    public BasePresenter(LiteOrm liteOrm, WarpAipService apiService,Activity activity) {
        mLiteOrm = liteOrm;
        mWarpApiService = apiService;
        mActivity = activity;
    }

    public BasePresenter(LiteOrm liteOrm, WarpAipService apiService,Service service) {
        mLiteOrm = liteOrm;
        mWarpApiService = apiService;
        mService = service;
    }

    public void onCreate(){}

    /**
     * attach IBaseView to Presenter
     * @param view view
     */
    public void attachView(V view){
        this.mView = view;
    }

    public void onDestroy(){
        this.mView = null;
    }
}
