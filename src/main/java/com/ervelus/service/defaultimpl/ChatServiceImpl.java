package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.service.ChatService;
import com.ervelus.service.MessageService;
import com.ervelus.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class ChatServiceImpl implements ChatService {
    @InjectByType
    private MessageService messageService;
    @InjectByType
    private UserService userService;

    @Override
    public List<Message> getChatWithFriend(String username, String friendName) {
        User user = userService.findByUsername(username);
        User friend = userService.findByUsername(friendName);
        if (user==null || friend ==null) return null;

        return messageService.loadChat(user, friend);
    }

    @Override
    public boolean sendMessageToFriend(String username, String friendName, String text) {
        User user = userService.findByUsername(username);
        User friend = userService.findByUsername(friendName);
        if (user==null || friend ==null) return false;
        messageService.send(new Message(user, friend, text));
        return true;
    }
}
