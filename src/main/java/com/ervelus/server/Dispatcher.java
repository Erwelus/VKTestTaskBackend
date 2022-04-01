package com.ervelus.server;

import com.ervelus.controllers.AuthenticationController;
import com.ervelus.controllers.FriendController;
import com.ervelus.controllers.MessageController;
import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.security.SecurityFilter;

import java.io.*;
import java.net.Socket;
import java.util.Map;

@Component
public class Dispatcher {
    @InjectByType
    private SecurityFilter securityFilter;
    @InjectByType
    private MessageController messageController;
    @InjectByType
    private FriendController friendController;
    @InjectByType
    private AuthenticationController authenticationController;
    @InjectByType
    private CommandResolver commandResolver;


    public void dispatch(Map<String, Socket> connections, Socket socket){
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request;
            while ((request = in.readLine()) != null){
                String command = commandResolver.resolve(request);
                switch (command) {
                    case "login": {
                        authenticationController.login(request, out);
                        break;
                    }
                    case "register": {
                        authenticationController.register(request, out);
                        break;
                    }
                    case "friends": {
                        if (securityFilter.doFilter(request)){
                            friendController.getFriendList(request, out);
                        }
                    }
                    case "add": {
                        if (securityFilter.doFilter(request)){
                            friendController.addFriend(request, out);
                        }
                    }
                    case "accept": {
                        if (securityFilter.doFilter(request)){
                            friendController.acceptFriendRequest(request, out);
                        }
                    }
                    case "reject": {
                        if (securityFilter.doFilter(request)){
                            friendController.rejectFriendRequest(request, out);
                        }
                    }
                    case "chat": {
                        if (securityFilter.doFilter(request)){
                            messageController.getDialog(request, out);
                        }
                    }
                    case "send": {
                        if (securityFilter.doFilter(request)){
                            messageController.send(request, out);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
