package com.ervelus.integration;

import com.ervelus.controllers.AuthenticationController;
import com.ervelus.controllers.FriendController;
import com.ervelus.controllers.MessageController;
import com.ervelus.model.User;
import com.ervelus.security.SecurityFilter;
import com.ervelus.security.TokenProvider;
import com.ervelus.server.ChatServer;
import com.ervelus.server.Resolver;
import com.ervelus.server.Validator;
import com.ervelus.server.request.CommandResolver;
import com.ervelus.server.request.RequestDispatcher;
import com.ervelus.server.request.RequestValidator;
import com.ervelus.service.ChatService;
import com.ervelus.service.FriendService;
import com.ervelus.service.UserService;
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

public class ControllersIntegrationTest {
    static ChatServer server;
    static RequestDispatcher dispatcher;

    @BeforeAll
    static void setUp() throws IOException {
        server = new ChatServer();
        ServerSocket serverSocket = new ServerSocket(3003);
        dispatcher = new RequestDispatcher();
        Validator validator = new RequestValidator();
        Resolver resolver = new CommandResolver();
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
    void testAuthController() throws IOException {
        String username = "user";
        String pass = "pass";
        User user = new User(username, pass);
        Socket clientSocket = new Socket("localhost", 3003);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        AuthenticationController controller = new AuthenticationController();
        UserService userService = Mockito.mock(UserService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        Mockito.when(tokenProvider.createToken("user")).thenReturn("token");
        Mockito.when(userService.findByUsername("user")).thenReturn(user);
        Mockito.when(userService.validateCredentials(username,pass,user)).thenReturn(true);
        controller.setTokenProvider(tokenProvider);
        controller.setUserService(userService);
        dispatcher.setAuthenticationController(controller);
        writer.write("login&"+username+"&"+pass);
        writer.newLine();
        writer.flush();
        String expected = "Token: token";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void testFriendController() throws IOException {
        String username = "user";
        String req = "token&friends";
        Socket clientSocket = new Socket("localhost", 3003);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        FriendController controller = new FriendController();
        FriendService friendService = Mockito.mock(FriendService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        SecurityFilter filter = Mockito.mock(SecurityFilter.class);
        Mockito.when(tokenProvider.getUsernameFromToken("token")).thenReturn(username);
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn("token");
        Mockito.when(friendService.getFriendList(username)).thenReturn(new ArrayList<>());
        Mockito.when(filter.doFilter(req)).thenReturn(true);
        dispatcher.setSecurityFilter(filter);
        controller.setTokenProvider(tokenProvider);
        controller.setFriendService(friendService);
        dispatcher.setFriendController(controller);
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
    void testMessageController() throws IOException {
        String username = "user";
        String friend = "friend";
        String req = "token&chat&"+friend;
        Socket clientSocket = new Socket("localhost", 3003);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        MessageController controller = new MessageController();
        ChatService chatService = Mockito.mock(ChatService.class);
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        SecurityFilter filter = Mockito.mock(SecurityFilter.class);
        Mockito.when(tokenProvider.getUsernameFromToken("token")).thenReturn(username);
        Mockito.when(tokenProvider.resolveToken(req)).thenReturn("token");
        Mockito.when(chatService.getChatWithFriend(username, friend)).thenReturn(new ArrayList<>());
        Mockito.when(filter.doFilter(req)).thenReturn(true);
        dispatcher.setSecurityFilter(filter);
        controller.setTokenProvider(tokenProvider);
        controller.setChatService(chatService);
        dispatcher.setMessageController(controller);
        writer.write(req);
        writer.newLine();
        writer.flush();
        String expected = "This is the beginning of your chat with "+friend;
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }
}
