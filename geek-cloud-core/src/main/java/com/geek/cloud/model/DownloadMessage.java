package com.geek.cloud.model;

import lombok.Getter;

@Getter
public class DownloadMessage extends AbstractMessage {
    private final String name;

    public DownloadMessage(String name) {
        this.name = name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DOWNLOAD;
    }
}

