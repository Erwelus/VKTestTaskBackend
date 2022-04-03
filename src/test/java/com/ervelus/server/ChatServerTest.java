package com.ervelus.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;

class ChatServerTest {
    ChatServer server;

    @BeforeEach
    void setUp() throws IOException {
        ServerSocket serverSocket = new ServerSocket(3001);
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
    void run() throws IOException {
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        Mockito.doNothing().when(dispatcher).dispatch(any(), any());
        server.setDispatcher(dispatcher);
        Socket clientSocket = new Socket("localhost", 3001);
        Assertions.assertTrue(clientSocket.isConnected());
    }
}