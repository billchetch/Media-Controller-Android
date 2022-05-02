package net.chetch.mediacontroller;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import net.chetch.appframework.GenericActivity;
import net.chetch.mediacontroller.models.MediaControllerMessageSchema;
import net.chetch.mediacontroller.models.MediaControllerModel;
import net.chetch.messaging.ClientConnection;
import net.chetch.utilities.SLog;
import net.chetch.webservices.ConnectManager;
import net.chetch.webservices.WebserviceViewModel;


public class MainActivity extends GenericActivity {
    final static String LOG_TAG = "Main";

    ConnectManager connectManager = new ConnectManager();
    MediaControllerModel mediaModel;
    Observer connectProgress  = obj -> {
        if(obj instanceof WebserviceViewModel.LoadProgress) {
            WebserviceViewModel.LoadProgress progress = (WebserviceViewModel.LoadProgress) obj;
            try {
                String state = progress.startedLoading ? "Loading" : "Loaded";
                String progressInfo = state + (progress.info == null ? "" : " " + progress.info.toLowerCase());
                if(SLog.LOG)SLog.i(LOG_TAG, "in load data progress ..." + progressInfo);

            } catch (Exception e) {
                if(SLog.LOG)SLog.e(LOG_TAG, "load progress: " + e.getMessage());
            }
        } else if(obj instanceof ClientConnection){

        } else if(obj instanceof ConnectManager){
            ConnectManager cm = (ConnectManager)obj;
            //ConstraintLayout mainLayout = findViewById(R.id.erMainLayout);
            View progressCtn = findViewById(R.id.progressCtn);
            switch(cm.getState()){
                case CONNECT_REQUEST:
                    if(cm.fromError()){
                        String errMsg = "There was an error";
                        if(cm.getLastError() != null){
                            errMsg = errMsg + ": " + cm.getLastError().getMessage();
                        }
                        setProgressInfo(errMsg + "... retrying");
                    } else {
                        setProgressInfo("Connecting...");
                    }
                    //mainLayout.setVisibility(View.INVISIBLE);
                    progressCtn.setVisibility(View.VISIBLE);
                    break;

                case RECONNECT_REQUEST:
                    setProgressInfo("Disconnected!... Attempting to reconnect...");
                    //mainLayout.setVisibility(View.INVISIBLE);
                    progressCtn.setVisibility(View.VISIBLE);
                    break;

                case CONNECTED:
                    //mainLayout.setVisibility(View.VISIBLE);
                    progressCtn.setVisibility(View.INVISIBLE);
                    onClientConnected();
                    break;
            }
        }
    };

    SoundManagerDialogFragment soundManagerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(SLog.LOG)SLog.i(LOG_TAG, "Create Main activity");

        //connect
        try{
            //configure models
            mediaModel = new ViewModelProvider(this).get(MediaControllerModel.class);
            mediaModel.setClientName(MediaControllerModel.CLIENT_NAME);
            mediaModel.getError().observe(this, throwable  -> {
                showError(throwable);
            });

            connectManager.addModel(mediaModel);
            //connectManager.requestConnect(connectProgress);
        } catch (Exception e){
            showError(e);
            if(SLog.LOG)SLog.e(LOG_TAG, e.getMessage());
        }
    }

    private void onClientConnected(){
        if(SLog.LOG)SLog.e(LOG_TAG, "Client connected");
    }


    public void onClickMediaPlayerButton(View view){
        ImageButton btn = (ImageButton)view;

        if(SLog.LOG)SLog.e(LOG_TAG, "Media player button clicked " + btn.getTag());

        //mediaModel.sendPlayerCommand(MediaControllerMessageSchema.COMMAND_HELP);
    }

    public void openSoundManager(View view) {
        if (soundManagerDialog != null) {
            soundManagerDialog.dismiss();
        }
        soundManagerDialog = new SoundManagerDialogFragment();

        soundManagerDialog.show(getSupportFragmentManager(), "SoundManagerDialog");
    }
}