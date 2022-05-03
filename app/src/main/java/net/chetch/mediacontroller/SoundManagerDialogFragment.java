package net.chetch.mediacontroller;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import net.chetch.appframework.GenericDialogFragment;
import net.chetch.utilities.SLog;

public class SoundManagerDialogFragment extends GenericDialogFragment implements View.OnClickListener{

    static final String LOG_TAG = "SMDF";

    private String amplifierID = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inflateContentView(R.layout.sound_manager);

        Dialog dialog = createDialog();

        Button btn = contentView.findViewById(R.id.closeButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //Inside or Outside
        RadioGroup rg = contentView.findViewById(R.id.locationGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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

        rg.check(R.id.rbInside);

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
        if(cmd != null) {
            cmd = amplifierID + ":" + cmd;
            if (SLog.LOG) SLog.i(LOG_TAG, cmd.toString());
        }
    }
}
