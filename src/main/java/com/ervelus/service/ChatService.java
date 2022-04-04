package com.ervelus.service;

import com.ervelus.model.Message;
import com.ervelus.model.User;

import java.util.List;

/**
 * Interface for business-logic related to chatting
 */
public interface ChatService {
    /**
     * get the latest messages with friend
     * @param username who sent request
     * @param friendName a friend who chat is with
     * @return List of Message entities, the last element is the latest message
     */
    List<Message> getChatWithFriend(String username, String friendName);

    /**
     * Sends a message to a friend
     * @param username who sends
     * @param friendName who receives
     * @param text what should be sent
     * @return if was sent
     */
    boolean sendMessageToFriend(String username, String friendName, String text);
}
