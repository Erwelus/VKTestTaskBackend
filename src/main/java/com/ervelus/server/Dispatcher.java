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
    @InjectByType
    private CommandValidator validator;


    public void dispatch(Map<String, BufferedWriter> connections, Socket socket){
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String request;
            while ((request = in.readLine()) != null){
                System.out.println("---New request incoming---");
                String command = commandResolver.resolve(request);
                if (!validator.validate(request, command)){
                    out.write("Incorrect use of command. Client-side error");
                    out.newLine();
                    out.flush();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
