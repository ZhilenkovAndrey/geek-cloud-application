package com.geek.cloud.controllers;

import com.geek.cloud.model.*;
import com.geek.cloud.network.Net;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
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
    @FXML
    private ComboBox<String> clientDisks;
    @FXML
    private ComboBox<String> serverDisks;
    @FXML
    private TextField pathToFileClient;
    @FXML
    private TextField pathToFileServer;

    private void read() {
        pathToFileServer.setText(serverDir.normalize().toAbsolutePath().toString());

        serverDisks.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            serverDisks.getItems().add(p.toString());
        }
        serverDisks.getSelectionModel().select(0);

        try {
            while (true) {
                AbstractMessage message = net.read();
                if ((message instanceof ListMessage) ||
                    (message instanceof CdDirectory) ||
                    (message instanceof DeleteMessageFromServer)) {
                        serverView.getItems().clear();
                        serverView.getItems().addAll(
                                (new ListMessage(serverDir)).getFiles());
                        serverView.getItems().sort(String::compareTo);
                }

                if ((message instanceof DownloadMessage) ||
                    (message instanceof DeleteMessageFromClient)){
                        clientView.getItems().clear();
                        clientView.getItems().addAll(getClientFiles());
                        clientView.getItems().sort(String::compareTo);
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
            pathToFileClient.setText(clientDir.normalize().toAbsolutePath().toString());
            clientView.getItems().clear();
            clientView.getItems().addAll(getClientFiles());
            clientView.getItems().sort(String::compareTo);

            net = new Net("localhost", 8189);

            clientDisks.getItems().clear();
            for (Path p : FileSystems.getDefault().getRootDirectories()) {
                clientDisks.getItems().add(p.toString());
            }
            clientDisks.getSelectionModel().select(0);

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
        net.write(new DownloadMessage(
                serverView.getSelectionModel().getSelectedItem()));
    }

    public void deleteMessageFromServer(ActionEvent actionEvent) throws IOException {
        net.write(new DeleteMessageFromServer(
                serverView.getSelectionModel().getSelectedItem()));
    }

    public void deleteMessageFromClient(ActionEvent actionEvent) throws IOException {
        net.write(new DeleteMessageFromClient(
                clientView.getSelectionModel().getSelectedItem()));
    }

    public void cdDirectory(ActionEvent actionEvent) throws IOException {
        net.write(new CdDirectory(clientDir));
    }

    public void exitButton(ActionEvent actionEvent) throws IOException {
        Platform.exit();
    }
}