package com.ervelus.server;

import com.ervelus.infrastructure.annotations.InjectByType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class ChatServer implements Runnable{
    @InjectByType
    private ServerSocket serverSocket;
    @InjectByType
    private Dispatcher dispatcher;
    @InjectByType
    private Map<String, Socket> connections;


    @Override
    public void run() {
        System.out.println("Server successfully started on port "+serverSocket.getLocalPort());
        while (true){
            try {
                Socket socket = serverSocket.accept();
                Thread t = new Thread(() -> dispatcher.dispatch(connections, socket));
                t.start();
            } catch (IOException e) {
                System.err.println("Socket failed to connect, required changes in Server Socket configuration");
                e.printStackTrace();
                System.exit(1);
            }

        }
    }
}
