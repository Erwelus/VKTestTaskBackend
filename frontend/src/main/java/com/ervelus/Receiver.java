package com.ervelus;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver implements Runnable{
    private final BufferedReader reader;
    private final Terminal terminal;

    public Receiver(BufferedReader reader, Terminal terminal) {
        this.reader = reader;
        this.terminal = terminal;
    }

    public void receive(){
        try {
            String input = reader.readLine();
            if (input==null) System.exit(0);
            if (input.startsWith("Token")){
                terminal.setToken(input.split(" ")[1].trim());
                System.out.print("\nLogged in successfully\n>");
            }else if (input.startsWith("Notification")){
                System.out.print("\n" + input + "\n>");
            }else if (input.contains("wrote") &&
                    !input.startsWith(terminal.getCurrentFriend()) &&
                    !input.startsWith(terminal.getMyUsername())){
                System.out.println("\nNotification: "+input);
            }else{
                System.out.println(input);
            }
        } catch (IOException e) {
            System.err.println("Failed to get data from server");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        while (true){
            receive();
        }
    }
}
