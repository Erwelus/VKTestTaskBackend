package com.ervelus.integration;

import com.ervelus.controllers.AuthenticationController;
import com.ervelus.controllers.FriendController;
import com.ervelus.controllers.MessageController;
import com.ervelus.security.SecurityFilter;
import com.ervelus.security.TokenProvider;
import com.ervelus.security.TokenSecurityFilter;
import com.ervelus.server.ChatServer;
import com.ervelus.server.Resolver;
import com.ervelus.server.Validator;
import com.ervelus.server.request.CommandResolver;
import com.ervelus.server.request.RequestDispatcher;
import com.ervelus.server.request.RequestValidator;
import com.ervelus.service.FriendService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityIntegrationTest {
    static ChatServer server;
    static RequestDispatcher dispatcher;
    static FriendController friendController;
    String secret = "secret";
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.qmaQQC0dRNqj-43IVxsOlziKQMibjSVM6E3_-rDVKWU";
    String req = token+"&friends";
    String username = "username";

    @BeforeAll
    static void setUp() throws IOException {
        server = new ChatServer();
        ServerSocket serverSocket = new ServerSocket(3004);
        dispatcher = new RequestDispatcher();
        Validator validator = new RequestValidator();
        Resolver resolver = new CommandResolver();
        friendController = new FriendController();
        dispatcher.setFriendController(friendController);
        dispatcher.setValidator(validator);
        dispatcher.setResolver(resolver);
        server.setDispatcher(dispatcher);
        Map<String, BufferedWriter> connections = new ConcurrentHashMap<>();
        server.setConnections(connections);
        Thread thread = new Thread(() -> {
            try {
                server.setServerSocket(serverSocket);
                server.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @Test
    void testWithFilter() throws IOException {
        Socket clientSocket = new Socket("localhost", 3004);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        TokenSecurityFilter filter = new TokenSecurityFilter();
        dispatcher.setSecurityFilter(filter);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn(token);
        Mockito.when(tokenProvider.getUsernameFromToken(token)).thenReturn(username);
        Mockito.when(tokenProvider.validateToken(token)).thenReturn(true);
        FriendService friendService = Mockito.mock(FriendService.class);
        Mockito.when(friendService.getFriendList(username)).thenReturn(new ArrayList<>());
        friendController.setFriendService(friendService);
        friendController.setTokenProvider(tokenProvider);
        filter.setProvider(tokenProvider);
        writer.write(req);
        writer.newLine();
        writer.flush();
        String expected = "Your friend list is currently empty. Send someone a friend request!";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void testWithTokenProvider() throws IOException {
        Socket clientSocket = new Socket("localhost", 3004);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        TokenSecurityFilter filter = new TokenSecurityFilter();
        dispatcher.setSecurityFilter(filter);
        TokenProvider tokenProvider = new TokenProvider();
        tokenProvider.setTokenSecret(secret);
        FriendService friendService = Mockito.mock(FriendService.class);
        Mockito.when(friendService.getFriendList(username)).thenReturn(new ArrayList<>());
        friendController.setFriendService(friendService);
        friendController.setTokenProvider(tokenProvider);
        filter.setProvider(tokenProvider);
        writer.write(req);
        writer.newLine();
        writer.flush();
        String expected = "Your friend list is currently empty. Send someone a friend request!";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }
}
