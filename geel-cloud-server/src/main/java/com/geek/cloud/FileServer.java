package com.geek.cloud;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8189);       //making connection
        System.out.println("Server start...");

        while (true) {
            Socket socket = server.accept();                    //waiting client
            System.out.println("Client accepted");
            new Thread(new FileMessageHandler(socket)).start(); //read/write thread start
        }
    }
}
