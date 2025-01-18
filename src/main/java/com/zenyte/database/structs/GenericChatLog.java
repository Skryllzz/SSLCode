package com.zenyte.database.structs;

import com.zenyte.game.world.entity.player.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GenericChatLog {
    public static List<GenericChatLog> list = new ArrayList<>();
    private final Player player;
    private final String message;
    private final Timestamp date = new Timestamp(System.currentTimeMillis());

    public GenericChatLog(final Player player, final String message) {
        this.player = player;
        this.message = message;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }

    public Timestamp getDate() {
        return this.date;
    }
}
