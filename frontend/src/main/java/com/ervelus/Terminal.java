package com.ervelus;

import java.util.Scanner;

public class Terminal implements Runnable{
    private final Sender sender;
    private final Scanner scanner;
    private final Validator validator;
    private String token = "";
    private String currentFriend = "";
    private String myUsername = "";

    public Terminal(Sender sender, Scanner scanner, Validator validator) {
        this.sender = sender;
        this.scanner = scanner;
        this.validator = validator;
    }

    @Override
    public void run() {
        while (true){
            System.out.print(">");
            String userCommand = scanner.nextLine();
            String[] userCommandArr = userCommand.trim().split(" ", 3);
            if (validator.validate(userCommandArr)) {
                switch (userCommandArr[0]) {
                    case "login":
                    case "register": {
                        myUsername = userCommandArr[1];
                        sender.send(userCommandArr[0]+"&"+ userCommandArr[1]+"&"+ userCommandArr[2]);
                        break;
                    }
                    case "exit": {
                        if (!token.isEmpty()){
                            sender.send(token+"&"+ userCommandArr[0]);
                        }else System.exit(0);
                        break;
                    }
                    case "send": {
                        if (!token.isEmpty()){
                            sender.send(token+"&"+ userCommandArr[0]+"&"+ userCommandArr[1]+"&"+ userCommandArr[2]);
                        }else {
                            System.out.println("You have to log in to perform this command");
                        }
                        break;
                    }
                    case "friends": {
                        if (!token.isEmpty()){
                            sender.send(token+"&"+ userCommandArr[0]);
                        }else {
                            System.out.println("You have to log in to perform this command");
                        }
                        break;
                    }
                    case "chat": {
                        if (!token.isEmpty()){
                            currentFriend = userCommandArr[1];
                            sender.send(token+"&"+ userCommandArr[0]+"&"+ userCommandArr[1]);
                        }else {
                            System.out.println("You have to log in to perform this command");
                        }
                        break;
                    }
                    default: {
                        if (!token.isEmpty()){
                            sender.send(token+"&"+ userCommandArr[0]+"&"+ userCommandArr[1]);
                        }else {
                            System.out.println("You have to log in to perform this command");
                        }
                        break;
                    }
                }
            }else {
                System.out.println("Incorrect command syntax");
            }
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCurrentFriend() {
        return currentFriend;
    }
    public String getMyUsername() {
        return myUsername;
    }
}
