package name.gudong.translate.ui.activitys;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

import jonathanfinerty.once.Once;
import me.gudong.translate.R;
import name.gudong.translate.listener.ListenClipboardService;
import name.gudong.translate.mvp.model.type.EDurationTipTime;
import name.gudong.translate.util.SpUtils;
import name.gudong.translate.util.Utils;

public class SettingActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SettingsFragment())
                    .commit();
        }

        setTitle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setTitle(){
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("设置");
    }



    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private static final String KEY_TIP_OF_RECITE = "TIP_OF_RECITE";

        private com.jenzz.materialpreference.Preference mDurationPreference;
        private com.jenzz.materialpreference.SwitchPreference mShowIconInNotification;
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
            // 第一次点击单词本开关需要给用户一个功能提示框
            Once.toDo(KEY_TIP_OF_RECITE);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mDurationPreference = (com.jenzz.materialpreference.Preference) findPreference("preference_show_time");
            mShowIconInNotification = (com.jenzz.materialpreference.SwitchPreference) findPreference("preference_show_icon_in_notification");
            EDurationTipTime durationTime = SpUtils.getDurationTimeWay(getActivity());
            mDurationPreference.setSummary(getArrayValue(R.array.tip_time,durationTime.getIndex()));
            mDurationPreference.setOnPreferenceClickListener(this);

            mShowIconInNotification.setOnPreferenceChangeListener(this);
            findPreference("preference_use_recite_or_not").setOnPreferenceChangeListener(this);

            findPreference("preference_show_float_view_use_system").setEnabled(Utils.isSDKHigh5());
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey()){
                case "preference_show_time":
                    EDurationTipTime durationTime = SpUtils.getDurationTimeWay(getActivity());
                    final int resArray = R.array.tip_time;
                    new AlertDialog.Builder(getActivity())
                            .setSingleChoiceItems(R.array.tip_time,durationTime.getIndex(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            selectDurationTime(EDurationTipTime.ONE_SECOND.name());
                                            MobclickAgent.onEvent(getActivity(),"menu_duration_time_2");
                                            break;
                                        case 1:
                                            selectDurationTime(EDurationTipTime.FOUR_SECOND.name());
                                            MobclickAgent.onEvent(getActivity(),"menu_duration_time_4");
                                            break;
                                        case 2:
                                            selectDurationTime(EDurationTipTime.SIX_SECOND.name());
                                            MobclickAgent.onEvent(getActivity(),"menu_duration_time_6");
                                            break;
                                    }
                                    preference.setSummary(getArrayValue(resArray,which));
                                    dialog.dismiss();
                                }
                            })
                            .show();
                    break;
            }
            return false;
        }

        private String getArrayValue(int resArray,int index){
            return getActivity().getResources().getStringArray(resArray)[index];
        }

        private void selectDurationTime(String name) {
            SpUtils.setDurationTipTime(getActivity(), name);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            switch (preference.getKey()){
                case "preference_use_recite_or_not":
                    if (Once.needToDo(KEY_TIP_OF_RECITE)) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("提示")
                                .setMessage("定时提示生词，是咕咚翻译做的一个帮助用户记住生词的功能。\n\n开启定时单词提醒后，系统会每隔五分钟(时间可以设置)，随机弹出一个提示框，用于随机展示你收藏的生词，帮助你记住这些陌生单词。\n\n我相信再陌生的单词，如果可以不停的在你眼前出现，不一定那一次就记住了，当然这个功能是可以关闭的。\n\n灵感源于贝壳单词，感谢 @drakeet 同学的作品。")
                                .setPositiveButton("知道了", ((dialog, which) -> Once.markDone(KEY_TIP_OF_RECITE)))
                                .show();
                    }
                    startListenService();
                    break;
                case "preference_show_icon_in_notification":
                    if((Boolean) newValue){
                        Utils.showNormalNotification(getActivity());
                    }else{
                        Utils.cancelNotification(getActivity());
                    }
                    break;
            }

            return true;
        }

        private void startListenService() {
            ListenClipboardService.start(getActivity());
        }

    }

}
