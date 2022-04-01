package com.ervelus.service;

import com.ervelus.model.Message;
import com.ervelus.model.User;

import java.util.List;

public interface ChatService {
    List<Message> getChatWithFriend(String username, String friendName);
    boolean sendMessageToFriend(String username, String friendName, String text);
}
