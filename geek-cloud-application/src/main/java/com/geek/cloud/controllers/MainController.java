package com.geek.cloud.controllers;

import com.geek.cloud.network.Net;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private File dir;
    private String fileName;
    private Net net;
    public ListView<String> view;
    public TextField input;

    private void readListFiles() {
        try {
            view.getItems().clear();//clearing view field
            Long filesCount = net.readLong();//reading file length
            for (int i = 0; i < filesCount; i++) {
                String fileName = net.readUtf();//reading names of files
                view.getItems().addAll(fileName);//put this names to view field
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read() {
        try {
            while (true) {
                String command = net.readUtf(); //reading socket
                if (command.equals("#list#")) { //waiting command to read file names
                    readListFiles();            // files in view
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void writeFiles() {
        new Thread(() -> {
            try {
                fileName = input.getText();
                dir = new File("files");
                String[] files = dir.list();

                for (String file : files) {
                    if (fileName.equals(file)) net.writeUtf(file);
                }

                input.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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
