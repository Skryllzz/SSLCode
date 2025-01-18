package com.zenyte.game.item;

import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import mgi.types.config.items.ItemDefinitions;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 15/03/2019 18:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum SkillcapePerk {

    ATTACK(9747, 9748, 13342),
    COOKING(9801, 9802, 13342),
    FARMING(9810, 9811, 13342),
    MINING(9792, 9793, 13342),
    RANGED(9756, 9757, 13342, 13337, 21898),
    SLAYER(9786, 9787, 13342),
    THIEVING(9777, 9778, 13342),
    WOODCUTTING(9807, 9808, 13342),
    FIREMAKING(9804, 9805, 13342),
    RUNECRAFTING(9765, 9766, 13342),
    HITPOINTS(9768, 9769, 13342),
    HERBLORE(9774, 9775, 13342),
    AGILITY(9771, 9772, 13340, 13341, 13342),
    PRAYER(9759, 9760, 13342),
    SMITHING(9795, 9796, 13342),
    STRENGTH(9750, 9751, 13342),
    HUNTER(9948, 9949, 13342),
    FISHING(9798, 9799, 13342),
    CONSTRUCTION(9789, 9790, 13342),
    CRAFTING(9780, 9781, 13342),
    DEFENCE(9753, 9754, 13342),
    MAGIC(9762, 9763, 13342),
    FLETCHING(9783, 9784, 13342);

    private final int[] capes;
    private final int[] skillCapes;

    SkillcapePerk(final int... capes) {
        this.capes = capes;
        final IntArrayList list = new IntArrayList();
        for (final int cape : capes) {
            final ItemDefinitions definitions = ItemDefinitions.get(cape);
            if (definitions == null) continue;
            if (definitions.getName().equals("Null")) System.out.println("Null skillcape: " + cape);
            if (definitions.getName().toLowerCase().contains("max")) continue;
            list.add(cape);
        }
        this.skillCapes = list.toIntArray();
    }

    public boolean isEffective(@NotNull final Player player) {
        final int cape = player.getEquipment().getId(EquipmentSlot.CAPE);
        return cape != -1 && ArrayUtils.contains(capes, cape);
    }

    public boolean isCarrying(@NotNull final Player player) {
        return player.carryingAny(capes);
    }

    public int[] getCapes() {
        return capes;
    }

    public int[] getSkillCapes() {
        return skillCapes;
    }

}
