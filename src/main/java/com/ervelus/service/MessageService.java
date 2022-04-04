package com.ervelus.service;

import com.ervelus.model.Message;
import com.ervelus.model.User;

import java.util.List;
/**
 * Interface for business-logic related to working with messages
 */
public interface MessageService {
    /**
     * Sends a message to a friend
     * @param message message, containing sender, receiver and text
     */
    void send(Message message);

    /**
     * Loads the latest chat between given users
     * @return either empty list or list of messages between users
     */
    List<Message> loadChat(User userFrom, User userTo);
}
