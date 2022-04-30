package net.chetch.mediacontroller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.os.Bundle;

import net.chetch.appframework.GenericActivity;


public class MainActivity extends GenericActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}