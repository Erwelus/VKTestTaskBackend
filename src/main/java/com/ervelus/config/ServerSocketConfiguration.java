package com.ervelus.config;

import com.ervelus.infrastructure.annotations.BeanProducer;
import com.ervelus.infrastructure.annotations.Configuration;

import java.io.IOException;
import java.net.ServerSocket;

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
}
