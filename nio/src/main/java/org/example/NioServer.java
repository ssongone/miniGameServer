package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NioServer {
    private Game game;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ExecutorService executorService;
    PacketHandler packetHandler;

    Map<SocketChannel, User> users = new HashMap<>();

    public NioServer() throws IOException {
        game = new Game();
        selector = Selector.open();
        packetHandler = new PacketHandler(game);

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", 12345)); //바인딩할때 서버소켓 만들어짐
        serverSocketChannel.configureBlocking(false); // 논블로킹 모드 -> accept()에서 멈추지 않음
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        executorService = Executors.newFixedThreadPool(4); // 스레드풀 생성
    }
    public void start() throws IOException {
        log.info("Server started");

        while (true) {
            // 이벤트 감시
            selector.select();

            // 발생한 이벤트 처리
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey selectedKey = keyIterator.next();
                keyIterator.remove();
                
                if (selectedKey.isAcceptable()) {
                    acceptConnection(selectedKey);
                    continue;
                } 
                if (selectedKey.isReadable()) {
                    readData(selectedKey);
                    //continue;
                }
            }

        }
    }

    void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        users.put(clientChannel, new User(clientChannel));
        log.info("client connected");
    }

    void readData(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            log.info("connection closed : {}", clientChannel);
            clientChannel.close();
            return;
        }
        if (bytesRead > 0) {
            log.info("message received");
            User user = users.get(clientChannel);
            //packetHandler.processBuff(user, buffer);
            executorService.execute(() -> packetHandler.processBuff(user, buffer));
        }
    }


}
