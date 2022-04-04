package com.ervelus.server.request;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.server.Resolver;

/**
 * Resolver for commands in the incoming requests
 */
@Component
public class CommandResolver implements Resolver {
    /**
     * Returns the command, based on basic syntax of request and specific commands
     * @param request incoming request
     */
    public String resolve(String request){
        if (request.toLowerCase().startsWith("login") ||
                request.toLowerCase().startsWith("register") ||
                request.toLowerCase().startsWith("exit")){
            return request.split("&")[0].toLowerCase();
        }else return request.split("&")[1].toLowerCase();
    }
}
