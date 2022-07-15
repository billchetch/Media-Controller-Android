package net.chetch.mediacontroller;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import net.chetch.appframework.GenericDialogFragment;

public class SoundManagerWaitingDialogFragment extends GenericDialogFragment {

    public String waitingMessage = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        inflateContentView(R.layout.sound_manager_waiting);

        Dialog dialog = createDialog();

        TextView tv = contentView.findViewById(R.id.waitingMessage);
        tv.setText(waitingMessage);

        return dialog;
    }
}
