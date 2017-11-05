package name.gudong.translate.manager;

import android.content.Context;

import com.orhanobut.logger.Logger;

import net.grandcentrix.tray.TrayPreferences;

import name.gudong.translate.mvp.model.type.EDurationTipTime;
import name.gudong.translate.mvp.model.type.EIntervalTipTime;

import static name.gudong.translate.util.SpUtils.KEY_INTERVAL_TIP_TIME;


/**
 * Created by GuDong on 08/12/2016 00:16.
 * Contact with gudong.name@gmail.com.
 */

public class ReciteModulePreference extends TrayPreferences{
    private static final String TAG = "ReciteModulePreference";
    private static final boolean DEFAULT_IS_RECITE = false;
    //is open JIT translate or not
    private static final String KEY_OPEN_JIT = "preference_recite_open_jit";

    // is open recite words
    private static final String KEY_RECITE_OPEN = "preference_use_recite_or_not";

    //duration of tip
    private static final String KEY_DURATION_TIP_TIME = "DURATION_TIP_TIME";

    //is play mp3 auto
    private static final String KEY_PREFERENCE_AUTO_PLAY_SOUND = "preference_auto_play_sound";


    /**
     * 当前背诵单词的 query
     */
    private static final String KEY_CURRENT_CYCLIC = "CURRENT_CYCLIC";


    public ReciteModulePreference(final Context context) {
        super(context, "recite", 1);
    }

    //是否开启背单词
    public boolean isReciteOpenOrNot(){
        return getBoolean(KEY_RECITE_OPEN, DEFAULT_IS_RECITE);
    }

    public void setReciteOpenOrNot(boolean isRecite){
        put(KEY_RECITE_OPEN,isRecite);
    }

    public void setIntervalTipTime(String duration) {
        put(KEY_INTERVAL_TIP_TIME,duration);
    }

    public EIntervalTipTime getIntervalTimeWay() {
        String name = getIntervalTipTime();
        return EIntervalTipTime.valueOf(name);
    }

    private String getIntervalTipTime() {
        return getString(KEY_INTERVAL_TIP_TIME, EIntervalTipTime.THREE_MINUTE.name());
    }

    public void setDurationTipTime(String duration) {
        put(KEY_DURATION_TIP_TIME,duration);
    }

    public String getDurationTipTime() {
        return getString(KEY_DURATION_TIP_TIME,EDurationTipTime.FOUR_SECOND.name());
    }

    public void setCurrentCyclicWord(String query){
        put(KEY_CURRENT_CYCLIC,query);
        Logger.t(TAG).i("put  "+query +" suc");
    }

    public String getCurrentCyclicWord(){
        Logger.t(TAG).i("get  "+getString(KEY_CURRENT_CYCLIC,"") +" suc");
        return getString(KEY_CURRENT_CYCLIC,"");
    }

    public EDurationTipTime getDurationTimeWay() {
        return EDurationTipTime.valueOf(getDurationTipTime());
    }

    public int getDurationTime() {
        return EDurationTipTime.valueOf(getDurationTipTime()).getDurationTime();
    }


    public boolean isPlaySoundAuto(){
        return getBoolean(KEY_PREFERENCE_AUTO_PLAY_SOUND,false);
    }

    public void setPlaySoundAuto(boolean isPlaySound){
        put(KEY_PREFERENCE_AUTO_PLAY_SOUND,isPlaySound);
    }
}
