package com.ervelus.service;

import com.ervelus.model.Message;
import com.ervelus.model.User;

import java.util.List;

public interface MessageService {
    void send(Message message);
    List<Message> loadChat(User userFrom, User userTo);
}
