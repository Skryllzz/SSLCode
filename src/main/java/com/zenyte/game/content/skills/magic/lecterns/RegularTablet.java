package com.zenyte.game.content.skills.magic.lecterns;

import com.zenyte.game.item.Item;

import static com.zenyte.game.content.skills.magic.lecterns.TabletCreation.CLAY;
import static com.zenyte.game.content.skills.magic.spells.MagicSpell.*;

/**
 * @author Kris | 03/09/2019 08:06
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
enum RegularTablet implements LecternTablet {
    VARROCK_TELEPORT(25, 35.0F, new Item(8007), new Item(FIRE_RUNE), new Item(AIR_RUNE, 3), new Item(LAW_RUNE), new Item(CLAY)),
    LUMBRIDGE_TELEPORT(31, 41.0F, new Item(8008), new Item(EARTH_RUNE), new Item(AIR_RUNE, 3), new Item(LAW_RUNE), new Item(CLAY)),
    FALADOR_TELEPORT(37, 48.0F, new Item(8009), new Item(WATER_RUNE), new Item(AIR_RUNE, 3), new Item(LAW_RUNE), new Item(CLAY)),
    CAMELOT_TELEPORT(45, 55.5F, new Item(8010), new Item(AIR_RUNE, 5), new Item(LAW_RUNE), new Item(CLAY)),
    ARDOUGNE_TELEPORT(51, 61.0F, new Item(8011), new Item(WATER_RUNE, 2), new Item(LAW_RUNE, 2), new Item(CLAY)),
    WATCHTOWER_TELEPORT(58, 68.0F, new Item(8012), new Item(EARTH_RUNE, 2), new Item(LAW_RUNE, 2), new Item(CLAY)),
    TELEPORT_TO_HOUSE(40, 30.0F, new Item(8013), new Item(LAW_RUNE), new Item(AIR_RUNE), new Item(EARTH_RUNE), new Item(CLAY)),
    SAPPHIRE_ENCHANTMENT(7, 17.5F, new Item(8016), new Item(WATER_RUNE), new Item(COSMIC_RUNE), new Item(CLAY)),
    EMERALD_ENCHANTMENT(27, 37.0F, new Item(8017), new Item(AIR_RUNE, 3), new Item(COSMIC_RUNE), new Item(CLAY)),
    RUBY_ENCHANTMENT(49, 59.0F, new Item(8018), new Item(FIRE_RUNE, 5), new Item(COSMIC_RUNE), new Item(CLAY)),
    DIAMOND_ENCHANTMENT(57, 67.0F, new Item(8019), new Item(EARTH_RUNE, 10), new Item(COSMIC_RUNE), new Item(CLAY)),
    DRAGONSTONE_ENCHANTMENT(68, 78.0F, new Item(8020), new Item(WATER_RUNE, 15), new Item(EARTH_RUNE, 15), new Item(COSMIC_RUNE), new Item(CLAY)),
    ONYX_ENCHANTMENT(87, 97.0F, new Item(8021), new Item(EARTH_RUNE, 20), new Item(FIRE_RUNE, 20), new Item(COSMIC_RUNE), new Item(CLAY)),
    BONES_TO_BANANAS(15, 25.0F, new Item(8014), new Item(EARTH_RUNE, 2), new Item(WATER_RUNE, 2), new Item(NATURE_RUNE), new Item(CLAY)),
    BONES_TO_PEACHES(60, 35.5F, new Item(8015), new Item(NATURE_RUNE, 2), new Item(WATER_RUNE, 4), new Item(EARTH_RUNE, 3), new Item(CLAY));
    private final int level;
    private final float experience;
    private final Item[] runes;
    private final Item tab;

    RegularTablet(final int level, final float experience, final Item tab, final Item... runes) {
        this.level = level;
        this.experience = experience;
        this.tab = tab;
        this.runes = runes;
    }

    public int getLevel() {
        return this.level;
    }

    public float getExperience() {
        return this.experience;
    }

    public Item[] getRunes() {
        return this.runes;
    }

    public Item getTab() {
        return this.tab;
    }
}
