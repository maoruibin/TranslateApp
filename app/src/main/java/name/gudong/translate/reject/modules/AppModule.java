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

package name.gudong.translate.reject.modules;

import com.litesuits.orm.LiteOrm;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import name.gudong.translate.GDApplication;

@Module
public class AppModule {
    private static final String DB_NAME = "GdTranslate.db";
    private GDApplication application;

    public AppModule(GDApplication application){
        this.application=application;
    }

    @Provides
    @Singleton
    public GDApplication provideApplication(){
        return application;
    }

    @Provides
    @Singleton
    public LiteOrm provideLiteOrm(){
        return LiteOrm.newSingleInstance(application, DB_NAME);
    }
}
