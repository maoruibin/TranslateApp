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

package name.gudong.translate.injection.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import name.gudong.translate.listener.clipboard.ClipboardManagerCompat;
import name.gudong.translate.listener.view.TipViewController;
import name.gudong.translate.injection.ActivityScope;

@Module
public class ActivityModule {
    private Context mContext;

    public ActivityModule(Context context) {
        mContext = context;
    }

    @Provides
    @ActivityScope
    public Context provideContext(){
        return mContext;
    }

    @Provides
    public ClipboardManagerCompat provideClipboardManage(){
        return ClipboardManagerCompat.create(mContext);
    }

    @Provides
    @ActivityScope
    public TipViewController provideTipViewControl(){
        return new TipViewController(mContext);
    }
}
