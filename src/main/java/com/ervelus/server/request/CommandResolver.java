package com.ervelus.server.request;

import com.ervelus.infrastructure.annotations.Component;

@Component
public class CommandResolver {
    public String resolve(String request){
        if (request.toLowerCase().startsWith("login") ||
                request.toLowerCase().startsWith("register") ||
                request.toLowerCase().startsWith("exit")){
            return request.split("&")[0].toLowerCase();
        }else return request.split("&")[1].toLowerCase();
    }
}
