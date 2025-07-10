package net.chetch.mediacontroller.models;

import android.content.Context;
import android.util.Log;

import net.chetch.bluetooth.BluetoothViewModel;
import net.chetch.bluetooth.exceptions.BluetoothException;
import net.chetch.messaging.Message;
import net.chetch.messaging.MessageType;
import net.chetch.messaging.MessagingViewModel;
import net.chetch.messaging.filters.CommandResponseFilter;
import net.chetch.utilities.SLog;
import net.chetch.xmpp.ChetchXMPPViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class MediaControllerModel extends BluetoothViewModel {

    static private final String LOG_TAG = "MCM";
    static public boolean VIBRATE = true;

    static public final String SENDER = "Bluetooth";

    static public String REMOTE_BLUETOOTH_DEVICE = "";

    public void init(Context context) throws BluetoothException {
        super.init(context, REMOTE_BLUETOOTH_DEVICE);
    }

    public boolean sendPlayerShortcut(String shortcut){
        try {
            Message message = new Message();
            message.Type = MessageType.COMMAND;
            message.Target = "MediaPlayer";
            message.Sender = SENDER;
            message.addValue("Shortcut", shortcut);
            this.send(message);
            return true;
        } catch (Exception e){
            postError(e);
            return false;
        }
    }

    public boolean sendIRAlias(String target, String alias){
        try {
            Message message = new Message();
            message.Type = MessageType.COMMAND;
            message.Sender = SENDER;
            message.Target = target;
            message.addValue("Alias", alias);
            this.send(message);
            return true;
        } catch (Exception e){
            postError(e);
            return false;
        }
    }
}
