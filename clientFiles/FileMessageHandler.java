package com.geek.cloud;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class FileMessageHandler implements Runnable {
    private final File dir;
    private final DataInputStream is;
    private final DataOutputStream os;

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
}
