package com.geek.cloud.controllers;

import com.geek.cloud.model.*;
import com.geek.cloud.network.Net;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;


public class MainController implements Initializable {


    public ListView<String> clientView;
    public ListView<String> serverView;
    private Net net;
    @FXML
    private ComboBox<String> clientDisks;
    @FXML
    private ComboBox<String> serverDisks;
    @FXML
    public TextField pathToFileClient;
    @FXML
    public TextField pathToFileServer;

    private void read() {

        pathToFileServer.setText(Path.of("serverFiles").normalize().toAbsolutePath().toString());
        updatePathLine(serverDisks);

        try {
            openDirectory(serverView, pathToFileServer);

            while (true) {
                AbstractMessage message = net.read();

                if ((message instanceof ListMessage) ||
                    (message instanceof DeleteMessageFromServer)) {
                        updateView(serverView, pathToFileServer);
                }

                if ((message instanceof DownloadMessage) ||
                    (message instanceof FileMessage) ||
                    (message instanceof DeleteMessageFromClient)) {
                        updateView(clientView, pathToFileClient);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pathToFileClient.setText(Path.of("clientFiles").normalize().toAbsolutePath().toString());

        try {
            updatePathLine(clientDisks);
            updateView(clientView, pathToFileClient);
            openDirectory(clientView, pathToFileClient);

            net = new Net("localhost", 8189);

            Thread.sleep(300);
            Thread readThread = new Thread(this::read);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updatePathLine(ComboBox<String> line) {
        line.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            line.getItems().add(p.toString());
        }
        line.getSelectionModel().select(0);
    }

    private void updateView(ListView<String> someView, TextField pathToFile) throws IOException {
        someView.getItems().clear();
        someView.getItems().addAll((new ListMessage(Path.of(pathToFile.getText()))).getFiles());
        someView.getItems().sort(String::compareTo);
    }

    public void uploadToServer(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        net.write(new FileMessage(Path.of(pathToFileClient.getText()).resolve(fileName)));
    }

    public void downloadFromServer(ActionEvent actionEvent) throws Exception {
        net.write(new DownloadMessage(
                serverView.getSelectionModel().getSelectedItem()));
    }

    public void deleteFromServer(ActionEvent actionEvent) throws IOException {
        net.write(new DeleteMessageFromServer(
                serverView.getSelectionModel().getSelectedItem()));
    }

    public void deleteFromClient(ActionEvent actionEvent) throws IOException {
        net.write(new DeleteMessageFromClient(
                clientView.getSelectionModel().getSelectedItem()));
    }

    public void pathUpClient(ActionEvent actionEvent) throws IOException {
        Path path = Paths.get(pathToFileClient.getText()).getParent();
        if (path != null) {
            pathToFileClient.setText(String.valueOf(path));
            updateView(clientView, pathToFileClient);
        }
    }

    public void pathUpServer(ActionEvent actionEvent) throws IOException {
        Path path = Paths.get(pathToFileServer.getText()).getParent();
        if (path != null) {
            pathToFileServer.setText(String.valueOf(path));
            updateView(serverView, pathToFileServer);

        }
    }

    public void changeClientDisk(ActionEvent actionEvent) throws IOException {
        ComboBox<String> a = (ComboBox<String>) actionEvent.getSource();
        pathToFileClient.setText(a.getSelectionModel().getSelectedItem());
        updateView(clientView, pathToFileClient);
    }

    public void changeServerDisk(ActionEvent actionEvent) throws IOException {
        ComboBox<String> a = (ComboBox<String>) actionEvent.getSource();
        pathToFileServer.setText(a.getSelectionModel().getSelectedItem());
        updateView(serverView, pathToFileServer);
    }

    private void openDirectory(ListView<String> someView, TextField pathToDirectory) throws IOException {
        someView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    Path path = Paths.get(pathToDirectory.getText())
                            .resolve(someView.getSelectionModel().getSelectedItem());
                    if (Files.isDirectory(path)) {
                        try {
                            pathToDirectory.setText(String.valueOf(path));
                            updateView(someView, pathToDirectory);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void exitButton(ActionEvent actionEvent) {
        Platform.exit();
    }
}