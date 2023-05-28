package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 12345));

            // 수신 스레드
            Thread receiverThread = new Thread(() -> {
                try {
                    ByteBuffer input = ByteBuffer.allocate(1024);

                    while (true) {
                        input.clear();
                        int bytesRead = socketChannel.read(input);

                        if (bytesRead > 0) {
                            input.flip();
                            byte[] receivedData = new byte[bytesRead];
                            input.get(receivedData);
                            String response = new String(receivedData, "UTF-8");
                            System.out.println("서버 응답: " + response);
                        } else if (bytesRead == -1) {
                            // 서버 연결 종료 시 스레드 종료
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            receiverThread.start();

            ByteBuffer buffer = ByteBuffer.allocate(64);
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
            }

            socketChannel.close();

            // 메인 스레드에서 스레드가 종료될 때까지 대기
            try {
                receiverThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
