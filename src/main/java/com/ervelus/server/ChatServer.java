package com.ervelus.server;

import com.ervelus.infrastructure.annotations.InjectByType;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class ChatServer implements Runnable{
    @InjectByType
    @Setter
    private ServerSocket serverSocket;
    @InjectByType
    @Setter
    private Dispatcher dispatcher;
    @InjectByType
    @Setter
    private Map<String, BufferedWriter> connections;


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
