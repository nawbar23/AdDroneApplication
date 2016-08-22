package com.ericsson.addroneapplication.comunication.data;

import com.ericsson.addroneapplication.comunication.messages.CommunicationMessage;

/**
 * Created by nbar on 2016-08-19.
 * Interface for all communications message parsed to their values
 */
public interface CommunicationMessageValue {
    CommunicationMessage getMessage();
}
