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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.type.ETranslateFrom;

/**
 * Created by GuDong on 1/22/16 18:41.
 * Contact with gudong.name@gmail.com.
 */
public class SpUtils {

    /**
     * 获取翻译网络引擎路径
     * @return
     */
    public static String getUrlByLocalSetting(){
        String translateWay =  SpUtils.getTranslateEngine(GDApplication.mContext);
        ETranslateFrom eTranslateFrom = ETranslateFrom.valueOf(translateWay);
        return eTranslateFrom.getUrl();
    }

    private static final String KEY_TRANSLATE_FROM = "TRANSLATE_FROM";
    private static final String KEY_WORDBOOK_RECITE_MODE = "WORDBOOK_RECITE_MODE";
    //every tip's interval time
    public static final String KEY_INTERVAL_TIP_TIME = "INTERVAL_TIP_TIME";


    //is open JIT translate or not
    private static final String KEY_OPEN_JIT = "preference_recite_open_jit";

    //is open JIT translate or not
    private static final String KEY_PREFERENCE_SHOW_ICON_IN_NOTIFICATION = "preference_show_icon_in_notification";

    //is notify dayline
    private static final String KEY_PREFERENCE_NOTIFY_DAYLINE = "preference_notify_dayline";

    //is notify dayline
    private static final String KEY_PREFERENCE_AUTO_PASTE = "preference_auto_paste_words";

    //App 是否在前台
    private static final String KEY_FLAG_APP_FRONT = "FLAG_LISTENER_CLIPBOARD";

    //是否授予 Android M 浮窗权限
    private static final String KEY_DRAW_OVERLAYS_PERMISSION = "DRAW_OVERLAYS_PERMISSION";

    //是否已经显示过用户引导
    private static final String KEY_HAS_SHOW_GUIDE = "HAS_SHOW_GUIDE";


    public static void setTranslateEngine(Context context, String version) {
        putStringPreference(context, KEY_TRANSLATE_FROM, version);
    }

    public static String getTranslateEngine(Context context) {
        return getStringPreference(context, KEY_TRANSLATE_FROM, ETranslateFrom.JIN_SHAN.name());
    }

    public static ETranslateFrom getTranslateEngineWay(Context context) {
        return ETranslateFrom.valueOf(getTranslateEngine(context));
    }

    //是否开启划词翻译
    public static void setOpenJITOrNot(Context context,boolean isOpen){
        putBooleanPreference(context,KEY_OPEN_JIT,isOpen);
    }

    public static boolean isShowIconInNotification(Context context){
        return getBooleanPreference(context,KEY_PREFERENCE_SHOW_ICON_IN_NOTIFICATION,false);
    }

    public static boolean isNotifyDayline(Context context){
        return getBooleanPreference(context,KEY_PREFERENCE_NOTIFY_DAYLINE,false);
    }

    public static boolean isAutoPasteWords(Context context){
        return getBooleanPreference(context,KEY_PREFERENCE_AUTO_PASTE,false);
    }

    public static boolean getOpenJITOrNot(Context context){
        return getBooleanPreference(context,KEY_OPEN_JIT,true);
    }

    //最前台是不是本应用 这个标志用来做判断 在当前应用的主界面长按是不做响应的
    public static void setAppFront(Context context,boolean isOpen){
        putBooleanPreference(context, KEY_FLAG_APP_FRONT,isOpen);
    }

    public static boolean getAppFront(Context context){
        return getBooleanPreference(context, KEY_FLAG_APP_FRONT,false);
    }

    //设置是否匹配权限
    public static void setDrawOverlays(Context context,boolean isOpen){
        putBooleanPreference(context, KEY_DRAW_OVERLAYS_PERMISSION,isOpen);
    }

    public static boolean hasGrantDrawOverlays(Context context){
        return getBooleanPreference(context, KEY_DRAW_OVERLAYS_PERMISSION,false);
    }

    public static boolean hasShowGuide(Context context){
        return getBooleanPreference(context, KEY_HAS_SHOW_GUIDE,false);
    }

    public static void setHasShowGuideFlag(Context context,boolean isShow){
        putBooleanPreference(context,KEY_HAS_SHOW_GUIDE,isShow);
    }

    public static void setWordBookReciteMode(Context context,boolean isReciteMode){
        putBooleanPreference(context,KEY_WORDBOOK_RECITE_MODE,isReciteMode);
    }

    public static boolean isWordBookReciteMode(Context context){
        return getBooleanPreference(context,KEY_WORDBOOK_RECITE_MODE,false);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////

    // -------------------    SharePreference Util Begin   -------------------  //

    public static void removeKey(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().remove(key).apply();
    }

    public static void putStringPreference(Context context, String key, String value) {
        getSharePreference(context).edit().putString(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String def) {
        return getSharePreference(context).getString(key, def);
    }

    public static void putIntPreference(Context context, String key, int value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(key, value).apply();
    }

    public static int getIntPreference(Context context, String key, int def) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, def);
    }

    public static void putBooleanPreference(Context context, String key, boolean value) {
        getSharePreference(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBooleanPreference(Context context, String key,boolean defValue) {
        return getSharePreference(context).getBoolean(key, defValue);
    }

    private static SharedPreferences getSharePreference(Context context){
        return context.getSharedPreferences(context.getPackageName()+"_preferences",Context.MODE_MULTI_PROCESS);
    }
}
