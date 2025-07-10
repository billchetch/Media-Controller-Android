package net.chetch.mediacontroller;

import android.content.Context;
import android.content.DialogInterface;
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
import net.chetch.appframework.GenericDialogFragment;
import net.chetch.appframework.IDialogManager;
import net.chetch.appframework.NotificationBar;
import net.chetch.bluetooth.BluetoothViewModel;
import net.chetch.bluetooth.exceptions.BluetoothConfigurationException;
import net.chetch.bluetooth.exceptions.BluetoothConnectionException;
import net.chetch.bluetooth.exceptions.BluetoothException;
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
            model.getError().observe(this, t -> {
                if(t instanceof BluetoothConfigurationException){
                    switch(((BluetoothConfigurationException)t).getCode()){
                        case BluetoothConfigurationException.NO_DEVICE_OR_MAC_ADDRESS:
                            showError("No device name or mac address found. Please add one in settings.").setOnDismissListener(dialogInterface -> {
                                //Open settings
                                openSettings();
                            });
                            break;

                        case BluetoothConfigurationException.DEVICE_NOT_PAIRED:
                            showError("Device not paired. Please check remote device name is correct in settings and ensure this is paired with the remote device.").setOnDismissListener(dialogInterface -> {
                                openSettings();
                            });
                            break;

                        case BluetoothConfigurationException.USER_REFUSED_TO_ENABLE_ADAPTER:
                            showError("This application cannot work without enabling bluetooth. Please enable in order to use this app.").setOnDismissListener(dialogInterface -> {
                                System.exit(0);
                            });
                            break;
                    }
                } else if(t instanceof BluetoothConnectionException){
                    String errMsg = null;
                    switch(((BluetoothConnectionException)t).getCode()){
                        case BluetoothConnectionException.SOCKET_CONNECT_FAILURE:
                        case BluetoothConnectionException.REMOTE_ADAPTER_TURNED_OFF:
                            errMsg = "Failed to connect to remote device.  Please ensure it is on and can accept a bluetooth connection";
                            break;
                        case BluetoothConnectionException.LOCAL_ADAPTER_TURNED_OFF:
                            errMsg = "Failed to connect.  Please ensure your bluetooth is enabled";
                            break;
                    }
                    if(errMsg != null){
                        showError(errMsg, false).throwable = t;
                    }
                }
            });
            model.getConnectStatus().observe(this, status -> {
                View mainLayout = findViewById(R.id.mainLayout);
                switch(status){
                    case CONNECTING:
                        showProgress("Connecting please wait...");
                        mainLayout.setVisibility(View.INVISIBLE);
                        break;
                    case CONNECTED:
                        dismissError();
                        hideProgress();
                        mainLayout.setVisibility(View.VISIBLE);
                        break;
                    case DISCONNECTED:
                        hideProgress();
                        mainLayout.setVisibility(View.INVISIBLE);
                        break;
                }
            });
            model.getReceived().observe(this, msg ->{
                //TODO
            });

            if(!model.isConnected()) {
                model.init(this);
                model.connect();
            }
        }  catch (BluetoothException e){
            if(e.getCode() == BluetoothException.NO_ADAPTER_AVAILABLE){
                showError("This application cannot be used as this device does not have bluetooth!").setOnDismissListener(dialogInterface -> {
                    System.exit(0);
                });
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