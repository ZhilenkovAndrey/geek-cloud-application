package com.geek.cloud;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileMessageHandler implements Runnable {

    private File dir = new File("files");
    private final DataInputStream is;
    private final DataOutputStream os;
    private ArrayList<String> files1;

    public FileMessageHandler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());   //read from socket
        os = new DataOutputStream(socket.getOutputStream());//write to socket
        String[] files = dir.list();                         //convert to string massive of files in path
        os.writeUTF("#list#");                           //write in stream command "#list#"
        os.writeLong(files.length);                          //write in stream length of file bytes
        for (String file : files) {
            os.writeUTF(file);                               //write in stream all files
        }
    }

    private void readListFiles() throws IOException {
        String fileName = is.readUTF();
        File file = new File(dir + fileName);
        byte[] buf = new byte[1024];
        long size = is.readLong();

        try (OutputStream f = new FileOutputStream(file)) {
              for (int i = 0; i < (size + 1024 - 1)/1024 ; i++) {
                  int read = is.read(buf);
                  f.write(buf, 0, read);
              }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = is.readUTF();
                if (command.equals("file")) {
                    readListFiles();
                }
                os.writeUTF("file arrive");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

