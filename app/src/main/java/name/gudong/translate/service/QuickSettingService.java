package name.gudong.translate.service;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import name.gudong.translate.listener.ListenClipboardService;
import name.gudong.translate.manager.ReciteModulePreference;

@TargetApi(Build.VERSION_CODES.N)
public class QuickSettingService extends TileService {
    private static final String TAG = "QuickSettingService";
    ReciteModulePreference mRecitePreference;

    @Override
    public void onCreate() {
        super.onCreate();
        mRecitePreference = new ReciteModulePreference(getBaseContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(getQsTile() == null){
            return super.onStartCommand(intent, flags, startId);
        }
        if(mRecitePreference.isReciteOpenOrNot()){
            getQsTile().setState(Tile.STATE_ACTIVE);// 更改成非活跃状态
        }else{
            getQsTile().setState(Tile.STATE_INACTIVE);// 更改成非活跃状态
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onClick() {
        int state = getQsTile().getState();
        Log.d(TAG, "onClick state = " + Integer.toString(getQsTile().getState()));
        if (state == Tile.STATE_INACTIVE) {
            getQsTile().setState(Tile.STATE_ACTIVE);// 更改成活跃状态
            mRecitePreference.setReciteOpenOrNot(true);
        } else {
            getQsTile().setState(Tile.STATE_INACTIVE);//更改成非活跃状态
            mRecitePreference.setReciteOpenOrNot(false);
        }
        getQsTile().updateTile();//更新Tile
        shiftRecite();
    }

    private void shiftRecite() {
        ListenClipboardService.start(getBaseContext());
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.d(TAG, "onStartListening and status is " + Integer.toString(getQsTile().getState()));
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Log.d(TAG, "onStopListening and status is " + Integer.toString(getQsTile().getState()));
    }
}
