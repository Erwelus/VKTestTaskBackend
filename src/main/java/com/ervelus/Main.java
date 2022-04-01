package com.ervelus;

import com.ervelus.infrastructure.Application;
import com.ervelus.infrastructure.ApplicationContext;
import com.ervelus.server.ChatServer;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args){
        Map<Class, Class> beanMap = new HashMap<>();
        ApplicationContext context = Application.run("com.ervelus", beanMap);
        try {
            ChatServer server = context.getObject(ChatServer.class);
            server.run();
        } catch (ReflectiveOperationException e) {
            System.err.println("Failed to start the application");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
