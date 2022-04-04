package com.ervelus.integration;

import com.ervelus.controllers.AuthenticationController;
import com.ervelus.controllers.FriendController;
import com.ervelus.controllers.MessageController;
import com.ervelus.repository.DBConnector;
import com.ervelus.repository.UserRepository;
import com.ervelus.repository.defaultimpl.FriendListRepositoryImpl;
import com.ervelus.repository.defaultimpl.MessageRepositoryImpl;
import com.ervelus.repository.defaultimpl.UserRepositoryImpl;
import com.ervelus.security.TokenProvider;
import com.ervelus.security.TokenSecurityFilter;
import com.ervelus.server.ChatServer;
import com.ervelus.server.Resolver;
import com.ervelus.server.Validator;
import com.ervelus.server.request.CommandResolver;
import com.ervelus.server.request.RequestDispatcher;
import com.ervelus.server.request.RequestValidator;
import com.ervelus.service.FriendService;
import com.ervelus.service.defaultimpl.ChatServiceImpl;
import com.ervelus.service.defaultimpl.FriendServiceImpl;
import com.ervelus.service.defaultimpl.MessageServiceImpl;
import com.ervelus.service.defaultimpl.UserServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RepositoryIntegrationTest {
    static DBConnector connector;
    static String testDBURL;
    static String testDBUsername;
    static String testDBPassword;
    static ChatServer server;
    static RequestDispatcher dispatcher;
    static FriendController friendController;
    static AuthenticationController authenticationController;
    static MessageController messageController;
    static UserServiceImpl userService;
    static MessageServiceImpl messageService;
    static FriendServiceImpl friendService;
    static ChatServiceImpl chatService;
    static String secret = "secret";
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSJ9.qmaQQC0dRNqj-43IVxsOlziKQMibjSVM6E3_-rDVKWU";
    String username = "username";
    static String encodedPass = DigestUtils.sha256Hex("password");

    @BeforeAll
    static void setUp() throws IOException, SQLException {
        connector = new DBConnector();
        testDBURL = "jdbc:postgresql://localhost:5432/studs";
        testDBUsername = "s285583";
        testDBPassword = "nvn024";
        connector.setConnection(DriverManager.getConnection(testDBURL, testDBUsername, testDBPassword));
        server = new ChatServer();
        ServerSocket serverSocket = new ServerSocket(3006);
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
        userService = new UserServiceImpl();
        messageService = new MessageServiceImpl();
        friendService = new FriendServiceImpl();
        chatService = new ChatServiceImpl();
        friendService.setUserService(userService);
        chatService.setFriendService(friendService);
        chatService.setMessageService(messageService);
        chatService.setUserService(userService);
        dispatcher.setAuthenticationController(authenticationController);
        dispatcher.setMessageController(messageController);
        dispatcher.setFriendController(friendController);
        dispatcher.setValidator(validator);
        dispatcher.setResolver(resolver);
        dispatcher.setSecurityFilter(filter);
        authenticationController.setTokenProvider(tokenProvider);
        authenticationController.setUserService(userService);
        messageController.setTokenProvider(tokenProvider);
        messageController.setChatService(chatService);
        friendController.setTokenProvider(tokenProvider);
        friendController.setFriendService(friendService);
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
        prepareDB();
    }

    @Test
    void testWithUserRepository() throws IOException {
        Socket clientSocket = new Socket("localhost", 3006);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        userRepository.setConnector(connector);
        userService.setUserRepository(userRepository);
        writer.write("login&"+username+"&password");
        writer.newLine();
        writer.flush();
        String expected = "Token: "+token;
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void testWithFriendRepository() throws IOException {
        Socket clientSocket = new Socket("localhost", 3006);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        userRepository.setConnector(connector);
        userService.setUserRepository(userRepository);
        FriendListRepositoryImpl friendListRepository = new FriendListRepositoryImpl();
        friendListRepository.setConnector(connector);
        friendService.setFriendListRepository(friendListRepository);
        writer.write(token+"&friends");
        writer.newLine();
        writer.flush();
        String expected = "friend: ACCEPTED";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void testWithMessageRepository() throws IOException, SQLException {
        Socket clientSocket = new Socket("localhost", 3006);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        userRepository.setConnector(connector);
        userService.setUserRepository(userRepository);
        FriendListRepositoryImpl friendListRepository = new FriendListRepositoryImpl();
        friendListRepository.setConnector(connector);
        MessageRepositoryImpl messageRepository = new MessageRepositoryImpl();
        messageRepository.setConnector(connector);
        messageService.setMessageRepository(messageRepository);
        friendService.setFriendListRepository(friendListRepository);
        writer.write(token+"&chat&friend");
        writer.newLine();
        writer.flush();
        String expected = "This is the beginning of your chat with friend";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected,actual);
        writer.close();
        clientSocket.close();
    }

    static void prepareDB() throws SQLException {
        PreparedStatement preparedStatement = connector.getConnection().prepareStatement("insert into users_vk values (?,?,?)");
        preparedStatement.setInt(1, 100);
        preparedStatement.setString(2, "username");
        preparedStatement.setString(3, encodedPass);
        preparedStatement.executeUpdate();

        preparedStatement.setInt(1, 101);
        preparedStatement.setString(2, "friend");
        preparedStatement.executeUpdate();

        preparedStatement.close();

        Statement statement = connector.getConnection().createStatement();
        statement.executeUpdate("insert into friend_vk (owner, friend, status) values (100, 101, 'ACCEPTED')");
        statement.executeUpdate("insert into friend_vk (owner, friend, status) values (101, 100, 'ACCEPTED')");
        statement.close();
    }

    @AfterAll
    static void cleanDB() throws SQLException {
        Statement statement = connector.getConnection().createStatement();
        statement.executeUpdate("delete from users_vk where id = 100 or id = 101");
        statement.close();
    }
}
