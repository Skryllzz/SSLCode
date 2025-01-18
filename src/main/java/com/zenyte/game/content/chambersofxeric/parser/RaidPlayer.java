package com.zenyte.game.content.chambersofxeric.parser;

import org.jetbrains.annotations.NotNull;

public class RaidPlayer {
    String playerName;
    int points;

    public RaidPlayer(String playerName) {
        this.playerName = playerName;
    }

    public RaidPlayer(String playerName, int points) {
        this.playerName = playerName;
        this.points = points;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPoints() {
        return points;
    }

    @NotNull
    @Override
    public String toString() {
        return "RaidPlayer(playerName=" + this.getPlayerName() + ", points=" + this.getPoints() + ")";
    }
}
