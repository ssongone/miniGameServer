package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        NioServer server = new NioServer();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}