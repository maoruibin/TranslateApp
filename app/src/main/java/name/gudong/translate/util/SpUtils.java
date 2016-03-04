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
import name.gudong.translate.mvp.model.type.EDurationTipTime;
import name.gudong.translate.mvp.model.type.EIntervalTipTime;
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
    //every tip's interval time
    public static final String KEY_INTERVAL_TIP_TIME = "INTERVAL_TIP_TIME";
    //duration of tip
    private static final String KEY_DURATION_TIP_TIME = "DURATION_TIP_TIME";

    private static final String KEY_RECITE_OPEN = "RECITE_OPEN_OR_NOT";

    //is open JIT translate or not
    private static final String KEY_OPEN_JIT = "RECITE_OPEN_JIT";

    public static void setTranslateEngine(Context context, String version) {
        putStringPreference(context, KEY_TRANSLATE_FROM, version);
    }

    public static String getTranslateEngine(Context context) {
        return getStringPreference(context, KEY_TRANSLATE_FROM, ETranslateFrom.JIN_SHAN.name());
    }

    public static ETranslateFrom getTranslateEngineWay(Context context) {
        return ETranslateFrom.valueOf(getTranslateEngine(context));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setIntervalTipTime(Context context, String duration) {
        putStringPreference(context, KEY_INTERVAL_TIP_TIME,duration);
    }

    public static String getIntervalTipTime(Context context) {
        return getStringPreference(context, KEY_INTERVAL_TIP_TIME, EIntervalTipTime.FIVE_MINUTE.name());
    }

    public static EIntervalTipTime getIntervalTimeWay(Context context) {
        String name = getIntervalTipTime(context);
        return EIntervalTipTime.valueOf(name);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setDurationTipTime(Context context, String duration) {
        putStringPreference(context, KEY_DURATION_TIP_TIME,duration);
    }

    public static String getDurationTipTime(Context context) {
        return getStringPreference(context, KEY_DURATION_TIP_TIME, EDurationTipTime.FOUR_SECOND.name());
    }

    public static EDurationTipTime getDurationTimeWay(Context context) {
        return EDurationTipTime.valueOf(getDurationTipTime(context));
    }

    //是否开启背单词
    public static void setReciteOpenOrNot(Context context,boolean isOpen){
        putBooleanPreference(context,KEY_RECITE_OPEN,isOpen);
    }

    public static boolean getReciteOpenOrNot(Context context){
        return getBooleanPreference(context,KEY_RECITE_OPEN,false);
    }

    //是否开启划词翻译
    public static void setOpenJITOrNot(Context context,boolean isOpen){
        putBooleanPreference(context,KEY_OPEN_JIT,isOpen);
    }

    public static boolean getOpenJITOrNot(Context context){
        return getBooleanPreference(context,KEY_OPEN_JIT,true);
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
