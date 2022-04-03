package com.ervelus.controllers;

import com.ervelus.security.TokenProvider;
import com.ervelus.service.FriendService;
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

class FriendControllerTest {
    static FriendController controller;
    static List<String> emptyList;
    static String token;

    @BeforeAll
    static void init(){
        emptyList = new ArrayList<>();
        token="token";
        controller = new FriendController();
    }

    @Test
    void getFriendList() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String username = "username";
        String friend = "friend";
        String expected = friend+"\n"+System.lineSeparator();
        String req = token+"&friends";
        List<String> resList = new ArrayList<>();
        resList.add(friend);
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.getFriendList(username)).thenReturn(resList);
        controller.getFriendList(req, out);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void getFriendEmptyList() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String username = "userNoFriends";
        String expected = "Your friend list is currently empty. Send someone a friend request!"+System.lineSeparator();
        String req = token+"&friends";
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.getFriendList(username)).thenReturn(emptyList);
        controller.getFriendList(req, out);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void addFriendOnline() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        StringWriter stringWriterFriend = new StringWriter();
        BufferedWriter outFriend = new BufferedWriter(stringWriterFriend);
        String username = "username";
        String friend = "friend";
        Map<String, BufferedWriter> map = new HashMap<>();
        map.put(friend, outFriend);
        String expected = "Notification: Friend request successfully sent"+System.lineSeparator();
        String expectedFriend = "Notification: Incoming friend request from user "+username+System.lineSeparator();
        String req = token+"&add&"+friend;
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.sendFriendRequest(username, friend)).thenReturn(true);
        controller.addFriend(req, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
        Assertions.assertEquals(expectedFriend, stringWriterFriend.toString());
    }

    @Test
    void addFriendOffline() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String username = "username";
        String friend = "friend";
        Map<String, BufferedWriter> map = new HashMap<>();
        String expected = "Notification: Friend request successfully sent"+System.lineSeparator();
        String req = token+"&add&"+friend;
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.sendFriendRequest(username, friend)).thenReturn(true);
        controller.addFriend(req, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void addFriendNotExists() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String username = "username";
        String friend = "friend";
        Map<String, BufferedWriter> map = new HashMap<>();
        String expected = "Notification: Failed to send a friend request: user not found"+System.lineSeparator();
        String req = token+"&add&"+friend;
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.sendFriendRequest(username, friend)).thenReturn(false);
        controller.addFriend(req, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void acceptFriendOnline() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        StringWriter stringWriterFriend = new StringWriter();
        BufferedWriter outFriend = new BufferedWriter(stringWriterFriend);
        String username = "username";
        String friend = "friend";
        Map<String, BufferedWriter> map = new HashMap<>();
        map.put(friend, outFriend);
        String expected = "Notification: Friend successfully added"+System.lineSeparator();
        String expectedFriend = "Notification: "+username+" accepted your friend request"+System.lineSeparator();
        String req = token+"&accept&"+friend;
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.acceptFriendRequest(username, friend)).thenReturn(true);
        controller.acceptFriendRequest(req, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
        Assertions.assertEquals(expectedFriend, stringWriterFriend.toString());
    }

    @Test
    void acceptFriendOffline() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String username = "username";
        String friend = "friend";
        Map<String, BufferedWriter> map = new HashMap<>();
        String expected = "Notification: Friend successfully added"+System.lineSeparator();
        String req = token+"&accept&"+friend;
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.acceptFriendRequest(username, friend)).thenReturn(true);
        controller.acceptFriendRequest(req, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void acceptFriendNotExists() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String username = "username";
        String friend = "friend";
        Map<String, BufferedWriter> map = new HashMap<>();
        String expected = "Notification: Failed to accept friend request: request not found"+System.lineSeparator();
        String req = token+"&accept&"+friend;
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.acceptFriendRequest(username, friend)).thenReturn(false);
        controller.acceptFriendRequest(req, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void rejectFriendOnline() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        StringWriter stringWriterFriend = new StringWriter();
        BufferedWriter outFriend = new BufferedWriter(stringWriterFriend);
        String username = "username";
        String friend = "friend";
        Map<String, BufferedWriter> map = new HashMap<>();
        map.put(friend, outFriend);
        String expected = "Notification: Friend request rejected"+System.lineSeparator();
        String expectedFriend = "Notification: "+username+" rejected your friend request"+System.lineSeparator();
        String req = token+"&reject&"+friend;
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.rejectFriendRequest(username, friend)).thenReturn(true);
        controller.rejectFriendRequest(req, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
        Assertions.assertEquals(expectedFriend, stringWriterFriend.toString());
    }

    @Test
    void rejectFriendOffline() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String username = "username";
        String friend = "friend";
        Map<String, BufferedWriter> map = new HashMap<>();
        String expected = "Notification: Friend request rejected"+System.lineSeparator();
        String req = token+"&reject&"+friend;
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.rejectFriendRequest(username, friend)).thenReturn(true);
        controller.rejectFriendRequest(req, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
    }

    @Test
    void rejectFriendNotExists() {
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        controller.setFriendService(friendService);
        controller.setTokenProvider(tokenProvider);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        String username = "username";
        String friend = "friend";
        Map<String, BufferedWriter> map = new HashMap<>();
        String expected = "Notification: Failed to reject a friend request: request not found"+System.lineSeparator();
        String req = token+"&reject&"+friend;
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(friendService.rejectFriendRequest(username, friend)).thenReturn(false);
        controller.rejectFriendRequest(req, out, map);
        Assertions.assertEquals(expected, stringWriter.toString());
    }
}