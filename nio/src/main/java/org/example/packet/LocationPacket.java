package org.example.packet;

import org.example.Game;
import org.example.User;


public class LocationPacket implements Packet{
    private final Game game;

    public LocationPacket(Game game) {
        this.game = game;
    }

    @Override
    public void readBody(User user, String body) {

        System.out.println(body);

        String[] location = body.split(",");
        int nowX = Integer.parseInt(location[0]);
        int nowY = Integer.parseInt(location[1]);
        user.setX(nowX);
        user.setY(nowY);
        checkLocation(user);

        System.out.println(user);
    }

    private void checkLocation(User user) {
        int before = user.getLocation();
        if (before != user.setLocation(user.getX(), user.getY()))
            game.movePlayer(user, before, user.getLocation());
    }
}
