package com.geek.cloud.controllers;

import com.geek.cloud.network.Net;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Net net;
    public ListView<String> view;
    public TextField input;

    private void readListFiles() {
        try {
            view.getItems().clear();
            Long filesCount = net.readLong();
        for (int i = 0; i < filesCount; i++) {
            String fileName = net.readUtf();
            view.getItems().addAll(fileName);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read() {
        while (true) {
            String command = null;
            try {
                command = net.readUtf();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (command.equals("#list")) {
                Platform.runLater(this::readListFiles);
                readListFiles();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            net = new Net("localhost", 8189);
            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
