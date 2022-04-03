package com.ervelus.controllers;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.infrastructure.annotations.InjectByType;
import com.ervelus.model.Message;
import com.ervelus.security.TokenProvider;
import com.ervelus.service.ChatService;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class MessageController {
    @InjectByType
    @Setter
    private ChatService chatService;
    @InjectByType
    @Setter
    private TokenProvider tokenProvider;

    public void send(String request, BufferedWriter out, Map<String, BufferedWriter> connections){
        try {
            String[] reqArgs = request.split("&");
            String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
            String friendName = reqArgs[2];
            String text = reqArgs[3];
            if (chatService.sendMessageToFriend(username, friendName, text)) {
                out.write(username+" wrote: "+text);
                if (connections.containsKey(friendName)) {
                    connections.get(friendName).write(username + " wrote: " + text);
                    connections.get(friendName).newLine();
                    connections.get(friendName).flush();
                }
            }else {
                out.write("Failed to send a message: friend not found");
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

    public void getDialog(String request, BufferedWriter out){
        try {
            String[] reqArgs = request.split("&");
            String username = tokenProvider.getUsernameFromToken(tokenProvider.resolveToken(request));
            String friendName = reqArgs[2];
            List<Message> messages = chatService.getChatWithFriend(username, friendName);
            StringBuilder response = new StringBuilder();
            if(messages == null){
                response.append("Friend not found");
            }else if (messages.isEmpty()) {
                response.append("This is the beginning of your chat with ").append(friendName);
            }else {
                messages.forEach(message -> response.append(message.getUserFrom().getUsername())
                        .append(": ").append(message.getContent()).append("\n"));
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
}
