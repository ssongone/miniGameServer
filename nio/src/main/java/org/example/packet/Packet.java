package org.example.packet;

import org.example.User;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

public interface Packet extends BiConsumer<User,String> {
    @Override
    default void accept(User user, String body) {
        readBody(user, body);
    }

    void readBody(User user, String body);
}


