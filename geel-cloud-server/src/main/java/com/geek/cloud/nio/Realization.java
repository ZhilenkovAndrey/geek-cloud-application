package com.geek.cloud.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Realization {

    String catRealization(String path) {
        String a = "";
        try {
            SeekableByteChannel channel = Files.newByteChannel(Path.of(path));
            ByteBuffer buff = ByteBuffer.allocate(1024);
            while (true) {
                int readCount = channel.read(buff);

                if (readCount <= 0) break;
                buff.flip();

                while (buff.hasRemaining()) {
                    a += (char) buff.get();
                }
                buff.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
            a = "no such file\n\r";
        }
        return a;
    }

    String cdRealization1(Path path) {
        String a;
        try {
            Path absolutPath = path.toAbsolutePath();
            path = absolutPath.resolve("..").normalize();
            a = String.valueOf(path);
        } catch (Exception e) {
            a = "directory exist.\n\r -> ";
        }
        return a;
    }

    String touchRealization(Path dir) {
        String mess;
        Path file = dir.resolve("newFile.txt");
        try {
            Files.createFile(file);
            mess = "file create.\n\r -> ";
        } catch (IOException e) {
            mess = "file exist.\n\r -> ";
        }
        return mess;
    }

    String mkdirRealization(Path dir) {
        String mess;
        try {
            Files.createDirectory(Path.of(String.valueOf(dir), "newDir"));
            mess = "directory create\n\r -> ";
        } catch (IOException e) {
            mess = "directory exist.\n\r -> ";
        }
        return mess;
    }

    String getLsResultString(Path dir) {
        String a;
        try {
            a = Files.list(dir)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.joining("\n\r")) + "\n\r";
        } catch (IOException e) {
            a = "directory exist.\n\r -> ";
        }
        return a;
    }
}
