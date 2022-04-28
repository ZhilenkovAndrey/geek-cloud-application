package com.geek.cloud.netty;

import com.geek.cloud.netty.handlers.EchoHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class EchoPipeLine extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(
                new StringEncoder(),
                new StringDecoder(),
                new EchoHandler()
        );
    }
}
