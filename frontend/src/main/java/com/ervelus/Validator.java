package com.ervelus;

public class Validator {
    public boolean validate(String[] commandArr){
        switch (commandArr[0]){
            case "login":
            case "send":
            case "register": {
                return commandArr.length == 3;
            }
            case "exit":
            case "friends": {
                return commandArr.length == 1;
            }
            case "add":
            case "accept":
            case "chat":
            case "reject": {
                return commandArr.length == 2;
            }
        }
        return false;
    }
}
