package com.ervelus.service.defaultimpl;

import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.service.FriendService;
import com.ervelus.service.MessageService;
import com.ervelus.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class ChatServiceImplTest {
    static String username;
    static String friendName;
    static String secondFriendName;
    static String user_not_friend;
    static User first_user;
    static User second_user;
    static User third_user;
    static User fourth_user;
    static Message message;
    static List<Message> emptyMessageList;
    static List<Message> notEmptyMessageList;
    static List<String> emptyFriendList;
    static List<String> notEmptyFriendList;
    static UserService userServiceMock;
    static MessageService messageServiceMock;
    static FriendService friendServiceMock;
    ChatServiceImpl chatService;


    @BeforeAll
    static void init(){
        username = "test_user";
        friendName = "test_friend";
        secondFriendName = "test_friend_2";
        user_not_friend = "test_friend_3";
        first_user = new User(1, username, "password");
        second_user = new User(2, friendName, "password");
        third_user = new User(3, secondFriendName, "password");
        fourth_user = new User(4, user_not_friend, "password");
        message = new Message(first_user, second_user, "text");
        emptyMessageList = new ArrayList<>();
        notEmptyMessageList = new ArrayList<>();
        notEmptyMessageList.add(message);
        emptyFriendList = new ArrayList<>();
        notEmptyFriendList = new ArrayList<>();
        notEmptyFriendList.add(friendName);
        notEmptyFriendList.add(username);
        notEmptyFriendList.add(secondFriendName);
        userServiceMock = Mockito.mock(UserService.class);
        messageServiceMock = Mockito.mock(MessageService.class);
        friendServiceMock = Mockito.mock(FriendService.class);
        Mockito.when(userServiceMock.findByUsername(username)).thenReturn(first_user);
        Mockito.when(userServiceMock.findByUsername(friendName)).thenReturn(second_user);
        Mockito.when(userServiceMock.findByUsername(secondFriendName)).thenReturn(third_user);
        Mockito.when(userServiceMock.findByUsername("username")).thenReturn(null);
        Mockito.when(messageServiceMock.loadChat(first_user, second_user)).thenReturn(notEmptyMessageList);
        Mockito.when(messageServiceMock.loadChat(second_user, first_user)).thenReturn(notEmptyMessageList);
        Mockito.when(messageServiceMock.loadChat(first_user, third_user)).thenReturn(emptyMessageList);
        Mockito.when(messageServiceMock.loadChat(third_user, first_user)).thenReturn(emptyMessageList);
        Mockito.when(messageServiceMock.loadChat(any(), eq(null))).thenReturn(null);
        Mockito.when(messageServiceMock.loadChat(eq(null), any())).thenReturn(null);
        Mockito.when(friendServiceMock.getFriendNameList(username)).thenReturn(notEmptyFriendList);
        Mockito.when(friendServiceMock.getFriendNameList(friendName)).thenReturn(notEmptyFriendList);
        Mockito.when(friendServiceMock.getFriendNameList(secondFriendName)).thenReturn(notEmptyFriendList);
        Mockito.when(friendServiceMock.getFriendNameList(user_not_friend)).thenReturn(emptyFriendList);
        Mockito.when(friendServiceMock.getFriendNameList(null)).thenReturn(null);
    }

    @BeforeEach
    void initChatService(){
        chatService = new ChatServiceImpl();
        chatService.setFriendService(friendServiceMock);
        chatService.setUserService(userServiceMock);
        chatService.setMessageService(messageServiceMock);
    }


    @Test
    void testNotEmptyChat() {
        Assertions.assertEquals(notEmptyMessageList, chatService.getChatWithFriend(username, friendName));
        Assertions.assertEquals(notEmptyMessageList, chatService.getChatWithFriend(friendName, username));
    }

    @Test
    void testEmptyChat() {
        Assertions.assertEquals(emptyMessageList, chatService.getChatWithFriend(username, secondFriendName));
        Assertions.assertEquals(emptyMessageList, chatService.getChatWithFriend(secondFriendName, username));
    }
    @Test
    void testNullChat() {
        Assertions.assertNull(chatService.getChatWithFriend(username, null));
        Assertions.assertNull(chatService.getChatWithFriend(null, secondFriendName));
        Assertions.assertNull(chatService.getChatWithFriend(null, friendName));
        Assertions.assertNull(chatService.getChatWithFriend(null, null));
        Assertions.assertNull(chatService.getChatWithFriend(username, user_not_friend));
        Assertions.assertNull(chatService.getChatWithFriend(username, "username"));
    }

    @Test
    void sendMessageToFriend() {
        Assertions.assertTrue(chatService.sendMessageToFriend(username, friendName, message.getContent()));
        Assertions.assertTrue(chatService.sendMessageToFriend(friendName, username, message.getContent()));
        Assertions.assertFalse(chatService.sendMessageToFriend(username, user_not_friend, message.getContent()));
        Assertions.assertFalse(chatService.sendMessageToFriend(username, "username", message.getContent()));
        Assertions.assertFalse(chatService.sendMessageToFriend(username, null, message.getContent()));
        Assertions.assertFalse(chatService.sendMessageToFriend(username, "username", null));
    }
}