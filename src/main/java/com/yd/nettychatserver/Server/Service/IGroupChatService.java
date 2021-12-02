package com.yd.nettychatserver.Server.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@Service
public class IGroupChatService {

    private int port;


    //初始化 netty服务器
    private void init(int port) throws Exception {
        this.port=port;
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup(16);
        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(boss, work);
            boot.channel(NioServerSocketChannel.class);//设置boss selector建立channel使用的对象
            boot.option(ChannelOption.SO_BACKLOG, 128);//boss 等待连接的 队列长度
            boot.childOption(ChannelOption.SO_KEEPALIVE, true); //让客户端保持长期活动状态
            boot.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    //从channel中获取pipeline 并往里边添加Handler
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("encoder", new StringEncoder());
                    pipeline.addLast("decoder", new StringDecoder());
                    pipeline.addLast(new ServerMessageHandler());//自定义Handler来处理消息
                }
            });
            System.out.println("服务器开始启动...");
            //绑定端口
            ChannelFuture channelFuture = boot.bind(port).sync();
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {

                @Override
                public void operationComplete(Future<? super Void> future)
                        throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("服务器正在启动...");
                    }
                    if (future.isDone()) {
                        System.out.println("服务器启动成功...OK");
                    }

                }
            });
            //监听channel关闭
            channelFuture.channel().closeFuture().sync();
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {

                @Override
                public void operationComplete(Future<? super Void> future)
                        throws Exception {
                    if (future.isCancelled()) {
                        System.out.println("服务器正在关闭..");
                    }
                    if (future.isCancellable()) {
                        System.out.println("服务器已经关闭..OK");
                    }

                }
            });

        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    /**
     * 启动服务器 start 函数
     *
     * @param
     * @throws Exception
     */
    public  void start() throws Exception {
        new IGroupChatService().init(9090);

    }

}


