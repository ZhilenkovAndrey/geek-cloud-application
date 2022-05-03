package com.geek.cloud.netty.serialization;

import com.geek.cloud.model.AbstractMessage;
import com.geek.cloud.model.DownloadMessage;
import com.geek.cloud.model.FileMessage;
import com.geek.cloud.model.ListMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<AbstractMessage> {

    private final Path serverDir = Path.of("serverFiles");
    private final Path clientDir = Path.of("clientFiles");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ListMessage message = new ListMessage(serverDir);
        ctx.writeAndFlush(message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
        log.info("received: {} message", msg.getMessageType().getName());

        if (msg instanceof FileMessage file) {
            Files.write(serverDir.resolve(file.getName()), file.getBytes());
            ctx.writeAndFlush(new ListMessage(serverDir));
        }

        if (msg instanceof DownloadMessage downloadMessage) {
            Files.copy(serverDir.resolve(downloadMessage.getName()),
                    clientDir.resolve(downloadMessage.getName()));
            ctx.writeAndFlush(new DownloadMessage(downloadMessage.getName()));
        }
    }
}