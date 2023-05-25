package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    public void start() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 12345)); //바인딩할때 서버소켓 만들어짐
        serverSocketChannel.configureBlocking(false); // 논블로킹 모드 -> accept()에서 멈추지 않음

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 이벤트 감시
            selector.select();
            // 발생한 이벤트 처리
            Set<SelectionKey> selectedKeys = selector.selectedKeys();

//            다중 스레드 환경에서 ConcurrentModificationException 오류
//            컬렉션을 반복하는 동안 컬렉션 자체를 수정하는 것은 권장되지 않음
//            for (SelectionKey selectedKey : selectedKeys) {
//                handleSelectionKey(selectedKey);
//                selectedKeys.remove(selectedKey);
//            }

            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectedKey = keyIterator.next();
                handleSelectionKey(selectedKey);
                keyIterator.remove();
            }

        }
    }

    void handleSelectionKey(SelectionKey key) throws IOException {

        if (key.isAcceptable()) {
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel clientChannel = serverChannel.accept();
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            return;
        }

        if (key.isReadable()) {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(4);
            int bytesRead = clientChannel.read(buffer);

            if (bytesRead == -1) {
                clientChannel.close();
                return;
            }
            if (bytesRead > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte tt = buffer.get();
                    System.out.println(tt);
                }
            }
        }
    }
}
