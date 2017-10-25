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
import android.support.v7.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import jonathanfinerty.once.Once;
import me.drakeet.library.CrashWoodpecker;
import me.drakeet.library.PatchMode;
import name.gudong.translate.injection.components.AppComponent;
import name.gudong.translate.injection.components.DaggerAppComponent;
import name.gudong.translate.injection.modules.ApiServiceModel;
import name.gudong.translate.injection.modules.AppModule;

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
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        mContext = this;
        setUpSomethingsByDevMode(BuildConfig.IS_DEBUG);
        Once.initialise(this);
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .apiServiceModel(new ApiServiceModel())
                .build();
        initCrashWoodpecker();

        Stetho.initializeWithDefaults(this);
    }

    private void initCrashWoodpecker() {
        CrashWoodpecker.instance()
                .withKeys("widget", "name.gudong")
                .setPatchMode(PatchMode.SHOW_LOG_PAGE)
                    .setPatchDialogUrlToOpen("http://gudong.name")
                .setPassToOriginalDefaultHandler(true)
                .flyTo(this);
    }

    private void setUpSomethingsByDevMode(boolean isDebug) {
        if(isDebug){
            Logger.init("gdt").hideThreadInfo().methodCount(1).logLevel(LogLevel.FULL);
        }else{
            Logger.init("gdt").hideThreadInfo().methodCount(1).logLevel(LogLevel.FULL);
        }
    }

    public static GDApplication get(){
        return (GDApplication)mContext.getApplicationContext();
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }
}
