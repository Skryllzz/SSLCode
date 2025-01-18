package com.zenyte.game.world.entity.player;

import com.zenyte.game.util.TextUtils;

/**
 * @author Tommeh | 5-4-2019 | 16:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum Privilege {

    PLAYER(0, "", "000000"),
    YOUTUBER(6, "<img=7>", "ff0000"),
    MEMBER(0, "", ""),
    FORUM_MODERATOR(9, "<img=6>", "cc6eee"),
    SUPPORT(7, "<img=4>", "00b8ff"),
    MODERATOR(1, "<img=0>", "c6cad1"),
    GLOBAL_MODERATOR(8, "<img=5>", "5bf45b") {
        @Override
        public String toString() {
            return "Senior Moderator";
        }
    },
    ADMINISTRATOR(2, "<img=1>", "e4df28"),
    SPAWN_ADMINISTRATOR(2, "<img=1>", "e4df28") {
        @Override
        public String toString() {
            return "Management";
        }
    },
    HIDDEN_ADMINISTATOR(0, "", "000000");
    private final int icon; //client ordinal
    private final String crown;
    private final String yellColor;

    Privilege(final int icon, final String crown, final String yellColor) {
        this.icon = icon;
        this.crown = crown;
        this.yellColor = yellColor;
    }

    public boolean eligibleTo(final Privilege p) {
        return ordinal() >= p.ordinal();
    }

    @Override
    public String toString() {
        return TextUtils.formatName(name().toLowerCase().replaceAll("_", " "));
    }

    public int getIcon() {
        return this.icon;
    }

    public String getCrown() {
        return this.crown;
    }

    public String getYellColor() {
        return this.yellColor;
    }
}
