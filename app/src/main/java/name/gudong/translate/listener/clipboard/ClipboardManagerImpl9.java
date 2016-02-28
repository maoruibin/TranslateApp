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
import android.os.Handler;
import android.os.Looper;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;

public class ClipboardManagerImpl9 extends ClipboardManagerCompat implements Runnable {

    /**
     * It's better to check clipboard data for a static thread
     */
    private static Handler sHandler;

    static {
        sHandler = new Handler(Looper.getMainLooper());
    }

    ClipboardManager mClipboardManager;
    private CharSequence mLastData;
    private boolean mWorking = false;

    public ClipboardManagerImpl9(Context context) {
        mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void addPrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        super.addPrimaryClipChangedListener(listener);
        synchronized (mPrimaryClipChangedListeners) {
            if (mPrimaryClipChangedListeners.size() == 1) {
                startListen();
            }
        }
    }

    private void startListen() {
        mWorking = true;
        sHandler.postDelayed(this, 10000);
    }

    private void stopListen() {
        mWorking = false;
        sHandler.removeCallbacks(this);
    }

    @Override
    public void removePrimaryClipChangedListener(OnPrimaryClipChangedListener listener) {
        super.removePrimaryClipChangedListener(listener);
        synchronized (mPrimaryClipChangedListeners) {
            if (mPrimaryClipChangedListeners.size() == 0) {
                stopListen();
            }
        }
    }

    @Override
    public CharSequence getText() {
        return mClipboardManager.getText();
    }

    @Override
    public void run() {
        if (mWorking) {
            CharSequence data = getText();
            Log.d("uc-toast", "run: " + data);
            check(data);
            sHandler.postDelayed(this, 1000);
        }
    }

    private void check(CharSequence data) {
        if (TextUtils.isEmpty(mLastData) && TextUtils.isEmpty(data)) {
            return;
        }

        if (!TextUtils.isEmpty(mLastData) && mLastData.equals(data)) {
            return;
        }
        mLastData = data;
        notifyPrimaryClipChanged();
    }
}
