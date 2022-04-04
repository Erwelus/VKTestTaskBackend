package com.ervelus.integration;

import com.ervelus.controllers.AuthenticationController;
import com.ervelus.server.ChatServer;
import com.ervelus.server.Resolver;
import com.ervelus.server.Validator;
import com.ervelus.server.request.CommandResolver;
import com.ervelus.server.request.RequestDispatcher;
import com.ervelus.server.request.RequestValidator;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.ArgumentMatchers.any;

public class ServerIntegrationTest {
    static ChatServer server;

    @BeforeAll
    static void setUp() throws IOException {
        ServerSocket serverSocket = new ServerSocket(3002);
        Thread thread = new Thread(() -> {
            try {
                server = new ChatServer();
                server.setServerSocket(serverSocket);
                server.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @Test
    void testWithDispatcher() throws IOException {
        Socket clientSocket = new Socket("localhost", 3002);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        RequestDispatcher dispatcher = new RequestDispatcher();
        server.setDispatcher(dispatcher);
        Resolver commandResolver = Mockito.mock(Resolver.class);
        Mockito.when(commandResolver.resolve("login&user&pass")).thenReturn("login");
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("login&user&pass", "login")).thenReturn(true);
        AuthenticationController authController = Mockito.mock(AuthenticationController.class);
        dispatcher.setResolver(commandResolver);
        dispatcher.setAuthenticationController(authController);
        dispatcher.setValidator(requestValidator);
        Mockito.doAnswer(invocationOnMock -> {
            dispatcher.getOut().write("login_called");
            dispatcher.getOut().newLine();
            dispatcher.getOut().flush();
            return null;
        }).when(authController).login(any(), any(), any());
        writer.write("login&user&pass");
        writer.newLine();
        writer.flush();
        String expected = "login_called";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void testWithResolver() throws IOException {
        Socket clientSocket = new Socket("localhost", 3002);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        RequestDispatcher dispatcher = new RequestDispatcher();
        server.setDispatcher(dispatcher);
        Resolver commandResolver = new CommandResolver();
        Validator requestValidator = Mockito.mock(Validator.class);
        Mockito.when(requestValidator.validate("login&user&pass", "login")).thenReturn(true);
        AuthenticationController authController = Mockito.mock(AuthenticationController.class);
        dispatcher.setResolver(commandResolver);
        dispatcher.setAuthenticationController(authController);
        dispatcher.setValidator(requestValidator);
        Mockito.doAnswer(invocationOnMock -> {
            dispatcher.getOut().write("login_called");
            dispatcher.getOut().newLine();
            dispatcher.getOut().flush();
            return null;
        }).when(authController).login(any(), any(), any());
        writer.write("login&user&pass");
        writer.newLine();
        writer.flush();
        String expected = "login_called";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }

    @Test
    void testWithValidator() throws IOException {
        Socket clientSocket = new Socket("localhost", 3002);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        RequestDispatcher dispatcher = new RequestDispatcher();
        server.setDispatcher(dispatcher);
        Resolver commandResolver = new CommandResolver();
        Validator requestValidator = new RequestValidator();
        AuthenticationController authController = Mockito.mock(AuthenticationController.class);
        dispatcher.setResolver(commandResolver);
        dispatcher.setAuthenticationController(authController);
        dispatcher.setValidator(requestValidator);
        Mockito.doAnswer(invocationOnMock -> {
            dispatcher.getOut().write("login_called");
            dispatcher.getOut().newLine();
            dispatcher.getOut().flush();
            return null;
        }).when(authController).login(any(), any(), any());
        writer.write("login&user&pass");
        writer.newLine();
        writer.flush();
        String expected = "login_called";
        String actual = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
        Assertions.assertEquals(expected, actual);
        writer.close();
        clientSocket.close();
    }
}
