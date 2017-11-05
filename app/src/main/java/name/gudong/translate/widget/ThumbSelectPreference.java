package name.gudong.translate.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import name.gudong.translate.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * author  : ruibin1 (ruibin1@staff.weibo.com)
 * create  : 2017/10/25 - 下午11:12.
 */

public class ThumbSelectPreference extends com.jenzz.materialpreference.Preference {
    AppCompatSeekBar seekBar;
    TextView titleView;

    public ThumbSelectPreference(Context context) {
        super(context);
    }

    public ThumbSelectPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThumbSelectPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @SuppressLint("MissingSuperCall")
    @Override
    protected View onCreateView(ViewGroup parent) {
        LayoutInflater layoutInflater =
                (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.pb_thumb, parent, false);
        return layout;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onBindView(View view) {
        CharSequence title = getTitle();
        titleView = view.findViewById(com.jenzz.materialpreference.R.id.title);
        titleView.setVisibility(!isEmpty(title) ? VISIBLE : GONE);

        seekBar = view.findViewById(R.id.seek_time);
        seekBar.setMax(10);

        int value = getPersistedInt(1);
        titleView.setText("提示显示时间(" + value + "秒)");
        seekBar.setProgress(value);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                persistInt(progress);
                notifyChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public AppCompatSeekBar getSeekbar() {
        return seekBar;
    }
}
