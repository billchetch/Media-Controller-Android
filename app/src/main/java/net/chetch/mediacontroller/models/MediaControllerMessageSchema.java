package net.chetch.mediacontroller.models;

import net.chetch.messaging.Message;
import net.chetch.messaging.MessageSchema;

public class MediaControllerMessageSchema extends MessageSchema {

    static public final String SERVICE_NAME = "BBMedia";
    static public final String PLAYER_NAME = "BBMediaPlayer";

    static public final String COMMAND_MEDIA_SERVICE_STATUS = "status";
    static public final String COMMAND_MEDIA_PLAYER_STATUS = "mp-status";
    static public final String COMMAND_KEY_PRESS = "key-press";


    public MediaControllerMessageSchema(Message message){
        super(message);
    }
}
