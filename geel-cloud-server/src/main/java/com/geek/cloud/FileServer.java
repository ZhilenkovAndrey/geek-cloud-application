package com.geek.cloud;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        Parent parent = FXMLLoader.load(getClass().getResource("server.fxml"));
//        primaryStage.setTitle("GEEK CLOUD");
//        primaryStage.setScene(new Scene(parent));
//        primaryStage.show();
//    }
}
