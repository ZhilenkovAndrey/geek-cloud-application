package com.geek.cloud.model;

import lombok.Getter;

@Getter
public class DeleteMessageFromClient extends AbstractMessage {
    private final String name;

    public DeleteMessageFromClient(String name) {
        this.name = name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETE;
    }
}

