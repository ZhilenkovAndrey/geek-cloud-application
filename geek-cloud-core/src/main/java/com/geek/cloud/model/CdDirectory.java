package com.geek.cloud.model;

import lombok.Getter;

import java.nio.file.Path;

@Getter
public class CdDirectory extends AbstractMessage {
    private Path path;

    public CdDirectory(Path path) {
        this.path = path;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CD;
    }
}
