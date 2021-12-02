package com.yd.nettychatserver.Server.Service;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Scanner;

public class NettyChatClient {

    private String ip;
    private String name;
    private int port;

    public NettyChatClient(String ip, int port,String name) {
        this.ip = ip;
        this.port = port;
        this.name=name;
    }

    /**
     * 初始化客户
     */
    private void init() throws Exception {
        //创建监听事件的监听器
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            Bootstrap boot = new Bootstrap();
            boot.group(work);
            boot.channel(NioSocketChannel.class);
            boot.handler(new ChannelInitializer<NioSocketChannel>() {

                @Override
                protected void initChannel(NioSocketChannel ch)
                        throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("encoder", new StringEncoder());
                    pipeline.addLast("decoder", new StringDecoder());
                    pipeline.addLast(new ClientMessageHandler());

                }
            });
            ChannelFuture channelFuture = boot.connect(ip, port).sync();
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {

                @Override
                public void operationComplete(Future<? super Void> future)
                        throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("客户端启动中...");
                    }
                    if (future.isDone()) {
                        System.out.println("客户端启动成功...OK！");
                    }
                }
            });
            System.out.println(channelFuture.channel().localAddress().toString());
            System.out.println("#################################################");
            System.out.println("~~~~~~~~~~~~~~端口号#消息内容~~这样可以给单独一个用户发消息~~~~~~~~~~~~~~~~~~");
            System.out.println("#################################################");


            /**
             * 这里用控制台输入数据
             */
            Channel channel = channelFuture.channel();
            String str1 = "大家好，我叫"+name;
            str1=name+"@"+str1;
            channel.writeAndFlush(str1 + "\n");
            //获取channel
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                str=name+"@"+str;
                channel.writeAndFlush(str + "\n");
            }
            channelFuture.channel().closeFuture().sync();
            scanner.close();
        } finally {
            work.shutdownGracefully();
        }
    }

    /**
     * 主方法入口
     *
     * @param
     * @throws Exception
     */
    public static void startChat() throws Exception {

        Scanner scanner = new Scanner(System.in);
        String ip=null;
        int port;
        String name;
        System.out.println("请输入IP地址,端口号,名称并回车，格式：ip:port:name");
        while (scanner.hasNextLine()) {

            ip = scanner.nextLine();
            if (ip.contains(":")) {
                break;
            }
            System.out.println("请输入IP地址,端口号,名称并回车，格式：ip:port:name");
        }
        String[] str = ip.split(":");
        ip = str[0];
        port = Integer.parseInt(str[1]);
        name=str[2];
        new NettyChatClient(ip, port,name).init();
    }

}

