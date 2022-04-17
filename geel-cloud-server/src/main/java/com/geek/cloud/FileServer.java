package com.geek.cloud;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.ServerCloneException;

public class FileServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8189);
        System.out.println("Server start...");
        while (true) {
            Socket socket = server.accept();
            new Thread(new FileMessageHandler(socket)).start();
        }
    }
}
