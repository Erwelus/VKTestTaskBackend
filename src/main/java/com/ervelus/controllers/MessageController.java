package com.ervelus.controllers;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.security.TokenProvider;
import com.ervelus.service.ChatService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class MessageController {
    @InjectByType
    private ChatService chatService;
    @InjectByType
    private TokenProvider tokenProvider;

    public void send(String request, BufferedWriter out, Map<String, BufferedWriter> connections){
        String[] reqArgs = request.split("&");
        String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
        String friendName = reqArgs[2];
        String text = reqArgs[3];
        try {
            if (chatService.sendMessageToFriend(username, friendName, text)) {
                out.write(username+": "+text);
                connections.get(friendName).write(username+": "+text);
            }else out.write("Failed to send a message: user not found");
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }
    }

    public void getDialog(String request, BufferedWriter out){
        String[] reqArgs = request.split("&");
        String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
        String friendName = reqArgs[2];
        List<Message> messages = chatService.getChatWithFriend(username, friendName);
        StringBuilder response = new StringBuilder();
        if (messages==null || messages.isEmpty()){
            response.append("This is the beginning of your chat with ").append(friendName);
        }else {
            messages.forEach(message -> response.append(message.getUserFrom())
                    .append(": ").append(message.getContent()).append("\n"));
        }
        try {
            out.write(response.toString());
        }catch (IOException e){
            System.err.println("Failed to send response to client");
            e.printStackTrace();
        }
    }
}
