package com.ervelus.controllers;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.security.TokenProvider;
import com.ervelus.service.FriendService;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Class contains logic of handling requests related to managing friends of user
 * handles <b>friends</b>, <b>add</b>, <b>accept</b> and <b>reject</b> requests
 * Is annotated with @Component, thus will be saved in context
 * @see Component
 */
@Component
public class FriendController {
    /**
     * Is injected automatically at the instantiation of the class
     * Used for business logic related to managing friends process
     * Setter is only used for tests
     */
    @InjectByType
    @Setter
    private FriendService friendService;
    /**
     * Is injected automatically at the instantiation of the class
     * Used for getting current user from token
     * Setter is only used for tests
     */
    @InjectByType
    @Setter
    private TokenProvider tokenProvider;

    /**
     * Handler of <b>friends</b> request
     * sends user his friend list or, if empty, inform them about it
     * @param request Incoming request
     * @param out Writer used to send response to the client
     */
    public void getFriendList(String request, BufferedWriter out){
        try {
            String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
            List<String> friends = friendService.getFriendList(username);
            StringBuilder response = new StringBuilder();
            if (friends==null || friends.isEmpty()){
                response.append("Your friend list is currently empty. Send someone a friend request!");
            }else {
                friends.forEach(friend -> response.append(friend).append("\n"));
            }
            out.write(response.toString());
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
     * Handler of <b>add</b> request
     * sends another user a friend request
     * If online, user will receive notification, otherwise will see incoming request in their friend list
     * @param request Incoming request
     * @param out Writer used to send response to the client
     */
    public void addFriend(String request, BufferedWriter out, Map<String, BufferedWriter> connections){
        try {
            String[] reqArgs = request.split("&");
            String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
            String friendName = reqArgs[2];
            out.write("Notification: ");
            if (friendService.sendFriendRequest(username, friendName)) {
                out.write("Friend request successfully sent");
                if (connections.containsKey(friendName)) {
                    connections.get(friendName).write("Notification: Incoming friend request from user " + username);
                    connections.get(friendName).newLine();
                    connections.get(friendName).flush();
                }
            }else {
                out.write("Failed to send a friend request: user not found");
            }
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
     * Handler of <b>accept</b> request
     * accepts another user's friend request
     * If online, user will receive notification, otherwise will see new friend in their friend list
     * @param request Incoming request
     * @param out Writer used to send response to the client
     */
    public void acceptFriendRequest(String request, BufferedWriter out, Map<String, BufferedWriter> connections){
        try {
            String[] reqArgs = request.split("&");
            String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
            String friendName = reqArgs[2];
            out.write("Notification: ");
            if (friendService.acceptFriendRequest(username, friendName)) {
                out.write("Friend successfully added");
                out.flush();
                if (connections.containsKey(friendName)) {
                    connections.get(friendName).write("Notification: ");
                    connections.get(friendName).write(username + " accepted your friend request");
                    connections.get(friendName).newLine();
                    connections.get(friendName).flush();
                }
            }else {
                out.write("Failed to accept friend request: request not found");
            }
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
     * Handler of <b>reject</b> request
     * rejects another user's friend request
     * If online, user will receive notification
     * Anyway, user will be removed from friend list
     * @param request Incoming request
     * @param out Writer used to send response to the client
     */
    public void rejectFriendRequest(String request, BufferedWriter out, Map<String, BufferedWriter> connections){
        try {
            String[] reqArgs = request.split("&");
            String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
            String friendName = reqArgs[2];
            out.write("Notification: ");
            if (friendService.rejectFriendRequest(username, friendName)) {
                out.write("Friend request rejected");
                if (connections.containsKey(friendName)) {
                    connections.get(friendName).write("Notification: ");
                    connections.get(friendName).write(username + " rejected your friend request");
                    connections.get(friendName).newLine();
                    connections.get(friendName).flush();
                }
            }else {
                out.write("Failed to reject a friend request: request not found");
            }
            out.newLine();
            out.flush();
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Validation error");
        }
    }
}
