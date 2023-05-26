package org.example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

@Getter @Setter @ToString
public class User {
    private SocketChannel socketChannel;
    private String name;

    private int x = 0, y = 0;

    public User(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.name = "";
    }

}
