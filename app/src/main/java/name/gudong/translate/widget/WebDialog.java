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
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.gudong.translate.R;
import name.gudong.translate.util.DialogUtil;

public class WebDialog extends DialogFragment {
    private static final String KEY_UTF_8 = "UTF_8";
    private DialogInterface.OnClickListener mNeutralClickCallback;
    private DialogInterface.OnClickListener mPositiveClickCallback;
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
        args.putString("dialogTitle", dialogTitle);
        args.putString("htmlFileName", htmlFileName);
        args.putInt("accentColor", accentColor);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * create a CustomWebViewDialog with a neutral button
     * @param dialogTitle
     * @param htmlFileName
     * @param accentColor
     * @param neutralText neutral button text
     * @param neutralListener click listener
     * @return
     */
    public static WebDialog create(String dialogTitle, String htmlFileName, int accentColor, String positiveText, DialogInterface.OnClickListener positiveListener, String neutralText, DialogInterface.OnClickListener neutralListener) {
        WebDialog dialog = new WebDialog();
        Bundle args = new Bundle();
        args.putString("dialogTitle", dialogTitle);
        args.putString("htmlFileName", htmlFileName);
        args.putInt("accentColor", accentColor);

        args.putString("positiveText", positiveText);
        dialog.setPositiveClickCallback(positiveListener);

        args.putString("neutralText", neutralText);
        dialog.setNeutralClickCallback(neutralListener);

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

        String dialogTitle = getArguments().getString("dialogTitle");
        String neutralText = getArguments().getString("neutralText");
        String positiveText = getArguments().getString("positiveText");
        neutralText = TextUtils.isEmpty(neutralText)?"":neutralText;
        positiveText = TextUtils.isEmpty(neutralText)?getString(android.R.string.ok):positiveText;
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(dialogTitle)
                .setView(customView)
                .setNeutralButton(neutralText, mNeutralClickCallback)
                .setPositiveButton(positiveText, mPositiveClickCallback)
                .show();

        final WebView webView = (WebView) customView.findViewById(R.id.webview);
        setWebView(webView,customView.getContext());
        try {
            String htmlFileName = getArguments().getString("htmlFileName");
            StringBuilder buf = new StringBuilder();
            InputStream json = getActivity().getAssets().open(htmlFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null)
                buf.append(str);
            in.close();

            final int accentColor = getArguments().getInt("accentColor");
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

    private void setWebView(WebView webView,Context context){
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName(KEY_UTF_8);
        settings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(context),"Android");
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

    public void setNeutralClickCallback(DialogInterface.OnClickListener neutralClickCallback) {
        mNeutralClickCallback = neutralClickCallback;
    }

    public void setPositiveClickCallback(DialogInterface.OnClickListener positiveClickCallback) {
        mPositiveClickCallback = positiveClickCallback;
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a dialog about gank site **/
        @JavascriptInterface
        public void clickThanksWords(){
            MobclickAgent.onEvent(getActivity(), "link_click_donate");
            DialogUtil.showAboutDonate((AppCompatActivity) getActivity());
        }
    }
}
