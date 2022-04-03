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
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

@Component
public class RequestDispatcher implements Dispatcher {
    @InjectByType
    @Setter
    private SecurityFilter securityFilter;
    @InjectByType
    @Setter
    private MessageController messageController;
    @InjectByType
    @Setter
    private FriendController friendController;
    @InjectByType
    @Setter
    private AuthenticationController authenticationController;
    @InjectByType
    @Setter
    private Resolver commandResolver;
    @InjectByType
    @Setter
    private Validator validator;


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
