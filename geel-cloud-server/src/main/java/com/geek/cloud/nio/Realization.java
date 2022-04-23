package com.geek.cloud.nio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Realization {

    void catRealization(Path dir) {

    }

    void cdRealization() {

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

    String mkdirRealization() {
        String mess;
        Path dir = Path.of("serverFiles", "newDir");
        try {
            Files.createDirectory(dir);
            mess = "directory create\n\r -> ";
        } catch (IOException e){
            mess = "directory exist.\n\r -> ";
        }
        return mess;
    }

    String getLsResultString(Path dir) throws IOException {
        return Files.list(dir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.joining("\n\r")) + "\n\r";
    }

}
