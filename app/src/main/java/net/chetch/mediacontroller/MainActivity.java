package net.chetch.mediacontroller;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import net.chetch.appframework.GenericActivity;
import net.chetch.appframework.GenericDialogFragment;
import net.chetch.appframework.IDialogManager;
import net.chetch.mediacontroller.models.MediaControllerMessageSchema;
import net.chetch.mediacontroller.models.MediaControllerModel;
import net.chetch.messaging.ClientConnection;
import net.chetch.messaging.MessagingViewModel;
import net.chetch.messaging.exceptions.MessagingServiceException;
import net.chetch.utilities.SLog;
import net.chetch.webservices.ConnectManager;
import net.chetch.messaging.exceptions.*;


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

    ConnectManager connectManager = new ConnectManager();
    MediaControllerModel mediaModel;
    Observer connectProgress  = obj -> {
        if(obj instanceof ConnectManager){
            ConnectManager cm = (ConnectManager)obj;

            String progressInfo = null;
            switch(cm.getState()){
                case CONNECT_REQUEST:
                    if(cm.fromError()){
                        String errMsg = "There was an error";
                        if(cm.getLastError() != null){
                            errMsg = errMsg + ": " + cm.getLastError().getMessage();
                        }
                        progressInfo = errMsg;
                    } else {
                        progressInfo = "Connecting...";
                    }
                    showConnectionState(progressInfo, false);
                    break;

                case CONNECTING:
                    showConnectionState("Connecting...", true);
                    break;

                case RECONNECTNG:
                    showConnectionState("Reconnecting...", true);
                    break;

                case RECONNECT_REQUEST:
                    progressInfo = "Disconnected!";
                    showConnectionState(progressInfo, false);
                    break;

                case CONNECTED:
                    hideConnectionState();
                    onClientConnected();
                    break;
            }
        } else {
            //Add more object specific handling here ... the ConnectManager models will also
            //call this progress observer directly as will the client in the case of a messaging model
        }
    };

    SoundManagerDialogFragment soundManagerDialog;
    int soundManagerCheckedID = 0;

    protected void showConnectionState(String connectionInfo, boolean showProgressBar){
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        View progressCtn = findViewById(R.id.progressCtn);
        View progressBar = findViewById(R.id.progressBar);

        if(connectionInfo == null){
            mainLayout.setVisibility(View.VISIBLE);
            progressCtn.setVisibility(View.INVISIBLE);
            setProgressInfo("Connected!");
        } else {
            progressBar.setVisibility(showProgressBar ? View.VISIBLE : View.GONE);
            mainLayout.setVisibility(View.INVISIBLE);
            progressCtn.setVisibility(View.VISIBLE);
            setProgressInfo(connectionInfo);
        }
    }

    protected void hideConnectionState(){
        showConnectionState(null, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SLog.LOG)SLog.i(LOG_TAG, "Create Main activity");

        includeActionBar(SettingsActivity.class);

        //connect
        try{
            //configure models
            mediaModel = new ViewModelProvider(this).get(MediaControllerModel.class);
            mediaModel.setClientName(MediaControllerModel.CLIENT_NAME);
            mediaModel.getError().observe(this, throwable  -> {
                if(mediaModel.isReady() && !ConnectManager.isConnectionError(throwable)) {
                    if(throwable instanceof MessagingServiceException){
                        MessagingServiceException ex = (MessagingServiceException)throwable;
                        MessagingViewModel.MessagingService ms = ex.messagingService;
                        if(ms.isResponsive()){
                            showError(ex);
                        }
                    } else {
                        showError(throwable);
                    }
                }
            });
            mediaModel.observeMessagingServices(this, ms ->{
                if(!ms.isResponsive()){
                    showConnectionState(ms.name + " is not responding. Please ensure all services are running on the mdeia server.", false);
                } else {
                    hideConnectionState();
                }
            });


            connectManager.addModel(mediaModel);
            connectManager.requestConnect(connectProgress); //comment this if you want to load without a server
            //hideConnectionState(); //uncomment this if you want to load without a server

        } catch (Exception e){
            showError(e);
            if(SLog.LOG)SLog.e(LOG_TAG, e.getMessage());
        }
    }

    private void onClientConnected(){
        if(SLog.LOG)SLog.i(LOG_TAG, "Client connected");

        //we ask for media player status
        mediaModel.sendPlayerCommand(MediaControllerMessageSchema.COMMAND_MEDIA_PLAYER_STATUS);
    }


    public void onClickMediaPlayerButton(View view){
        ImageButton btn = (ImageButton)view;

        if(SLog.LOG)SLog.i(LOG_TAG, "Media player button clicked " + btn.getTag());
        String cmd = btn.getTag().toString();
        cmd = cmd.toUpperCase();
        String keys2send;
        switch(cmd){
            case "UP":
            case "LEFT":
            case "DOWN":
            case "RIGHT":
            case "ENTER":
            case "ESC":
            case "ADD":
            case "SUBTRACT":
                keys2send = "{" + cmd + "}";
                sendPlayerCommand(keys2send, MediaControllerModel.VIBRATE);
                break;

            case "P":
                keys2send = cmd.toLowerCase();
                sendPlayerCommand(keys2send, MediaControllerModel.VIBRATE);
                break;
        }
    }

    private void sendPlayerCommand(String cmd, boolean vibrate){
        boolean sent = mediaModel.sendPlayerCommand(MediaControllerMessageSchema.COMMAND_KEY_PRESS, cmd);
        if(sent && vibrate){
            Vibrate(this, 150);
        }
    }

    public void openSoundManager(View view) {
        if (soundManagerDialog != null) {
            soundManagerDialog.dismiss();
        }
        soundManagerDialog = new SoundManagerDialogFragment();
        soundManagerDialog.mediaModel = mediaModel;
        soundManagerDialog.checkedID = soundManagerCheckedID;
        soundManagerDialog.show(getSupportFragmentManager(), "SoundManagerDialog");
    }

    @Override
    public void onDialogPositiveClick(GenericDialogFragment dialog) {
        super.onDialogPositiveClick(dialog);

        if(dialog instanceof SoundManagerDialogFragment){
            soundManagerCheckedID = ((SoundManagerDialogFragment)dialog).checkedID;
        }
    }
}