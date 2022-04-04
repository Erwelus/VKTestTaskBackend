package com.ervelus.server;

import java.io.BufferedWriter;
import java.net.Socket;
import java.util.Map;

/**
 * Interface for dispatching incoming requests
 */
public interface Dispatcher {
    /**
     * Main method of dispatcher, defines logic of dispatching
     * @param connections map of open connections
     * @param socket Socket for connecting with a client, who sent a request
     */
    void dispatch(Map<String, BufferedWriter> connections, Socket socket);
}
