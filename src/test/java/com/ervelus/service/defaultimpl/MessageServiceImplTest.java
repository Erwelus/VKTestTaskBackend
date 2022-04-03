package com.ervelus.service.defaultimpl;

import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.repository.MessageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class MessageServiceImplTest {
    MessageServiceImpl service;
    static MessageRepository messageRepository;
    static Message message;
    static Message secondMessage;
    static List<Message> emptyMessageList;
    static List<Message> notEmptyMessageList;
    static String username;
    static String friendName;
    static String friendWithoutChatName;
    static String userNotFriend;
    static User firstUser;
    static User secondUser;
    static User thirdUser;
    static User friendWithoutChat;

    @BeforeAll
    static void init(){
        username = "test_user";
        friendName = "test_friend";
        userNotFriend = "test_friend_3";
        friendWithoutChatName = "test_friend_2";
        firstUser = new User(1, username, "password");
        secondUser = new User(2, friendName, "password");
        thirdUser = new User(3, userNotFriend, "password");
        friendWithoutChat = new User(4, friendWithoutChatName, "password");
        message = new Message(firstUser, secondUser, "text");
        secondMessage = new Message(secondUser, firstUser, "text");
        emptyMessageList = new ArrayList<>();
        notEmptyMessageList = new ArrayList<>();
        notEmptyMessageList.add(message);
        notEmptyMessageList.add(secondMessage);
        messageRepository = Mockito.mock(MessageRepository.class);
        Mockito.when(messageRepository.loadChat(firstUser, secondUser)).thenReturn(notEmptyMessageList);
        Mockito.when(messageRepository.loadChat(firstUser, friendWithoutChat)).thenReturn(emptyMessageList);
        Mockito.when(messageRepository.loadChat(firstUser, thirdUser)).thenReturn(null);
        Mockito.when(messageRepository.loadChat(any(), eq(null))).thenReturn(null);
        Mockito.when(messageRepository.loadChat(eq(null), any())).thenReturn(null);
    }


    @BeforeEach
    void setUp() {
        service = new MessageServiceImpl();
        service.setMessageRepository(messageRepository);
    }

    @Test
    void loadChat() {
        Assertions.assertEquals(notEmptyMessageList, service.loadChat(firstUser, secondUser));
        Assertions.assertEquals(emptyMessageList, service.loadChat(firstUser, friendWithoutChat));
        Assertions.assertNull(service.loadChat(firstUser, thirdUser));
        Assertions.assertNull(service.loadChat(firstUser, null));
        Assertions.assertNull(service.loadChat(null, secondUser));
        Assertions.assertNull(service.loadChat(null, null));
    }
}