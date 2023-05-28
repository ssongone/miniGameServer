package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try {
            // 소켓 채널 생성
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 12345));

            ByteBuffer buffer = ByteBuffer.allocate(10);
            Scanner sc = new Scanner(System.in);

            while (true) {
                buffer.clear();

                int type = sc.nextInt();
                if (type == -1)
                    break;
                String body = sc.next();
                byte[] byteBody = body.getBytes("UTF-8");


                buffer.put((byte) type);
                buffer.put((byte) byteBody.length);
                buffer.put(byteBody);
                buffer.flip();
                socketChannel.write(buffer);

                ByteBuffer input = ByteBuffer.allocate(1024);
                int bytesRead = socketChannel.read(input);

                if (bytesRead > 0) {
                    input.flip();
                    byte[] receivedData = new byte[bytesRead];
                    input.get(receivedData);
                    String response = new String(receivedData, "UTF-8");
                    System.out.println("서버 응답: " + response);
                }

            }

            // 소켓 채널 닫기
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
