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

package name.gudong.translate.util;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by GuDong on 1/21/16 15:52.
 * Contact with gudong.name@gmail.com.
 */
public class ViewUtil {
    /**
     * 设置输入框的光标到末尾
     */
    public static final void setEditTextSelectionToEnd(EditText editText) {
        Editable editable = editText.getEditableText();
        Selection.setSelection((Spannable) editable,
                editable.toString().length());
    }

    public static View getWordsView(Context context,String word,@ColorRes int color) {
        TextView tv = new TextView(context);
        tv.setTextColor(ContextCompat.getColor(context, color));
        tv.setTextSize(16);
        tv.setGravity(Gravity.LEFT);
        tv.setText(word);
        return tv;
    }
}
