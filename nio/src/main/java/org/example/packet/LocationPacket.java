package org.example.packet;

import org.example.User;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LocationPacket implements Packet{
    @Override
    public void readBody(User user, String body) {

        System.out.println(body);

        String[] location = body.split(",");
        int nowX = Integer.parseInt(location[0]);
        int nowY = Integer.parseInt(location[1]);
        user.setX(nowX);
        user.setY(nowY);
        System.out.println(user);
    }
}
