package com.ervelus.integration;

import com.ervelus.controllers.AuthenticationController;
import com.ervelus.controllers.FriendController;
import com.ervelus.controllers.MessageController;
import com.ervelus.model.FriendListEntry;
import com.ervelus.model.FriendStatus;
import com.ervelus.model.User;
import com.ervelus.repository.FriendListRepository;
import com.ervelus.repository.MessageRepository;
import com.ervelus.repository.UserRepository;
import com.ervelus.security.TokenProvider;
import com.ervelus.security.TokenSecurityFilter;
import com.ervelus.server.ChatServer;
import com.ervelus.server.Resolver;
import com.ervelus.server.Validator;
import com.ervelus.server.request.CommandResolver;
import com.ervelus.server.request.RequestDispatcher;
import com.ervelus.server.request.RequestValidator;
import com.ervelus.service.defaultimpl.ChatServiceImpl;
import com.ervelus.service.defaultimpl.FriendServiceImpl;
import com.ervelus.service.defaultimpl.MessageServiceImpl;
import com.ervelus.service.defaultimpl.UserServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceIntegrationTest {
    static ChatServer server;
    static RequestDispatcher dispatcher;
    static FriendController friendController;
    static AuthenticationController authenticationController;
    static MessageController messageController;
    static String secret = "secret";
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.qmaQQC0dRNqj-43IVxsOlziKQMibjSVM6E3_-rDVKWU";
    String username = "username";

    @BeforeAll
    static void setUp() throws IOException {
        server = new ChatServer();
        ServerSocket serverSocket = new ServerSocket(3005);
        dispatcher = new RequestDispatcher();
        Validator validator = new RequestValidator();
        Resolver resolver = new CommandResolver();
        TokenSecurityFilter filter = new TokenSecurityFilter();
        TokenProvider tokenProvider = new TokenProvider();
        tokenProvider.setTokenSecret(secret);
        filter.setProvider(tokenProvider);
        friendController = new FriendController();
        messageController = new MessageController();
        authenticationController = new AuthenticationController();
        dispatcher.setAuthenticationController(authenticationController);
        dispatcher.setMessageController(messageController);
        dispatcher.setFriendController(friendController);
        dispatcher.setValidator(validator);
        dispatcher.setResolver(resolver);
        dispatcher.setSecurityFilter(filter);
        authenticationController.setTokenProvider(tokenProvider);
        messageController.setTokenProvider(tokenProvider);
        friendController.setTokenProvider(tokenProvider);
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
    void testWithUserService() throws IOException {
        String pass = "pass";
        String encodedPassword = DigestUtils.sha256Hex(pass);
        User user = new User(username, encodedPassword);
        Socket clientSocket = new Socket("localhost", 3005);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        UserServiceImpl userService = new UserServiceImpl();
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findUserByUsername(username)).thenReturn(user);
        userService.setUserRepository(userRepository);
        authenticationController.setUserService(userService);
        writer.write("login&"+username+"&"+pass);
        writer.newLine();
        writer.flush();
        String expected = "Token: "+token;
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void testWithFriendService() throws IOException {
        String pass = "pass";
        String encodedPassword = DigestUtils.sha256Hex(pass);
        User user = new User(username, encodedPassword);
        Socket clientSocket = new Socket("localhost", 3005);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        UserServiceImpl userService = new UserServiceImpl();
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        FriendServiceImpl friendService = new FriendServiceImpl();
        friendService.setUserService(userService);
        FriendListRepository friendListRepository = Mockito.mock(FriendListRepository.class);
        Mockito.when(friendListRepository.getFriendList(user)).thenReturn(new ArrayList<>());
        Mockito.when(userRepository.findUserByUsername(username)).thenReturn(user);
        userService.setUserRepository(userRepository);
        friendService.setFriendListRepository(friendListRepository);
        friendController.setFriendService(friendService);
        writer.write(token+"&friends");
        writer.newLine();
        writer.flush();
        String expected = "Your friend list is currently empty. Send someone a friend request!";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void testWithChatService() throws IOException {
        String pass = "pass";
        String encodedPassword = DigestUtils.sha256Hex(pass);
        User user = new User(username, encodedPassword);
        User friend = new User("friend", encodedPassword);
        List<FriendListEntry> friends = new ArrayList<>();
        friends.add(new FriendListEntry(user, friend, FriendStatus.ACCEPTED));
        Socket clientSocket = new Socket("localhost", 3005);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        UserServiceImpl userService = new UserServiceImpl();
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        ChatServiceImpl chatService = new ChatServiceImpl();
        chatService.setUserService(userService);
        FriendServiceImpl friendService = new FriendServiceImpl();
        friendService.setUserService(userService);
        MessageServiceImpl messageService = new MessageServiceImpl();
        MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
        Mockito.when(messageRepository.loadChat(user, friend)).thenReturn(new ArrayList<>());
        Mockito.when(userRepository.findUserByUsername(username)).thenReturn(user);
        Mockito.when(userRepository.findUserByUsername("friend")).thenReturn(friend);
        FriendListRepository friendListRepository = Mockito.mock(FriendListRepository.class);
        Mockito.when(friendListRepository.getFriendList(user)).thenReturn(friends);

        userService.setUserRepository(userRepository);
        messageService.setMessageRepository(messageRepository);
        chatService.setMessageService(messageService);
        chatService.setFriendService(friendService);
        friendService.setFriendListRepository(friendListRepository);
        messageController.setChatService(chatService);
        writer.write(token+"&chat&friend");
        writer.newLine();
        writer.flush();
        String expected = "This is the beginning of your chat with friend";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }


}
