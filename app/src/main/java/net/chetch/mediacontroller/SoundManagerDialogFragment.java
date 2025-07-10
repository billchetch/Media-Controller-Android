package net.chetch.mediacontroller;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import net.chetch.appframework.GenericDialogFragment;
import net.chetch.mediacontroller.models.MediaControllerModel;
import net.chetch.utilities.SLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SoundManagerDialogFragment extends GenericDialogFragment implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    static final String LOG_TAG = "SMDF";

    static final String LG_INSIDE = "lgin";
    static final String LG_OUTSIDE = "lgout";
    static final int MENU_ITEM_TOGGLE_POWER_INSIDE = 100;
    static final int MENU_ITEM_TOGGLE_POWER_OUTSIDE = 101;
    static final int MENU_ITEM_TOGGLE_POWER_BOTH = 102;

    public MediaControllerModel model;
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
                if (checkedId == R.id.rbInside) {
                    amplifierID = LG_INSIDE;
                } else if (checkedId == R.id.rbOutside) {
                    amplifierID = LG_OUTSIDE;
                }
            }
        });

        ImageView iv = contentView.findViewById(R.id.powerOptions);
        registerForContextMenu(iv);

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

        Object alias = view.getTag();
        if(alias != null && amplifierID != null) {
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
            sendIRAlias(amplifierID, alias.toString(), MediaControllerModel.VIBRATE);
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, MENU_ITEM_TOGGLE_POWER_INSIDE, 0, "Toggle Amplifier Inside").setOnMenuItemClickListener(this);
        menu.add(0, MENU_ITEM_TOGGLE_POWER_OUTSIDE, 0, "Toggle Amplifier Outside").setOnMenuItemClickListener(this);
        menu.add(0, MENU_ITEM_TOGGLE_POWER_BOTH, 0, "Toggle Amplifiers").setOnMenuItemClickListener(this);

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        String alias;
        switch(menuItem.getItemId()) {
            case MENU_ITEM_TOGGLE_POWER_INSIDE:
                alias = "On/Off";
                sendIRAlias("lght1", alias, MediaControllerModel.VIBRATE);
                break;

            case MENU_ITEM_TOGGLE_POWER_OUTSIDE:
                alias = "On/Off";
                sendIRAlias("lght2", alias, MediaControllerModel.VIBRATE);
                break;

            case MENU_ITEM_TOGGLE_POWER_BOTH:
                alias = "On/Off";
                sendIRAlias("lght1", alias, MediaControllerModel.VIBRATE);
                sendIRAlias("lght2", alias, MediaControllerModel.VIBRATE);
                break;

            default:
                break;
        }
        return false;
    }

    private void sendIRAlias(String target, String alias, boolean vibrate){
        boolean sent = model.sendIRAlias(target, alias);
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
