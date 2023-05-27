package org.example.packet;

import org.example.Game;
import org.example.User;

public class UserNamePacket implements Packet{
    private final Game game;

    public UserNamePacket(Game game) {
        this.game = game;
    }
    @Override
    public void readBody(User user, String body){
        System.out.println(body);
        user.setName(body);
        game.addPlayer(user, 21);
    }
}
