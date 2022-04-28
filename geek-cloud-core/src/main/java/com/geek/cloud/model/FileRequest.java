package com.geek.cloud.model;

import lombok.Getter;
import java.nio.file.Path;

@Getter
public class FileRequest extends AbstractMessage {
    private final String name;

    public FileRequest(Path path) {
        name = path.getFileName().toString();
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_REQUEST;
    }
}
