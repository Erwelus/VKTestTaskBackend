package com.ervelus.controllers;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.FriendListEntry;
import com.ervelus.security.TokenProvider;
import com.ervelus.service.FriendService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

@Component
public class FriendController {
    @InjectByType
    private FriendService friendService;
    @InjectByType
    private TokenProvider tokenProvider;

    public void getFriendList(String request, BufferedWriter out){
        String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
        List<String> friends = friendService.getFriendNamesList(username);
        StringBuilder response = new StringBuilder();
        if (friends==null || friends.isEmpty()){
            response.append("Your friend list is currently empty. Send someone a friend request!");
        }else {
            friends.forEach(friend -> response.append(friend).append("\n"));
        }
        try {
            out.write(response.toString());
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }
    }

    public void addFriend(String request, BufferedWriter out){
        String[] reqArgs = request.split("&");
        String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
        String friendName = reqArgs[2];
        try {
            if (friendService.sendFriendRequest(username, friendName)) {
                out.write("Friend request successfully sent");
            }else out.write("Failed to send a friend request: user not found");
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }
    }

    public void acceptFriendRequest(String request, BufferedWriter out){
        String[] reqArgs = request.split("&");
        String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
        String friendName = reqArgs[2];
        try {
            if (friendService.acceptFriendRequest(username, friendName)) {
                out.write("Friend successfully added");
            }else out.write("Failed to accept friend request: user not found");
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }
    }

    public void rejectFriendRequest(String request, BufferedWriter out){
        String[] reqArgs = request.split("&");
        String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
        String friendName = reqArgs[2];
        try {
            if (friendService.rejectFriendRequest(username, friendName)) {
                out.write("Friend request rejected");
            }else out.write("Failed to reject a friend request: user not found");
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }
    }
}
