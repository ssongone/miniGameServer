package org.example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.channels.SocketChannel;

@Getter @Setter @ToString
public class User {
    private SocketChannel socketChannel;
    private String name;
    private int location = 21;
    private int x = 0, y = 0;

    public User(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.name = "";
    }

    public int setLocation(int x, int y) {
        int nowX = (x / 50) + 2;
        int nowY = (y / 50) + 2;
        if (x >= 0)
            nowX++;
        if (y >= 0)
            nowY++;
        int nowLocation = (nowY * 6) + nowX;
        this.location = nowLocation;
        return nowLocation;
    }

}
