package com.bingo.rpc.server;

import com.bingo.rpc.protocol.InvokerProtocol;
import com.bingo.rpc.registry.Registry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private Registry registry;

    public ServerHandler(Registry registry){
        this.registry = registry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InvokerProtocol request = (InvokerProtocol)msg;
        Object result = registry.invoke(request);
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }
}
