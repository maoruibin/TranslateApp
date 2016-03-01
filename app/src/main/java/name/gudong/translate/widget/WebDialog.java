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

package name.gudong.translate.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.gudong.translate.R;

public class WebDialog extends DialogFragment {
    private static final String KEY_DIALOG_TITLE = "DIALOG_TITLE";
    private static final String KEY_HTML_FILE_NAME = "html_file_name";
    private static final String KEY_ACCENT_COLOR = "accentColor";

    public static void show(Context context, FragmentManager fragmentManager, String dialogTitle, String htmlFileName, String tag,@ColorRes int accentColor) {
        WebDialog.create(dialogTitle, htmlFileName, accentColor)
                .show(fragmentManager, "");
    }

    /**
     * create a custom dialog use web view load layout by html file
     *
     * @param dialogTitle  dialog title
     * @param htmlFileName html file name
     * @param accentColor  accent color
     * @return a instance of CustomWebViewDialog
     */
    public static WebDialog create(String dialogTitle, String htmlFileName, int accentColor) {
        WebDialog dialog = new WebDialog();
        Bundle args = new Bundle();
        args.putString(KEY_DIALOG_TITLE, dialogTitle);
        args.putString(KEY_HTML_FILE_NAME, htmlFileName);
        args.putInt(KEY_ACCENT_COLOR, accentColor);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View customView;
        try {
            customView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_about_dialog, null);
        } catch (InflateException e) {
            throw new IllegalStateException("This device does not support Web Views.");
        }

        String dialogTitle = getArguments().getString(KEY_DIALOG_TITLE);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setView(customView)
                .setPositiveButton(android.R.string.ok, null)
                .show();

        final WebView webView = (WebView) customView.findViewById(R.id.webview);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        try {
            String htmlFileName = getArguments().getString(KEY_HTML_FILE_NAME);
            StringBuilder buf = new StringBuilder();
            InputStream json = getActivity().getAssets().open(htmlFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null)
                buf.append(str);
            in.close();

            final int accentColor = getResources().getColor(getArguments().getInt(KEY_ACCENT_COLOR));
            String formatLodString = buf.toString()
                    .replace("{style-placeholder}", "body { background-color: #ffffff; color: #000; }")
                    .replace("{link-color}", colorToHex(shiftColor(accentColor, true)))
                    .replace("{link-color-active}", colorToHex(accentColor));
            webView.loadDataWithBaseURL(null, formatLodString, "text/html", "UTF-8", null);
        } catch (Throwable e) {
            webView.loadData("<h1>Unable to load</h1><p>" + e.getLocalizedMessage() + "</p>", "text/html", "UTF-8");
        }
        return dialog;
    }

    private String colorToHex(int color) {
        return Integer.toHexString(color).substring(2);
    }

    private int shiftColor(int color, boolean up) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= (up ? 1.1f : 0.9f); // value component
        return Color.HSVToColor(hsv);
    }
}
