package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ByteBuffer buffer = ByteBuffer.allocate(4);

        byte clientIdx = (byte) 3;

        try {
            // 소켓 채널 생성
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 12345));

            // 메시지 전송
            String message = "Hello, Server!";


                String input = sc.next();
//                if (input.equals("exit"))
//                    break;
//
//                if (!input.equals("M")) {
//                    System.out.println("입력을 확인해주세요");
//                    continue;
//                }

                buffer.put(clientIdx);
                buffer.put((byte) sc.nextInt());
                buffer.put((byte) sc.nextInt());

                buffer.flip();

                while (buffer.hasRemaining()) {
                    byte data = buffer.get();
                    System.out.println(data);
                }
                buffer.rewind();

                //buffer.clear();



            //ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(buffer);

            // 소켓 채널 닫기
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }





    }
}