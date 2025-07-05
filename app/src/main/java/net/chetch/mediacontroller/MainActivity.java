package net.chetch.mediacontroller;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import net.chetch.appframework.GenericActivity;
import net.chetch.appframework.IDialogManager;
import net.chetch.appframework.NotificationBar;
import net.chetch.bluetooth.BluetoothViewModel;
import net.chetch.mediacontroller.models.MediaControllerModel;
import net.chetch.utilities.Logger;
import net.chetch.utilities.SLog;
import net.chetch.webservices.ConnectManager;


public class MainActivity extends GenericActivity implements IDialogManager {

    public static void Vibrate(Context ctx, int ms){
        Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for x milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(ms);
        }
    }

    final static String LOG_TAG = "Main";

    MediaControllerModel model;

    SoundManagerDialogFragment soundManagerDialog;
    int soundManagerCheckedID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SLog.LOG) SLog.i(LOG_TAG, "Create Main activity");

        includeActionBar(SettingsActivity.class);

        //connect
        try{
            model = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MediaControllerModel.class);
            model.getError().observe(this, this::showError);

            model.getConnecdted().observe(this, connected -> {
                View mainLayout = findViewById(R.id.mainLayout);
                if(connected){
                    hideProgress();
                    mainLayout.setVisibility(View.VISIBLE);
                } else {
                    mainLayout.setVisibility(View.INVISIBLE);
                    showProgress();
                }
            });

            model.getReceived().observe(this, msg ->{
                //TODO
            });

            if(!model.isConnected()) {
                model.init(this);
                model.connect();
                showProgress();
            }
        } catch (Exception e){
            this.showError(e.getMessage());
        }
    }

    public void onClickMediaPlayerButton(View view){
        ImageButton btn = (ImageButton)view;

        if(SLog.LOG)SLog.i(LOG_TAG, "Media player button clicked " + btn.getTag());
        String cmd = btn.getTag().toString();
        cmd = cmd.toUpperCase();
        sendPlayerShortcut(cmd, MediaControllerModel.VIBRATE);
    }

    private void sendPlayerShortcut(String shortcut, boolean vibrate){
        boolean sent = model.sendPlayerShortcut(shortcut);
        if(sent && vibrate){
            Vibrate(this, 150);
        }
    }

    public void openSoundManager(View view) {
        if (soundManagerDialog != null) {
            soundManagerDialog.dismiss();
        }
        soundManagerDialog = new SoundManagerDialogFragment();
        soundManagerDialog.model = model;
        soundManagerDialog.checkedID = soundManagerCheckedID;
        soundManagerDialog.show(getSupportFragmentManager(), "SoundManagerDialog");
    }

    /*@Override
    public void onDialogPositiveClick(GenericDialogFragment dialog) {
        super.onDialogPositiveClick(dialog);

        if(dialog instanceof SoundManagerDialogFragment){
            soundManagerCheckedID = ((SoundManagerDialogFragment)dialog).checkedID;
        }
    }*/
}