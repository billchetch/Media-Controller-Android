package net.chetch.mediacontroller;

import android.content.SharedPreferences;
import android.util.Log;

import net.chetch.appframework.SettingsActivityBase;
import net.chetch.mediacontroller.models.MediaControllerModel;
import net.chetch.utilities.SLog;
import net.chetch.webservices.network.NetworkRepository;

public class SettingsActivity extends SettingsActivityBase{
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key == null)return;

        if(key.equals("vibrate")) {
            MediaControllerModel.VIBRATE = sharedPreferences.getBoolean(key, true);
        } else if(key.equals("remote_device")){
            MediaControllerModel.REMOTE_BLUETOOTH_DEVICE = sharedPreferences.getString(key, null);
        }
    }
}
