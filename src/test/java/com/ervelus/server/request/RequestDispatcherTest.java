package com.ervelus.server.request;

import com.ervelus.controllers.AuthenticationController;
import com.ervelus.controllers.FriendController;
import com.ervelus.controllers.MessageController;
import com.ervelus.security.SecurityFilter;
import com.ervelus.server.Resolver;
import com.ervelus.server.Validator;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class RequestDispatcherTest {
    static RequestDispatcher dispatcher;
    static ServerSocket serverSocket;
    static Socket socket;

    @BeforeAll
    static void init() throws IOException {
        dispatcher = new RequestDispatcher();
        serverSocket = new ServerSocket(3000);
    }

    @AfterAll
    static void finish() throws IOException {
        serverSocket.close();
        socket.close();
    }

    @BeforeEach
    void setUp() {
        Map<String, BufferedWriter> connections = new HashMap<>();
        Thread thread = new Thread(() -> {
            try {
                socket = serverSocket.accept();
                dispatcher.dispatch(connections, socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @Test
    void dispatchLogin() throws IOException {
        Socket clientSocket = new Socket("localhost", 3000);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("login&user&pass")).thenReturn("login");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("login&user&pass", "login")).thenReturn(true);
        AuthenticationController authController = Mockito.mock(AuthenticationController.class);
        dispatcher.setCommandResolver(commandResolver);
        dispatcher.setAuthenticationController(authController);
        dispatcher.setValidator(requestValidator);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String expected = "login called";
        Mockito.doAnswer(invocationOnMock -> {
            serverWriter.write("login called");
            serverWriter.newLine();
            serverWriter.flush();
            return null;
        }).when(authController).login(any(), any(), any());
        writer.write("login&user&pass");
        writer.newLine();
        writer.flush();
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void dispatchExit() throws IOException {
        Socket clientSocket = new Socket("localhost", 3000);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("token&exit")).thenReturn("exit");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("token&exit", "exit")).thenReturn(true);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        Mockito.when(securityFilter.doFilter("token&exit")).thenReturn(true);
        AuthenticationController authController = Mockito.mock(AuthenticationController.class);
        dispatcher.setSecurityFilter(securityFilter);
        dispatcher.setCommandResolver(commandResolver);
        dispatcher.setAuthenticationController(authController);
        dispatcher.setValidator(requestValidator);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String expected = "exit called";
        Mockito.doAnswer(invocationOnMock -> {
            serverWriter.write("exit called");
            serverWriter.newLine();
            serverWriter.flush();
            return null;
        }).when(authController).exit(any(), any());
        writer.write("token&exit");
        writer.newLine();
        writer.flush();
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void dispatchRegister() throws IOException {
        Socket clientSocket = new Socket("localhost", 3000);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("register&user&pass")).thenReturn("register");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("register&user&pass", "register")).thenReturn(true);
        AuthenticationController authController = Mockito.mock(AuthenticationController.class);
        dispatcher.setCommandResolver(commandResolver);
        dispatcher.setAuthenticationController(authController);
        dispatcher.setValidator(requestValidator);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String expected = "register called";
        Mockito.doAnswer(invocationOnMock -> {
            serverWriter.write("register called");
            serverWriter.newLine();
            serverWriter.flush();
            return null;
        }).when(authController).register(any(), any(), any());

        writer.write("register&user&pass");
        writer.newLine();
        writer.flush();
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void dispatchFriends() throws IOException {
        Socket clientSocket = new Socket("localhost", 3000);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("token&friends")).thenReturn("friends");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("token&friends", "friends")).thenReturn(true);
        FriendController friendController = Mockito.mock(FriendController.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        Mockito.when(securityFilter.doFilter("token&friends")).thenReturn(true);
        dispatcher.setCommandResolver(commandResolver);
        dispatcher.setFriendController(friendController);
        dispatcher.setValidator(requestValidator);
        dispatcher.setSecurityFilter(securityFilter);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String expected = "friends called";
        Mockito.doAnswer(invocationOnMock -> {
            serverWriter.write("friends called");
            serverWriter.newLine();
            serverWriter.flush();
            return null;
        }).when(friendController).getFriendList(any(), any());

        writer.write("token&friends");
        writer.newLine();
        writer.flush();
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void dispatchAdd() throws IOException {
        Socket clientSocket = new Socket("localhost", 3000);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("token&add&friend")).thenReturn("add");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("token&add&friend", "add")).thenReturn(true);
        FriendController friendController = Mockito.mock(FriendController.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        Mockito.when(securityFilter.doFilter("token&add&friend")).thenReturn(true);
        dispatcher.setCommandResolver(commandResolver);
        dispatcher.setFriendController(friendController);
        dispatcher.setValidator(requestValidator);
        dispatcher.setSecurityFilter(securityFilter);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String expected = "add called";
        Mockito.doAnswer(invocationOnMock -> {
            serverWriter.write("add called");
            serverWriter.newLine();
            serverWriter.flush();
            return null;
        }).when(friendController).addFriend(any(), any(), any());

        writer.write("token&add&friend");
        writer.newLine();
        writer.flush();
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void dispatchAccept() throws IOException {
        Socket clientSocket = new Socket("localhost", 3000);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("token&accept&friend")).thenReturn("accept");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("token&accept&friend", "accept")).thenReturn(true);
        FriendController friendController = Mockito.mock(FriendController.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        Mockito.when(securityFilter.doFilter("token&accept&friend")).thenReturn(true);
        dispatcher.setCommandResolver(commandResolver);
        dispatcher.setFriendController(friendController);
        dispatcher.setValidator(requestValidator);
        dispatcher.setSecurityFilter(securityFilter);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String expected = "accept called";
        Mockito.doAnswer(invocationOnMock -> {
            serverWriter.write("accept called");
            serverWriter.newLine();
            serverWriter.flush();
            return null;
        }).when(friendController).acceptFriendRequest(any(), any(), any());

        writer.write("token&accept&friend");
        writer.newLine();
        writer.flush();
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void dispatchReject() throws IOException {
        Socket clientSocket = new Socket("localhost", 3000);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("token&reject&friend")).thenReturn("reject");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("token&reject&friend", "reject")).thenReturn(true);
        FriendController friendController = Mockito.mock(FriendController.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        Mockito.when(securityFilter.doFilter("token&reject&friend")).thenReturn(true);
        dispatcher.setCommandResolver(commandResolver);
        dispatcher.setFriendController(friendController);
        dispatcher.setValidator(requestValidator);
        dispatcher.setSecurityFilter(securityFilter);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String expected = "reject called";
        Mockito.doAnswer(invocationOnMock -> {
            serverWriter.write("reject called");
            serverWriter.newLine();
            serverWriter.flush();
            return null;
        }).when(friendController).rejectFriendRequest(any(), any(), any());

        writer.write("token&reject&friend");
        writer.newLine();
        writer.flush();
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void dispatchChat() throws IOException {
        Socket clientSocket = new Socket("localhost", 3000);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("token&chat&friend")).thenReturn("chat");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("token&chat&friend", "chat")).thenReturn(true);
        MessageController messageController = Mockito.mock(MessageController.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        Mockito.when(securityFilter.doFilter("token&chat&friend")).thenReturn(true);
        dispatcher.setCommandResolver(commandResolver);
        dispatcher.setMessageController(messageController);
        dispatcher.setValidator(requestValidator);
        dispatcher.setSecurityFilter(securityFilter);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String expected = "chat called";
        Mockito.doAnswer(invocationOnMock -> {
            serverWriter.write("chat called");
            serverWriter.newLine();
            serverWriter.flush();
            return null;
        }).when(messageController).getDialog(any(), any());

        writer.write("token&chat&friend");
        writer.newLine();
        writer.flush();
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void dispatchSend() throws IOException {
        Socket clientSocket = new Socket("localhost", 3000);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("token&send&friend&text")).thenReturn("send");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("token&send&friend&text", "send")).thenReturn(true);
        MessageController messageController = Mockito.mock(MessageController.class);
        SecurityFilter securityFilter = Mockito.mock(SecurityFilter.class);
        Mockito.when(securityFilter.doFilter("token&send&friend&text")).thenReturn(true);
        dispatcher.setCommandResolver(commandResolver);
        dispatcher.setMessageController(messageController);
        dispatcher.setValidator(requestValidator);
        dispatcher.setSecurityFilter(securityFilter);

        BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String expected = "send called";
        Mockito.doAnswer(invocationOnMock -> {
            serverWriter.write("send called");
            serverWriter.newLine();
            serverWriter.flush();
            return null;
        }).when(messageController).send(any(), any(), any());

        writer.write("token&send&friend&text");
        writer.newLine();
        writer.flush();
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }
}
