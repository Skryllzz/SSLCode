package com.zenyte.database.structs;

import com.zenyte.game.world.entity.player.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ClanChatMessage {
    public static final List<ClanChatMessage> list = new ArrayList<>();
    private final Player player;
    private final String message;
    private final String clan;
    private final Timestamp date = new Timestamp(System.currentTimeMillis());

    public ClanChatMessage(final Player player, final String message, final String clan) {
        this.player = player;
        this.message = message;
        this.clan = clan;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }

    public String getClan() {
        return this.clan;
    }

    public Timestamp getDate() {
        return this.date;
    }
}
