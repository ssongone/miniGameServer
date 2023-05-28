package org.example.packet;

import org.example.Game;
import org.example.User;

import java.io.IOException;


public class LocationPacket implements Packet{
    private final Game game;

    public LocationPacket(Game game) {
        this.game = game;
    }

    @Override
    public void readBody(User user, String body) {
        String[] location = body.split(",");
        int nowX = Integer.parseInt(location[0]);
        System.out.println("nowX = " + nowX);
        int nowY = Integer.parseInt(location[1]);
        System.out.println("nowY = " + nowY);
        user.setX(nowX);
        user.setY(nowY);

        int before = user.getLocation();
        int now =  user.setLocation(user.getX(), user.getY());
        try {
            game.movePlayer(user, before, now);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
