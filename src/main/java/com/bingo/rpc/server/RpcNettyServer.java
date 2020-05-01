package com.bingo.rpc.server;

import com.bingo.rpc.registry.MapRegistry;
import com.bingo.rpc.registry.Registry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class RpcNettyServer {

    private int port;
    public RpcNettyServer(int port){
        this.port = port;
    }

    public void start(){

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            /**
                             * 自定义协议解码器
                             * 入参有五个，分别解释如下
                             * maxFrameLength: 框架的最大长度，如果帧的长度大于此值，则将抛出ToolLongFrameException
                             * lengthFieldOffset: 长度字段的偏移量：即对应的长度字段在整个消息数据中的位置
                             * lengthFieldLength: 长度字段的长度。如：长度字段是int型表示，那么这个值就是4（long型就是8）
                             * lengthAdjustment: 要添加长度字段的补偿值
                             * initialBytesToStrip: 从解码帧中去除第一个字节数
                             */
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            //自定义协议解码器
                            pipeline.addLast(new LengthFieldPrepender(4));
                            //对象参数类型编码器
                            pipeline.addLast("encoder",new ObjectEncoder());
                            //对象参数类型解码器
                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE,
                                    ClassResolvers.cacheDisabled(null)));

                            Registry registry = new MapRegistry();
                            pipeline.addLast(new ServerHandler(registry));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            ChannelFuture future = server.bind(port).sync();
            System.out.println("bingo rpc server listen at "+port);
            future.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
