package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ByteBuffer buffer = ByteBuffer.allocate(10);

        try {
            // 소켓 채널 생성
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 12345));

            buffer.put((byte)sc.nextInt());
            String body = sc.next();
            byte[] byteBody = body.getBytes("UTF-8");
            buffer.put((byte) byteBody.length);
            buffer.put(byteBody);

            buffer.flip();

            socketChannel.write(buffer);
            //buffer.clear();
             //소켓 채널 닫기
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}