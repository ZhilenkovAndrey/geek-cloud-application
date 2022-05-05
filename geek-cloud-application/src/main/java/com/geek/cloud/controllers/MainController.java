package com.geek.cloud.controllers;

import com.geek.cloud.model.*;
import com.geek.cloud.network.Net;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Path clientDir;
    private Path serverDir = Path.of("serverFiles");
    public ListView<String> clientView;
    public ListView<String> serverView;
    private Net net;

    private void read() {
        try {
            while (true) {
                AbstractMessage message = net.read();
                if (message instanceof ListMessage lm) {
                    serverView.getItems().clear();
                    serverView.getItems().addAll(lm.getFiles());
                }

                if (message instanceof DownloadMessage) {
                    clientView.getItems().clear();
                    clientView.getItems().addAll(getClientFiles());
                }

                if (message instanceof DeleteMessageFromServer) {
                    serverView.getItems().clear();
                    serverView.getItems().addAll(new ListMessage(serverDir).getFiles());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getClientFiles() throws IOException {
        return Files.list(clientDir)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            clientDir = Path.of("clientFiles");
            clientView.getItems().clear();
            clientView.getItems().addAll(getClientFiles());
            net = new Net("localhost", 8189);
            Thread.sleep(300);
            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        net.write(new FileMessage(clientDir.resolve(fileName)));
    }

    public void download(ActionEvent actionEvent) throws Exception {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        net.write(new DownloadMessage(fileName));
    }

    public void deleteFromServer(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        net.write(new DeleteMessageFromServer(fileName));
    }
}