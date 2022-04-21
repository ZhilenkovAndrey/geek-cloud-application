package com.geek.cloud.controllers;

import com.geek.cloud.network.Net;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private File dir = new File("clientFiles");;
    private String sendCommand;
    private Net net;
    public ListView<String> view;
    public TextField input;
    public TextArea downView;
    private String[] files;

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

    private void printMessage(String message) throws IOException {
        downView.clear();
        downView.setText(message);
    }

    private void read() {
        try {
            while (true) {
                String command = net.readUtf(); //reading socket
                if (command.equals("#list#")) { //waiting command to read file names
                    readListFiles();            // files in view
                } else {
                    printMessage(command);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendFiles() throws IOException {
        sendCommand = input.getText();
        files = dir.list();

        for (String fileName : files) {
            if (sendCommand.equals(fileName)) {

                net.writeUtf("file");
                net.writeUtf(fileName);
                net.writeLong(fileName.length());
                byte[] buf = new byte[1024];
                File file = dir.toPath().resolve(fileName).toFile();

                try (InputStream f = new FileInputStream(file)) {
                    while (f.available() > 0) {
                        int read = f.read(buf);
                        net.getOs().write(buf, 0, read);
                    }
                }
                input.clear();
//                net.getOs().flush();
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

            downView.setEditable(false);
            files = dir.list();

            for (String file : files) {
                downView.appendText(file + "\n");
                System.out.println(file);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
