package com.geek.cloud.model;

import lombok.Getter;

@Getter
public class DeleteMessageFromServer extends AbstractMessage{
    private final String name;

    public DeleteMessageFromServer(String name) {
        this.name  = name;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETE;
    }
}
