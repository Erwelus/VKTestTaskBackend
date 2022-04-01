package com.ervelus.config;

import com.ervelus.infrastructure.annotations.BeanProducer;
import com.ervelus.infrastructure.annotations.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ServerSocketConfiguration {
    @BeanProducer
    public ServerSocket getServerSocket(){
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            System.err.println("Failed to create a ServerSocket: port already in use");
            System.exit(0);
        }
        return serverSocket;
    }

    @BeanProducer
    public Map<String, Socket> getConnectionMap(){
        return new ConcurrentHashMap<>();
    }
}
