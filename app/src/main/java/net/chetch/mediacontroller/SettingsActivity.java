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
        if(key.equals("api_base_url")){
            restartMainActivityOnFinish = true;
            try{
                String apiBaseURL = sharedPreferences.getString(key, null);
                NetworkRepository.getInstance().setAPIBaseURL(apiBaseURL);
            } catch (Exception e){
                if(SLog.LOG)SLog.e("Settings", e.getMessage());
            }
        } else if(key.equals("vibrate"))
        {
            MediaControllerModel.VIBRATE = sharedPreferences.getBoolean(key, true);
        }
    }
}
