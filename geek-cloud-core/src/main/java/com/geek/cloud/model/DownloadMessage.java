package com.geek.cloud.model;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class DownloadMessage extends AbstractMessage {
    private final String name;

    public DownloadMessage(String name) throws IOException {
        this.name = name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.FILE_REQUEST;
    }
}

