package com.zenyte.game.world.broadcasts;

import com.zenyte.game.world.entity.player.GameSetting;

import java.util.Optional;

/**
 * @author Tommeh | 5-2-2019 | 00:41
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum BroadcastType {
    HELPFUL_TIP("7881fd", 13, Optional.of(GameSetting.HELPFUL_TIPS)),
    RARE_DROP("e59400", 13, Optional.of(GameSetting.RARE_DROP_BROADCASTS)),
    LVL_99("e59400", 13, Optional.of(GameSetting.LEVEL_99_BROADCASTS)),
    MAXED("ff0000", 13, Optional.of(GameSetting.MAXED_PLAYER_BROADCASTS)),
    XP_200M("ff0000", 13, Optional.of(GameSetting.MAX_SKILL_XP_BROADCASTS)),
    PET("ff0000", 13, Optional.of(GameSetting.PET_BROADCASTS)),
    GAMBLE_FIRECAPE("ff0000", 13, Optional.empty()),
    HCIM_DEATH("ff0000", 8, Optional.of(GameSetting.HARDCORE_IRONMAN_DEATH_BROADCASTS)),
    MYSTERY_BOX_RARE_ITEM("ff0000", 13, Optional.of(GameSetting.MYSTERY_BOX_BROADCASTS)),
    COSMETIC_BOX_RARE_ITEM("ff0000", 13, Optional.of(GameSetting.MYSTERY_BOX_BROADCASTS)),
    INFERNO_COMPLETION("ff0000", 13, Optional.empty()),
    TREASURE_TRAILS("e59400", 13, Optional.of(GameSetting.TREASURE_TRAILS_BROADCASTS));
    public static final BroadcastType[] VALUES = values();
    private final String color;
    private final int icon;
    private final Optional<GameSetting> setting;

    BroadcastType(final String color, final int icon, final Optional<GameSetting> setting) {
        this.color = color;
        this.icon = icon;
        this.setting = setting;
    }

    public String getColor() {
        return this.color;
    }

    public int getIcon() {
        return this.icon;
    }

    public Optional<GameSetting> getSetting() {
        return this.setting;
    }
}
