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
    Map<Integer, List<User>> map = new ConcurrentHashMap<>();

    public Game() {
        for (int i = 0 ; i < MAP_SIZE ; i++) {
            map.put(i, new ArrayList<>());
        }
    }

    public void addPlayer(User user, int location) {
        List<User> playerList = map.get(location);
        playerList.add(user);
        log.info("현재 같은 맵에 있는 사람들 : {}", playerList);

        try {
            broadcastMessage(location);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void movePlayer(User user, int before, int now) {
        if (before != now) {
            map.get(before).remove(user);
            map.get(now).add(user);
            log.info("before 맵 {}  : {}", before, map.get(before));
            log.info("now 맵 {} : {}", now, map.get(now));
        }

        try {
            broadcastMessage(now);
            broadcastMessage(before);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void broadcastMessage(int location) throws IOException {
        log.info("broadcastMessage()");
        List<User> Users = map.get(location);
        String message = "현재 같은 맵에 있는 사람은 : ";
        for (User user : Users) {
            message += (user.getName() + "(" + user.getX() + ", " + user.getY() + ") ");
        }
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes("UTF-8"));

        for (User user : Users) {
            SocketChannel clientChannel = user.getSocketChannel();
            clientChannel.write(buffer);
            buffer.rewind();
        }
    }

}
