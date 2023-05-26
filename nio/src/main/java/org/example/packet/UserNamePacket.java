package org.example.packet;

import org.example.User;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class UserNamePacket implements Packet{
    @Override
    public void readBody(User user, String body){
        System.out.println(body);
        user.setName(body);
    }
}
