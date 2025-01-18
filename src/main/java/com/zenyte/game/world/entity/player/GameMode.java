package com.zenyte.game.world.entity.player;

import com.zenyte.api.model.IronmanMode;
import com.zenyte.game.util.TextUtils;

import java.util.HashMap;
import java.util.Map;

public enum GameMode {

    REGULAR(31, IronmanMode.REGULAR),
    STANDARD_IRON_MAN(2, IronmanMode.IRONMAN),
    ULTIMATE_IRON_MAN(3, IronmanMode.ULTIMATE_IRONMAN),
    HARDCORE_IRON_MAN(10, IronmanMode.HARDCORE_IRONMAN);

    public static final GameMode[] values = values();
    private static final Map<Integer, GameMode> MODES = new HashMap<>();

    static {
        for (final GameMode mode : values) {
            MODES.put(mode.ordinal(), mode);
        }
    }

    private final int icon;
    private final IronmanMode apiRole;

    GameMode(final int icon, final IronmanMode apiRole) {
        this.icon = icon;
        this.apiRole = apiRole;
    }

    public static String getTitle(final Player player) {
        final GameMode mode = player.getGameMode();
        final boolean male = player.getAppearance().isMale();
        return mode.equals(STANDARD_IRON_MAN) ? "<col=60636B>Iron" + (male ? "man" : "woman") + "</col>" : mode.equals(ULTIMATE_IRON_MAN) ? "<col=D8D8D8>Ultimate Iron" + (male ? "man" : "woman") + "</col>" : mode.equals(HARDCORE_IRON_MAN) ? "<col=A30920>Hardcore Iron" + (male ? "man" : "woman") + "</col>" : mode.equals(REGULAR) ? "Regular player" : "";
    }

    public static GameMode get(final int index) {
        return MODES.get(index);
    }

    public String getCrown() {
        return this.equals(STANDARD_IRON_MAN) ? "<img=2>" : this.equals(ULTIMATE_IRON_MAN) ? "<img=3>" : this.equals(HARDCORE_IRON_MAN) ? "<img=10>" : "";
    }

    @Override
    public String toString() {
        return TextUtils.capitalize(name().toLowerCase().replaceAll("_", " "));
    }

    public int getIcon() {
        return this.icon;
    }

    public IronmanMode getApiRole() {
        return this.apiRole;
    }
}
