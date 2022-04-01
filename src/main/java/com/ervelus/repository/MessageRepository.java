package com.ervelus.repository;

import com.ervelus.model.Message;
import com.ervelus.model.User;

import java.util.List;

public interface MessageRepository {
    void save(Message message);
    List<Message> loadChat(User userFrom, User userTo);
}
