package com.ervelus.controllers;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.User;
import com.ervelus.security.TokenProvider;
import com.ervelus.service.UserService;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Class contains logic of handling requests related to authentication
 * handles <b>login</b>, <b>register</b> and <b>exit</b> requests
 * Is annotated with @Component, thus will be saved in context
 * @see Component
 */
@Component
public class AuthenticationController {
    /**
     * Is injected automatically at the instantiation of the class
     * Used for business logic related to authentication process
     * Setter is only used for tests
     */
    @InjectByType
    @Setter
    private UserService userService;
    /**
     * Is injected automatically at the instantiation of the class
     * Used for all operations with tokens
     * Setter is only used for tests
     */
    @InjectByType
    @Setter
    private TokenProvider tokenProvider;

    /**
     * Handler of <b>login</b> request
     * If credentials are correct, sends client his token and puts the client into connection map
     * Otherwise, informs either about incorrect username or password
     * @param request Incoming login request
     * @param out Writer used to send response to the client
     * @param connections Map storing active connections, correct user will be inserted into it
     */
    public void login(String request, BufferedWriter out, Map<String, BufferedWriter> connections){
        try {
            String[] reqArgs = request.split("&");
            String username = reqArgs[1];
            String password = reqArgs[2];
            User user = userService.findByUsername(username);
            if (user != null) {
                if (userService.validateCredentials(username, password, user)) {
                    System.out.println("---User "+username+" logged in---");
                    connections.put(username, out);
                    out.write("Token: "+tokenProvider.createToken(username));
                } else out.write("Wrong password");
            } else out.write("User not found");
            out.newLine();
            out.flush();
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Validation error");
        }
    }

    /**
     * Handler of <b>register</b> request
     * If there is no user with such username, user will be registered
     * and get his token
     * Otherwise, user will be informed
     * @param request Incoming register request
     * @param out Writer used to send response to the client
     * @param connections Map storing active connections, correct user will be inserted into it
     */
    public void register(String request, BufferedWriter out, Map<String, BufferedWriter> connections){
        try {
            String[] reqArgs = request.split("&");
            String username = reqArgs[1];
            String password = reqArgs[2];
            User user = userService.findByUsername(username);
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
        }catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Validation error");
        }
    }

    /**
     * Handler of <b>exit</b> request
     * After user disconnected, we should remove them from map of active connections
     * They will be able to get all new data on next login
     * @param request Incoming exit request
     * @param connections Map storing active connections, user will be deleted out of it
     */
    public void exit(String request, Map<String, BufferedWriter> connections){
        String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
        System.out.println("---User "+username+" disconnected---");
        connections.remove(username);
    }
}
