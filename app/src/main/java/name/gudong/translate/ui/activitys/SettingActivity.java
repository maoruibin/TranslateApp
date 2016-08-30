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

import me.gudong.translate.R;
import name.gudong.translate.mvp.model.type.EDurationTipTime;
import name.gudong.translate.util.SpUtils;

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


    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        private com.jenzz.materialpreference.Preference mDurationPreference;
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mDurationPreference = (com.jenzz.materialpreference.Preference) findPreference("preference_show_time");
            EDurationTipTime durationTime = SpUtils.getDurationTimeWay(getActivity());
            mDurationPreference.setSummary(getArrayValue(R.array.tip_time,durationTime.getIndex()));
            mDurationPreference.setOnPreferenceClickListener(this);
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
    }

}
