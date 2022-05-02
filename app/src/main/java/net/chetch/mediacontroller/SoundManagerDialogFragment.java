package net.chetch.mediacontroller;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.chetch.appframework.GenericDialogFragment;
import net.chetch.utilities.SLog;

public class SoundManagerDialogFragment extends GenericDialogFragment implements View.OnClickListener  {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inflateContentView(R.layout.sound_manager);

        Dialog dialog = createDialog();

        if(SLog.LOG)SLog.i("SMD", "Sound Manager Dialog created");

        return dialog;
    }

    @Override
    public void onClick(View view) {

    }
}
