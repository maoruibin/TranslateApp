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

package name.gudong.translate.injection.components;

import com.litesuits.orm.LiteOrm;

import javax.inject.Singleton;

import dagger.Component;
import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.ApiBaidu;
import name.gudong.translate.mvp.model.ApiGoogle;
import name.gudong.translate.mvp.model.ApiJinShan;
import name.gudong.translate.mvp.model.ApiYouDao;
import name.gudong.translate.mvp.model.SingleRequestService;
import name.gudong.translate.mvp.model.WarpAipService;
import name.gudong.translate.injection.modules.ApiServiceModel;
import name.gudong.translate.injection.modules.AppModule;

/**
 * Created by GuDong on 12/27/15 16:41.
 * Contact with gudong.name@gmail.com.
 *
 * Updated by Levine on 2/21/17 add google api
 */
@Singleton
@Component(modules = {AppModule.class,ApiServiceModel.class})
public interface AppComponent {

    GDApplication getApplication();

    LiteOrm getLiteOrm();

    WarpAipService getWarpService();

    ApiJinShan getApiJinShan();

    ApiYouDao getApiYoudao();

    ApiBaidu getApiBaidu();

    ApiGoogle getApiGoogle();

    SingleRequestService getDwnloadService();

}
