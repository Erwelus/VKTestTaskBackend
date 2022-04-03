package com.ervelus.controllers;

import com.ervelus.model.Message;
import com.ervelus.model.User;
import com.ervelus.security.TokenProvider;
import com.ervelus.service.ChatService;
import com.ervelus.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class MessageControllerTest {
    MessageController controller;
    static TokenProvider tokenProvider;
    static ChatService chatService;
    static String username;
    static String friendName;
    static String friendWithoutChatName;
    static String notFriendName;
    static String token;
    static String messageText;
    static List<String> emptyList;
    static List<Message> notEmptyList;

    @BeforeAll
    static void init(){
        username = "username";
        friendName = "friend";
        notFriendName = "asd";
        friendWithoutChatName = "qwe";
        token = "token";
        messageText = "text";
        emptyList = new ArrayList<>();
        notEmptyList = new ArrayList<>();
        notEmptyList.add(new Message(new User( username, ""), new User(friendName, ""), messageText));
        tokenProvider = Mockito.mock(TokenProvider.class);
        Mockito.when(tokenProvider.getUsernameFromToken(any())).thenReturn(username);
        chatService = Mockito.mock(ChatService.class);
        Mockito.when(chatService.sendMessageToFriend(username, friendName, messageText)).thenReturn(true);
        Mockito.when(chatService.sendMessageToFriend(eq(username), eq(notFriendName), any())).thenReturn(false);
        Mockito.when(chatService.sendMessageToFriend(eq(username), eq(null), any())).thenReturn(false);
        Mockito.when(chatService.getChatWithFriend(username, notFriendName)).thenReturn(null);
        Mockito.when(chatService.getChatWithFriend(username, friendName)).thenReturn(notEmptyList);
    }


    @BeforeEach
    void setUp() {
        controller = new MessageController();
        controller.setTokenProvider(tokenProvider);
        controller.setChatService(chatService);
    }

    @Test
    void sendToOnlineFriend() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        StringWriter stringWriterFriend = new StringWriter();
        BufferedWriter outFriend = new BufferedWriter(stringWriterFriend);
        Map<String, BufferedWriter> map = new HashMap<>();
        map.put(friendName, outFriend);
        String expected = username+" wrote: "+messageText+System.lineSeparator();
        controller.send(token+"&send&"+friendName+"&"+messageText, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
        Assertions.assertEquals(expected, stringWriterFriend.toString());
    }

    @Test
    void sendToOfflineFriend() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        Map<String, BufferedWriter> map = new HashMap<>();
        String expected = username+" wrote: "+messageText+System.lineSeparator();
        controller.send(token+"&send&"+friendName+"&"+messageText, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void sendToNotFriend() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        Map<String, BufferedWriter> map = new HashMap<>();
        String expected = "Failed to send a message: friend not found"+System.lineSeparator();
        controller.send(token+"&send&"+notFriendName+"&"+messageText, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void sendIncorrect() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        Map<String, BufferedWriter> map = new HashMap<>();
        controller.send(token+"&send&"+notFriendName, out, map);
        Assertions.assertEquals("", stringWriter.toString());
    }

    @Test
    void getDialogWithFriend() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String expected = username+": "+messageText+"\n"+System.lineSeparator();
        controller.getDialog(token+"&chat&"+friendName, out);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void getDialogWithNotFriend() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String expected = "Friend not found"+System.lineSeparator();
        controller.getDialog(token+"&chat&"+notFriendName, out);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void getEmptyDialogWithFriend() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String expected = "This is the beginning of your chat with "+friendWithoutChatName+System.lineSeparator();
        controller.getDialog(token+"&chat&"+friendWithoutChatName, out);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void getDialogIncorrect() {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        controller.getDialog(token+"&chat"+notFriendName, out);
        Assertions.assertEquals("", stringWriter.toString());
    }
}