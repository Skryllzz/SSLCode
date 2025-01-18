package com.zenyte.game.content;

import com.zenyte.game.util.TextUtils;
import com.zenyte.game.world.entity.player.Skills;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tommeh | 24-3-2019 | 17:41
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum AccomplishmentCape {
    ATTACK(Skills.ATTACK, 9747, 9748, 9749),
    DEFENCE(Skills.DEFENCE, 9753, 9754, 9755),
    STRENGTH(Skills.STRENGTH, 9750, 9751, 9752),
    HITPOINTS(Skills.HITPOINTS, 9768, 9769, 9770),
    RANGED(Skills.RANGED, 9756, 9757, 9758),
    PRAYER(Skills.PRAYER, 9759, 9760, 9761),
    MAGIC(Skills.MAGIC, 9762, 9763, 9764),
    COOKING(Skills.COOKING, 9801, 9802, 9803),
    WOODCUTTING(Skills.WOODCUTTING, 9807, 9808, 9809),
    FLETCHING(Skills.FLETCHING, 9783, 9784, 9785),
    FISHING(Skills.FISHING, 9798, 9799, 9800),
    FIREMAKING(Skills.FIREMAKING, 9804, 9805, 9806),
    CRAFTING(Skills.CRAFTING, 9780, 9781, 9782),
    SMITHING(Skills.SMITHING, 9795, 9796, 9797),
    MINING(Skills.MINING, 9792, 9793, 9794),
    HERBLORE(Skills.HERBLORE, 9774, 9775, 9776),
    AGILITY(Skills.AGILITY, 9771, 9772, 9773),
    THIEVING(Skills.THIEVING, 9777, 9778, 9779),
    SLAYER(Skills.SLAYER, 9786, 9787, 9788),
    FARMING(Skills.FARMING, 9810, 9811, 9812),
    RUNECRAFTING(Skills.RUNECRAFTING, 9765, 9766, 9767),
    CONSTRUCTION(Skills.CONSTRUCTION, 9789, 9790, 9791),
    HUNTER(Skills.HUNTER, 9948, 9949, 9950),
    DIARY(-1, 19476, 13069, 13070);
    private static final Set<AccomplishmentCape> ALL = EnumSet.allOf(AccomplishmentCape.class);
    private static final Map<Integer, AccomplishmentCape> CAPES = new HashMap<>();
    private static final Map<Integer, AccomplishmentCape> BY_SKILL = new HashMap<>();

    static {
        for (final AccomplishmentCape cape : ALL) {
            CAPES.put(cape.getUntrimmed(), cape);
            CAPES.put(cape.getTrimmed(), cape);
            BY_SKILL.put(cape.getSkill(), cape);
        }
    }

    private final int skill;
    private final int untrimmed;
    private final int trimmed;
    private final int hood;

    AccomplishmentCape(final int skill, final int untrimmed, final int trimmed, final int hood) {
        this.skill = skill;
        this.untrimmed = untrimmed;
        this.trimmed = trimmed;
        this.hood = hood;
    }

    public static AccomplishmentCape get(final int id) {
        return CAPES.get(id);
    }

    public static AccomplishmentCape getBySkill(final int id) {
        return BY_SKILL.get(id);
    }

    @Override
    public String toString() {
        return TextUtils.capitalizeFirstCharacter(name().toLowerCase());
    }

    public int getSkill() {
        return this.skill;
    }

    public int getUntrimmed() {
        return this.untrimmed;
    }

    public int getTrimmed() {
        return this.trimmed;
    }

    public int getHood() {
        return this.hood;
    }
}
