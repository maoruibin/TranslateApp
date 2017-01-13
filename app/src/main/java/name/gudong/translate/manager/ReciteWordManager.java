package name.gudong.translate.manager;

import name.gudong.translate.GDApplication;
import name.gudong.translate.mvp.model.type.EIntervalTipTime;
import rx.functions.Action1;

public class ReciteWordManager {
    private static final String KEY_TAG_COUNT_DOWN = "Countdown";
    private static final String KEY_TAG = "clipboard";
    private static ReciteModulePreference mRecitePreference;
    /**
     * 显示 Tip 的动作
     */
    private static Action1 mActionShowTip;


    private ReciteWordManager() {
        mRecitePreference = new ReciteModulePreference(GDApplication.mContext);
    }

    private static class SingletonInstance {
        private static final ReciteWordManager INSTANCE = new ReciteWordManager();
    }

    public static ReciteWordManager getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public boolean isReciteOpenOrNot(){
        return mRecitePreference.isReciteOpenOrNot();
    }
    public boolean isPlaySoundAuto(){
        return mRecitePreference.isPlaySoundAuto();
    }

    public EIntervalTipTime getIntervalTimeWay(){
        return mRecitePreference.getIntervalTimeWay();
    }
}