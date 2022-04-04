package com.ervelus.repository;

import com.ervelus.model.Message;
import com.ervelus.model.User;

import java.util.List;

/**
 * Interface for persisting messages
 */
public interface MessageRepository {
    /**
     * Saves message into DB
     */
    void save(Message message);

    /**
     * Loads 10 last messages between to users from DB
     */
    List<Message> loadChat(User userFrom, User userTo);
}
