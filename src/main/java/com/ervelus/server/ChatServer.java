package com.ervelus.server;

import com.ervelus.infrastructure.annotations.InjectByType;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Main server of the application
 */
public class ChatServer implements Runnable{
    /**
     * ServerSocket where the server works.
     * Setter is only used for tests, in automatically injected
     */
    @InjectByType
    @Setter
    private ServerSocket serverSocket;
    /**
     * Dispatcher of incoming requests.
     * Setter is only used for tests, in automatically injected
     */
    @InjectByType
    @Setter
    private Dispatcher dispatcher;
    /**
     * Map, containing all open connections for online interaction.
     * Should be concurrent.
     * Setter is only used for tests, in automatically injected
     */
    @InjectByType
    @Setter
    private Map<String, BufferedWriter> connections;

    /**
     * Running the server. Accepts incoming requests, starts new thread for working with each user to not make others wait.
     * IOException occurs when port is already in use, port is set in Configuration of the application
     */
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
