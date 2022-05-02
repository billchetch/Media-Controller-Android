package net.chetch.mediacontroller.models;

import android.util.Log;

import net.chetch.messaging.Message;
import net.chetch.messaging.MessagingViewModel;
import net.chetch.messaging.filters.CommandResponseFilter;
import net.chetch.utilities.SLog;
import net.chetch.webservices.DataStore;

import androidx.lifecycle.Observer;

public class MediaControllerModel extends MessagingViewModel {

    static public final String CLIENT_NAME = "ACMMediaController";
    static private final String LOG_TAG = "MCM";

    public CommandResponseFilter onServiceHelp = new CommandResponseFilter(MediaControllerMessageSchema.SERVICE_NAME, MediaControllerMessageSchema.COMMAND_HELP){
        @Override
        protected void onMatched(Message message) {
            MediaControllerMessageSchema schema = new MediaControllerMessageSchema(message);

        }
    };

    public CommandResponseFilter onPlayerKeyPressed = new CommandResponseFilter(MediaControllerMessageSchema.PLAYER_NAME, MediaControllerMessageSchema.COMMAND_KEY_PRESS){
        @Override
        protected void onMatched(Message message) {
            MediaControllerMessageSchema schema = new MediaControllerMessageSchema(message);

        }
    };

    public MediaControllerModel(){
        super();

        try {
            //addMessageFilter(onServiceHelp);
            addMessageFilter(onPlayerKeyPressed);

        } catch (Exception e){
            if(SLog.LOG)SLog.e(LOG_TAG, e.getMessage());
        }
    }

    public void sendServiceCommand(String commandName, Object ... args){
        getClient().sendCommand(MediaControllerMessageSchema.SERVICE_NAME, commandName, args);
    }

    public void sendPlayerCommand(String commandName, Object ... args){
        getClient().sendCommand(MediaControllerMessageSchema.PLAYER_NAME, commandName, args);
    }
}
