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

package name.gudong.translate;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import im.fir.sdk.FIR;
import jonathanfinerty.once.Once;
import me.gudong.translate.BuildConfig;
import name.gudong.translate.reject.components.AppComponent;
import name.gudong.translate.reject.components.DaggerAppComponent;
import name.gudong.translate.reject.modules.ApiServiceModel;
import name.gudong.translate.reject.modules.AppModule;

/**
 * Created by GuDong on 12/27/15 16:46.
 * Contact with gudong.name@gmail.com.
 */
public class GDApplication extends Application {
    private static AppComponent mAppComponent;
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        setUpLog();
        FIR.init(this);
        Once.initialise(this);
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .apiServiceModel(new ApiServiceModel())
                .build();
    }

    private void setUpLog() {
        if(BuildConfig.DEBUG){
            Logger.init("gdt").hideThreadInfo().setMethodCount(0);
        }else{
            Logger.init("gdt").hideThreadInfo().setMethodCount(0).setLogLevel(LogLevel.FULL);
        }
    }

    public static GDApplication get(){
        return (GDApplication)mContext.getApplicationContext();
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }
}
