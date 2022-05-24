package com.geek.cloud.network;

import com.geek.cloud.model.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Net {

    private final Socket socket;
    private final ObjectDecoderInputStream is;
    private final ObjectEncoderOutputStream os;

    private final String host;
    private final int port;

    public Net(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
            socket = new Socket(host, port);
        os = new ObjectEncoderOutputStream(socket.getOutputStream());
        is = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public AbstractMessage read() throws Exception {
        return (AbstractMessage) is.readObject();
    }

    public void write(AbstractMessage message) throws IOException {
        os.writeObject(message);
        os.flush();
    }
}