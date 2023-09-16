package net.chetch.mediacontroller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import net.chetch.appframework.ChetchApplication;
import net.chetch.mediacontroller.models.MediaControllerModel;
import net.chetch.utilities.SLog;
import net.chetch.webservices.network.NetworkRepository;

public class MCApplication extends ChetchApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        try{
            String apiBaseURL = sharedPref.getString("api_base_url", null);
            //String apiBaseURL = "http://192.168.3.188:8001/api";
            //String apiBaseURL = "http://192.168.1.103:8001/api";
            if(SLog.LOG)SLog.i("Application", " set api base url to " + apiBaseURL);
            NetworkRepository.getInstance().setAPIBaseURL(apiBaseURL);


            MediaControllerModel.VIBRATE = sharedPref.getBoolean("vibrate", true);
        } catch (Exception e){
            Log.e("ERApplication", e.getMessage());
        }
    }
}
