package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.service.ChatService;
import com.ervelus.service.FriendService;
import com.ervelus.service.MessageService;
import com.ervelus.service.UserService;

import java.util.List;

public class ChatServiceImpl implements ChatService {
    @InjectByType
    private MessageService messageService;
    @InjectByType
    private UserService userService;
    @InjectByType
    private FriendService friendService;

    @Override
    public List<Message> getChatWithFriend(String username, String friendName) {
        User user = userService.findByUsername(username);
        if (friendService.getFriendList(username) != null &&
                friendService.getFriendNameList(username).contains(friendName)) {
            User friend = userService.findByUsername(friendName);
            if (user == null || friend == null) return null;
            return messageService.loadChat(user, friend);
        }else return null;
    }

    @Override
    public boolean sendMessageToFriend(String username, String friendName, String text) {
        User user = userService.findByUsername(username);
        if (friendService.getFriendList(username) != null &&
                friendService.getFriendNameList(username).contains(friendName)) {
            User friend = userService.findByUsername(friendName);
            if (user == null || friend == null) return false;
            messageService.send(new Message(user, friend, text));
            return true;
        }else return false;
    }
}
