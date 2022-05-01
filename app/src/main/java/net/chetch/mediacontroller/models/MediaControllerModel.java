package net.chetch.mediacontroller.models;

import net.chetch.messaging.MessagingViewModel;
import net.chetch.webservices.DataStore;

import androidx.lifecycle.Observer;

public class MediaControllerModel extends MessagingViewModel {

    @Override
    public DataStore loadData(Observer observer) throws Exception {
        return super.loadData(observer);
    }
}
