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

/**
 * Default implementation of ChatService.
 * Is annotated with @Component, thus will be saved in context
 */
public class ChatServiceImpl implements ChatService {
    /**
     * Instance of MessageService for working with messages.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private MessageService messageService;
    /**
     * Instance of UserService for getting users by usernames.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private UserService userService;
    /**
     * Instance of UserService for getting friend lists by usernames.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private FriendService friendService;

    /**
     * If users are not null, and they are friends, returns list of messages between them. Otherwise - null
     * @param username who sent request
     * @param friendName a friend who chat is with
     */
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

    /**
     * If users are not null, and they are friends, sends a message to a friend.
     * @param username who sends
     * @param friendName who receives
     * @param text what should be sent
     * @return if the message was sent
     */
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
