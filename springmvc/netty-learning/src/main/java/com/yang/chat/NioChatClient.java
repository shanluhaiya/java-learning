package com.yang.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 */
public class NioChatClient {
    /**
     * 定义一些相关属性
     */

    private final String HOST = "127.0.0.1";
    private final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;

    /**
     * 完成初始化工作
     */
    public NioChatClient() {
        try {
            selector = Selector.open();

            // 连接服务器
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println("客户端" + userName + "就绪了...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向服务器发送消息
     */
    public void sendInfo(String info) {
        info = userName + " 说: " + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取从服务器回复的消息
     */
    public void readInfo() {
        try {
            int readChannels = selector.select();
            if (readChannels > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);
                        String msg = new String(buffer.array());
                        System.out.println(msg.trim());
                    }
                    // 删除当前的selectKey, 防止重复操作
                    iterator.remove();
                }
            } else {
                System.out.println("没有可用的通道...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        // 启动客户端
        NioChatClient chatClient = new NioChatClient();
        // 启动一个线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    chatClient.readInfo();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            chatClient.sendInfo(s);
        }
    }
}
