package org.example;

import org.example.packet.LocationPacket;
import org.example.packet.Packet;
import org.example.packet.UserNamePacket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler {

    private final Game game;

    Map<Integer, Packet> map = new HashMap<>();
    UserNamePacket userNamePacket;
    LocationPacket locationPacket;

    public PacketHandler(Game game) {
        this.game = game;
        userNamePacket = new UserNamePacket(game);
        locationPacket = new LocationPacket(game);
        map.put(1, userNamePacket);
        map.put(2, locationPacket);
    }

    public void processBuff(User user, ByteBuffer buffer) {

        buffer.flip();
        int type = buffer.get();
        Packet packet = map.get(type);

        int size = buffer.get();
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        String input = new String(bytes, StandardCharsets.UTF_8);

        packet.readBody(user, input);

    }

}
