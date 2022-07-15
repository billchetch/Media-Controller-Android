package net.chetch.mediacontroller;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.chetch.appframework.GenericDialogFragment;
import net.chetch.mediacontroller.models.MediaControllerMessageSchema;
import net.chetch.mediacontroller.models.MediaControllerModel;
import net.chetch.utilities.SLog;

import androidx.annotation.NonNull;

public class SoundManagerDialogFragment extends GenericDialogFragment implements View.OnClickListener{

    static final String LOG_TAG = "SMDF";

    public MediaControllerModel mediaModel;
    public int checkedID = 0;
    private String amplifierID = "";
    private SoundManagerWaitingDialogFragment waitingDialog;
    private String selectedSource;
    Handler waitHandler = new Handler();
    Runnable waitRunnable = ()->{
        closeWaiting();
    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inflateContentView(R.layout.sound_manager);

        Dialog dialog = createDialog();

        Button btn = contentView.findViewById(R.id.closeButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        //Inside or Outside
        RadioGroup rg = contentView.findViewById(R.id.locationGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedID = checkedId;
                switch(checkedId){
                    case R.id.rbInside:
                        amplifierID = "lght1";
                        break;
                    case R.id.rbOutside:
                        amplifierID = "lght2";
                        break;

                }
            }
        });


        if(checkedID == 0){
            checkedID = R.id.rbInside;
        }
        rg.check(checkedID);

        //Buttons
        int[] resources = new int[]{R.id.volumeUp, R.id.volumeDown, R.id.muteUnmute, R.id.sourceMediaPlayer, R.id.sourceBluetooth, R.id.sourceAux};
        for(int i = 0; i < resources.length; i++){
            View v = contentView.findViewById(resources[i]);
            v.setOnClickListener(this);
        }

        mediaModel.getLastServiceCommandResponse().observe(getActivity(), ld->{
            if(SLog.LOG)SLog.i(LOG_TAG, "Last service command response received");
            //in case required....
        });


        if(SLog.LOG)SLog.i(LOG_TAG, "Sound Manager Dialog created");

        return dialog;
    }



    @Override
    public void onClick(View view) {

        Object cmd = view.getTag();
        if(cmd != null && amplifierID != null) {

            cmd = "adm:" + amplifierID + ":" + cmd;
            if (SLog.LOG) SLog.i(LOG_TAG, cmd.toString());
            boolean source = false;
            switch(view.getTag().toString()){
                case "Optical":
                    selectedSource = "Media Player";
                    source = true;
                    break;
                case "Bluetooth":
                case "Aux":
                    selectedSource = view.getTag().toString();
                    source = true;
                    break;

                default:
                    source = false;
                    break;
            }
            if(source) {
                openWaiting("Selecting " + selectedSource + "... Please wait", 3000);
            }
            sendServiceCommand(cmd.toString(), MediaControllerModel.VIBRATE);
        }
    }

    private void sendServiceCommand(String cmd, boolean vibrate){
        boolean sent = mediaModel.sendServiceCommand(cmd);
        if(sent) {
            if (vibrate) {
                MainActivity.Vibrate(getActivity(), 150);
            }
        }
    }

    private void openWaiting(String message, int millis){
        waitingDialog = new SoundManagerWaitingDialogFragment();
        waitingDialog.waitingMessage = message;
        waitingDialog.show(getChildFragmentManager(), "WaitingDialog");
        waitHandler.postDelayed(waitRunnable, millis);
    }

    private void closeWaiting(){
        if(waitingDialog != null)waitingDialog.dismiss();
        waitingDialog = null;
    }

    private void close(){
        if(dialogManager != null)dialogManager.onDialogPositiveClick(this);
        dismiss();
    }
}
