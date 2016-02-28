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

package name.gudong.translate.listener.clipboard;

import android.content.Context;
import android.os.Build;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public abstract class ClipboardManagerCompat {

    protected final ArrayList<OnPrimaryClipChangedListener> mPrimaryClipChangedListeners
            = new ArrayList<OnPrimaryClipChangedListener>();

    public static ClipboardManagerCompat create(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new ClipboardManagerImpl11(context);
        } else {
            return new ClipboardManagerImpl9(context);
        }
    }

    public void addPrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        synchronized (mPrimaryClipChangedListeners) {
            mPrimaryClipChangedListeners.add(listener);
        }
    }

    protected final void notifyPrimaryClipChanged() {
        Logger.i("----","Clip is Changed ");
        synchronized (mPrimaryClipChangedListeners) {
            for (int i = 0; i < mPrimaryClipChangedListeners.size(); i++) {
                mPrimaryClipChangedListeners.get(i).onPrimaryClipChanged();
            }
        }
    }

    public void removePrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        synchronized (mPrimaryClipChangedListeners) {
            mPrimaryClipChangedListeners.remove(listener);
        }
    }

    public abstract CharSequence getText();

    public interface OnPrimaryClipChangedListener {
        void onPrimaryClipChanged();
    }
}
