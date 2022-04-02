package com.ervelus.controllers;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.User;
import com.ervelus.security.TokenProvider;
import com.ervelus.service.UserService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

@Component
public class AuthenticationController {
    @InjectByType
    private UserService userService;
    @InjectByType
    private TokenProvider tokenProvider;

    public void login(String request, BufferedWriter out, Map<String, BufferedWriter> connections){
        String[] reqArgs = request.split("&");
        String username = reqArgs[1];
        String password = reqArgs[2];
        User user = userService.findByUsername(username);
        try {
            if (user != null) {
                if (userService.validateCredentials(username, password, user)) {
                    System.out.println("---User "+username+" logged in---");
                    connections.put(username, out);
                    out.write("Token: "+tokenProvider.createToken(username));
                } else out.write("Incorrect password");
            } else out.write("User not found");
            out.newLine();
            out.flush();
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }
    }

    public void register(String request, BufferedWriter out, Map<String, BufferedWriter> connections){
        String[] reqArgs = request.split("&");
        String username = reqArgs[1];
        String password = reqArgs[2];
        User user = userService.findByUsername(username);
        try {
            if (user == null) {
                userService.register(new User(username, password));
                System.out.println("---User "+username+" registered---");
                connections.put(username, out);
                out.write("Token: "+tokenProvider.createToken(username));
            } else out.write("User with such username already exists");
            out.newLine();
            out.flush();
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }
    }

    public void exit(String request, Map<String, BufferedWriter> connections){
        String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
        System.out.println("---User "+username+" disconnected---");
        connections.remove(username);
    }
}
