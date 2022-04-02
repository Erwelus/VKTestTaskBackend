package com.ervelus.server;

import com.ervelus.infrastructure.annotations.Component;

@Component
public class CommandValidator {
    public boolean validate(String request, String command){
        String[] commandWithArgs = request.split("&");
        switch (command){
            case "login":
            case "send":
            case "register": {
                return commandWithArgs.length == 3;
            }
            case "exit":
            case "friends": {
                return commandWithArgs.length == 1;
            }
            case "add":
            case "accept":
            case "chat":
            case "reject": {
                return commandWithArgs.length == 2;
            }
        }
        return false;
    }
}
