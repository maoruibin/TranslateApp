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

import android.app.Service;

import dagger.Module;
import dagger.Provides;
import name.gudong.translate.listener.clipboard.ClipboardManagerCompat;
import name.gudong.translate.listener.view.TipViewController;
import name.gudong.translate.reject.ActivityScope;

/**
 * Created by GuDong on 2/28/16 19:09.
 * Contact with gudong.name@gmail.com.
 */
@Module
public class ServiceModule {
    private Service mService;

    public ServiceModule(Service service) {
        mService = service;
    }

    @Provides
    public Service provideService(){
        return mService;
    }

    @Provides
    public ClipboardManagerCompat provideClipboardManage(){
        return ClipboardManagerCompat.create(mService );
    }

    @Provides
    @ActivityScope
    public TipViewController provideTipViewControl(){
        return new TipViewController(mService);
    }
}
