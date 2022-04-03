package com.ervelus.service.defaultimpl;

import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.service.ChatService;
import com.ervelus.service.FriendService;
import com.ervelus.service.MessageService;
import com.ervelus.service.UserService;
import lombok.Setter;

import java.util.List;


public class ChatServiceImpl implements ChatService {
    @InjectByType
    @Setter
    private MessageService messageService;
    @InjectByType
    @Setter
    private UserService userService;
    @InjectByType
    @Setter
    private FriendService friendService;

    @Override
    public List<Message> getChatWithFriend(String username, String friendName) {
        User user = userService.findByUsername(username);
        List<String> friendNameList = friendService.getFriendNameList(username);
        if (friendNameList != null && friendNameList.contains(friendName)) {
            User friend = userService.findByUsername(friendName);
            if (user == null || friend == null) return null;
            return messageService.loadChat(user, friend);
        }else return null;
    }

    @Override
    public boolean sendMessageToFriend(String username, String friendName, String text) {
        User user = userService.findByUsername(username);
        List<String> friendNameList = friendService.getFriendNameList(username);
        if (friendNameList != null && friendNameList.contains(friendName)) {
            User friend = userService.findByUsername(friendName);
            if (user == null || friend == null || text == null) return false;
            messageService.send(new Message(user, friend, text));
            return true;
        }else return false;
    }
}
