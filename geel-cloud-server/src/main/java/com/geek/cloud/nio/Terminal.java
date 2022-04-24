package com.geek.cloud.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;

public class Terminal {
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final ByteBuffer buffer = ByteBuffer.allocate(256);
    private Path dir;

    public Terminal() throws IOException{
        dir = Path.of("serverFiles");
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8189));
        serverChannel.configureBlocking(false);
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started on port: 8199");

        while (serverChannel.isOpen()) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            try {
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    iterator.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        System.out.println("Client accepted..");
        channel.write(ByteBuffer.wrap("Welcome!\n\r -> ".getBytes(StandardCharsets.UTF_8)));
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        String message = readMessageFromChannel(channel).trim();
        System.out.println("Received: " + message);

        for (String f : dir.toFile().list()) {
            if (message.equals("cat " + f)) {
                channel.write(ByteBuffer.wrap(new Realization()
                        .catRealization(String.valueOf(Path.of(dir.toFile().getPath(), f)))
                        .getBytes(StandardCharsets.UTF_8)));
            }
        }

        if (message.equals("ls")) {
            channel.write(ByteBuffer.wrap(new Realization()
                    .getLsResultString(dir).getBytes(StandardCharsets.UTF_8)));
            channel.write(ByteBuffer.wrap(" -> ".getBytes(StandardCharsets.UTF_8)));
        }

        if (message.equals("cd")) {
            channel.write(ByteBuffer.wrap((new Realization()
                    .cdRealization1(dir) + "\n\r")
                    .getBytes(StandardCharsets.UTF_8)));
            dir = Path.of(new Realization()
                    .cdRealization1(dir));
        }

        if (message.equals("touch")) {
            String a = new Realization().touchRealization(dir);
            channel.write(ByteBuffer.wrap(a.getBytes(StandardCharsets.UTF_8)));
        }

        if (message.equals("mkdir")) {
            String a = new Realization().mkdirRealization(dir);
            channel.write(ByteBuffer.wrap(a.getBytes(StandardCharsets.UTF_8)));
        }

        else {
            try {
                channel.write(ByteBuffer.wrap("Unknown command\n\r -> "
                        .getBytes(StandardCharsets.UTF_8)));
            } catch (ClosedChannelException e) {
                System.out.println("Client disconnected...");
            }
        }
    }

    private String readMessageFromChannel(SocketChannel channel) throws IOException {
        StringBuilder sb = new StringBuilder();

        while (true) {
            int readCount = channel.read(buffer);
            if (readCount == -1) {
                channel.close();
                break;
            }

            if (readCount == 0) break;
            buffer.flip();

            while (buffer.hasRemaining()) {
                sb.append((char) buffer.get());
            }
            buffer.clear();
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        new Terminal();
    }
}
