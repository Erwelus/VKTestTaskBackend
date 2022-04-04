package com.ervelus;


import java.io.BufferedWriter;
import java.io.IOException;

public class Sender {
    private final BufferedWriter writer;

    public Sender(BufferedWriter writer) {
        this.writer = writer;
    }

    public void send(String data){
        try {
            writer.write(data);
            writer.newLine();
            writer.flush();
        }catch (IOException e){
            System.err.println("Failed to send data to server");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
