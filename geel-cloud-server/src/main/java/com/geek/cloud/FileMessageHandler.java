package com.geek.cloud;

import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class FileMessageHandler implements Runnable, Initializable {

    private final File dir;
    private final DataInputStream is;
    private final DataOutputStream os;
    public ListView<String> view;

    public FileMessageHandler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());   //read from socket
        os = new DataOutputStream(socket.getOutputStream()); //write to socket
        dir = new File("files");                    //path to dir with files
        String[] files = dir.list();                         //convert to string massive of files in path
        os.writeUTF("#list#");                           //write in stream command "#list#"
        os.writeLong(files.length);                          //write in stream length of file bytes
        for (String file : files) {
            os.writeUTF(file);                               //write in stream all files
        }
    }

    private void readListFiles() {
        try {
            view.getItems().clear();//clearing view field
            Long filesCount = is.readLong();//reading file length
            for (int i = 0; i < filesCount; i++) {
                String fileName = is.readUTF();//reading names of files
                view.getItems().addAll(fileName);//put this names to view field
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String utf = null;
        try {
            while (true) {
                utf = is.readUTF();//read in utf incoming file
                System.out.println(utf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

