package com.ervelus.config;

import com.ervelus.infrastructure.annotations.BeanProducer;
import com.ervelus.infrastructure.annotations.Configuration;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class for the project, includes bean definitions
 * All beans that are used in server logic should be declared here using @BeanProducer
 * Class is annotated with @Configuration, thus will be instantiated at the application start
 * @see Configuration
 */
@Configuration
public class ServerSocketConfiguration {
    /**
     * Bean definition for the ServerSocket that is used as main server
     * Method is called at the start of the application,
     * then Server Socket instance will be injected into ChatServer when instantiated
     * @return instance of Server Socket that will be used for injection
     * @see BeanProducer
     */
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

    /**
     * Bean definition of the map for storing active connections
     * The connections are used for real-time interaction with online users
     * Method is called at the start of the application,
     * the result will be injected after instantiation of ChatServer
     * @return instance of connection map that will be used for injection
     * @see BeanProducer
     */
    @BeanProducer
    public Map<String, BufferedWriter> getConnectionMap(){
        return new ConcurrentHashMap<>();
    }
}
