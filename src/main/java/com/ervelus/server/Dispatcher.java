package com.ervelus.server;

import java.io.BufferedWriter;
import java.net.Socket;
import java.util.Map;

public interface Dispatcher {
    void dispatch(Map<String, BufferedWriter> connections, Socket socket);
}
