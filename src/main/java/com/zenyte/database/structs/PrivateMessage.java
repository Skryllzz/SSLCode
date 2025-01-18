package com.zenyte.database.structs;

import com.zenyte.game.world.entity.player.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PrivateMessage {
    public static List<PrivateMessage> list = new ArrayList<>();
    private final Player player;
    private final String friend;
    private final String message;
    private final Timestamp date = new Timestamp(System.currentTimeMillis());

    public PrivateMessage(final Player player, final String friend, final String message) {
        this.player = player;
        this.friend = friend;
        this.message = message;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getFriend() {
        return this.friend;
    }

    public String getMessage() {
        return this.message;
    }

    public Timestamp getDate() {
        return this.date;
    }
}
