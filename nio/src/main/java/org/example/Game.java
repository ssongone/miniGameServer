package org.example;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Game {
    private static int MAP_SIZE = 36;
    //private Selector selector;
    Map<Integer, List<User>> map = new ConcurrentHashMap<>();

    public Game() {
        //this.selector = selector;
        for (int i = 0 ; i < MAP_SIZE ; i++) {
            map.put(i, new ArrayList<>());
        }
    }

    public void addPlayer(User user, int location) {
        List<User> playerList = map.get(location);
        playerList.add(user);
        log.info("현재 같은 맵에 있는 사람들 : {}", playerList);

    }

    public void movePlayer(User user, int before, int now) {
        map.get(before).remove(user);
        map.get(now).add(user);
        log.info("전 맵에 있는 사람들 : {}", map.get(before));
        log.info("현재 같은 맵에 있는 사람들 : {}", map.get(now));
    }


    public void broadcastMessage(int location) throws IOException {
        List<User> Users = map.get(location);
        String message = "현재 같은 맵에 있는 사람은 : ";
        for (User user : Users) {
            message += (user.getName() + ", ");
        }

        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

        for (User user : Users) {
            SocketChannel clientChannel = user.getSocketChannel();
            clientChannel.write(buffer);
            buffer.rewind();
        }
    }



    

}
