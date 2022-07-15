package net.chetch.mediacontroller.models;

import android.util.Log;

import net.chetch.messaging.Message;
import net.chetch.messaging.MessagingViewModel;
import net.chetch.messaging.filters.CommandResponseFilter;
import net.chetch.utilities.SLog;
import net.chetch.webservices.DataStore;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class MediaControllerModel extends MessagingViewModel {

    static public final String CLIENT_NAME = "ACMMediaController";
    static private final String LOG_TAG = "MCM";
    static public boolean VIBRATE = true;

    MutableLiveData<MediaControllerMessageSchema> liveDataLastServiceCommandResponse = new MutableLiveData<>();

    public CommandResponseFilter onServiceHCommandResponse = new CommandResponseFilter(MediaControllerMessageSchema.SERVICE_NAME){
        @Override
        protected void onMatched(Message message) {
            MediaControllerMessageSchema schema = new MediaControllerMessageSchema(message);
            liveDataLastServiceCommandResponse.postValue(schema);

        }
    };

    public CommandResponseFilter onPlayerCommandResponse = new CommandResponseFilter(MediaControllerMessageSchema.PLAYER_NAME){
        @Override
        protected void onMatched(Message message) {
            MediaControllerMessageSchema schema = new MediaControllerMessageSchema(message);

        }
    };

    public MediaControllerModel(){
        super();

        permissableServerTimeDifference = 60 * 15;

        try {
            addMessageFilter(onServiceHCommandResponse);
            addMessageFilter(onPlayerCommandResponse);

        } catch (Exception e){
            if(SLog.LOG)SLog.e(LOG_TAG, e.getMessage());
        }
    }

    public boolean sendServiceCommand(String commandName, Object ... args){
        MessagingService ms = getMessaingService(MediaControllerMessageSchema.SERVICE_NAME);
        if(ms != null && ms.isResponsive()) {
            getClient().sendCommand(MediaControllerMessageSchema.SERVICE_NAME, commandName, args);
            return true;
        } else {
            return false;
        }
    }

    public LiveData<MediaControllerMessageSchema> getLastServiceCommandResponse(){
        return liveDataLastServiceCommandResponse;
    }

    public boolean sendPlayerCommand(String commandName, Object ... args){
        MessagingService ms = getMessaingService(MediaControllerMessageSchema.PLAYER_NAME);
        if(ms != null && ms.isResponsive()) {
            getClient().sendCommand(MediaControllerMessageSchema.PLAYER_NAME, commandName, args);
            return true;
        } else {
            return false;
        }
    }
}
