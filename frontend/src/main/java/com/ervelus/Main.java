package com.ervelus;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        try {
            Socket socket = new Socket("localhost", 8080);
            Sender sender = new Sender(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            Validator validator = new Validator();
            Terminal terminal = new Terminal(sender, scanner, validator);
            Receiver receiver = new Receiver(new BufferedReader(new InputStreamReader(socket.getInputStream())),terminal);
            Thread terminalThread = new Thread(terminal);
            Thread receiverThread = new Thread(receiver);
            terminalThread.start();
            receiverThread.start();
            terminalThread.join();
        }catch (Exception e){
            System.err.println("Failed to connect to server. Make sure it's alive");
            System.exit(0);
        }
    }
}
