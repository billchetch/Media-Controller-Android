package net.chetch.mediacontroller;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

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


        if(SLog.LOG)SLog.i(LOG_TAG, "Sound Manager Dialog created");

        return dialog;
    }



    @Override
    public void onClick(View view) {

        Object cmd = view.getTag();
        if(cmd != null && amplifierID != null) {

            cmd = "adm:" + amplifierID + ":" + cmd;
            if (SLog.LOG) SLog.i(LOG_TAG, cmd.toString());
            sendServiceCommand(cmd.toString(), true);
        }
    }

    private void sendServiceCommand(String cmd, boolean vibrate){
        boolean sent = mediaModel.sendServiceCommand(cmd);
        if(sent && vibrate){
            MainActivity.Vibrate(getActivity(), 150);
        }
    }

    private void close(){
        if(dialogManager != null)dialogManager.onDialogPositiveClick(this);
        dismiss();
    }
}
