package com.ervelus.server;

import com.ervelus.infrastructure.annotations.Component;

@Component
public class CommandResolver {
    public String resolve(String request){
        if (request.startsWith("login") || request.startsWith("register") || request.startsWith("exit")){
            return request.split("&")[0];
        }else return request.split("&")[1];
    }
}
