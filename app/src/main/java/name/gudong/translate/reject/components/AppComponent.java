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

package name.gudong.translate.reject.components;

import com.litesuits.orm.LiteOrm;

import javax.inject.Singleton;

import dagger.Component;
import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.reject.modules.ApiServiceModel;
import name.gudong.translate.reject.modules.AppModule;

/**
 * Created by GuDong on 12/27/15 16:41.
 * Contact with gudong.name@gmail.com.
 */
@Singleton
@Component(modules = {AppModule.class,ApiServiceModel.class})
public interface AppComponent {

    GDApplication getApplication();

    LiteOrm getLiteOrm();

    WarpAipService getWarpService();

}
