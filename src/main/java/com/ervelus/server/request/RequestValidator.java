package com.ervelus.server.request;

import com.ervelus.infrastructure.annotations.Component;
import com.ervelus.server.Validator;

import java.util.Objects;

/**
 * Default implementation of validator
 */
@Component
public class RequestValidator implements Validator {
    /**
     * Validates request based on used separator, number of arguments and position of the command
     * @param request incoming request
     * @param command command that this request contains
     */
    public boolean validate(String request, String command){
        String[] commandWithArgs = request.split("&");
        switch (command){
            case "login":
            case "register": {
                return commandWithArgs.length == 3 && Objects.equals(commandWithArgs[0], command);
            }
            case "exit":
            case "friends": {
                return commandWithArgs.length == 2 && Objects.equals(commandWithArgs[1], command);
            }
            case "add":
            case "accept":
            case "chat":
            case "reject": {
                return commandWithArgs.length == 3 && Objects.equals(commandWithArgs[1], command);
            }
            case "send": {
                return commandWithArgs.length == 4 && Objects.equals(commandWithArgs[1], command);
            }
        }
        return false;
    }
}
