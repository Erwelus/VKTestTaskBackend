package com.ervelus.server.request;

import com.ervelus.controllers.AuthenticationController;
import com.ervelus.controllers.FriendController;
import com.ervelus.controllers.MessageController;
import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.security.SecurityFilter;
import com.ervelus.server.Dispatcher;
import com.ervelus.server.Resolver;
import com.ervelus.server.Validator;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

/**
 * Default implementation of Dispatcher.
 * Performs dispatching of the incoming requests and invokes corresponding methods of controllers
 */
@Component
public class RequestDispatcher implements Dispatcher {
    /**
     * Filter for requests, used for authentication.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private SecurityFilter securityFilter;
    /**
     * Controller for requests related to chatting.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private MessageController messageController;
    /**
     * Controller for requests related to friend managing.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private FriendController friendController;
    /**
     * Controller for requests related to authentication.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private AuthenticationController authenticationController;
    /**
     * Resolver for incoming requests.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private Resolver resolver;
    /**
     * Validator for incoming requests.
     * Setter is only used for tests, is automatically injected
     */
    @InjectByType
    @Setter
    private Validator validator;

    /**
     * Getter is only used for tests
     */
    @Getter
    private BufferedWriter out;
    /**
     * Getter is only used for tests
     */
    @Getter
    private BufferedReader in;

    /**
     * Dispatching incoming requests.
     * Invalid requests are ignored.
     * Valid requests are processed by corresponding controller.
     * Works, while client is connected.
     * SocketException is thrown when client incorrectly disconnected (not by using exit, lost connection e.g.), thus ignored
     * @param connections map of open connections
     * @param socket Socket for connecting with a client, who sent a request
     */
    public void dispatch(Map<String, BufferedWriter> connections, Socket socket){
        try {
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request;
            while ((request = in.readLine()) != null){
                System.out.println("---New request incoming---");
                String command = resolver.resolve(request);
                if (!validator.validate(request, command)){
                    out.write("Incorrect use of command. Client-side error");
                    out.newLine();
                    out.flush();
                    continue;
                }
                switch (command) {
                    case "login": {
                        authenticationController.login(request, out, connections);
                        break;
                    }
                    case "register": {
                        authenticationController.register(request, out, connections);
                        break;
                    }
                    case "friends": {
                        if (securityFilter.doFilter(request)){
                            friendController.getFriendList(request, out);
                        }
                        break;
                    }
                    case "add": {
                        if (securityFilter.doFilter(request)){
                            friendController.addFriend(request, out, connections);
                        }
                        break;
                    }
                    case "accept": {
                        if (securityFilter.doFilter(request)){
                            friendController.acceptFriendRequest(request, out, connections);
                        }
                        break;
                    }
                    case "reject": {
                        if (securityFilter.doFilter(request)){
                            friendController.rejectFriendRequest(request, out, connections);
                        }
                        break;
                    }
                    case "chat": {
                        if (securityFilter.doFilter(request)){
                            messageController.getDialog(request, out);
                        }
                        break;
                    }
                    case "send": {
                        if (securityFilter.doFilter(request)){
                            messageController.send(request, out, connections);
                        }
                        break;
                    }
                    case "exit": {
                        if (securityFilter.doFilter(request)){
                            authenticationController.exit(request, connections);
                        }
                        in.close();
                        out.close();
                        socket.close();
                        return;
                    }
                }
            }
        } catch (SocketException ignored) {
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
