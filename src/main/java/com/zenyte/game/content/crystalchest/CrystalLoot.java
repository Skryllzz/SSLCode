package com.zenyte.game.content.crystalchest;

import com.zenyte.game.content.skills.magic.Magic;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Kris | 04/04/2019 12:53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum CrystalLoot {
    SPINACH_ROLL(21.45, new Item(1969), new Item(995, 2000)),
    EMPTY(16.77),
    SWORDFISH(5.91, new Item(995, 1000), new Item(371, 5)),
    RUNES(9.58, new Item(Magic.AIR_RUNE, 50), new Item(Magic.WATER_RUNE, 50), new Item(Magic.EARTH_RUNE, 50), new Item(Magic.FIRE_RUNE, 50), new Item(Magic.BODY_RUNE, 50), new Item(Magic.MIND_RUNE, 50), new Item(Magic.CHAOS_RUNE, 10), new Item(Magic.LAW_RUNE, 10), new Item(Magic.COSMIC_RUNE, 10), new Item(Magic.NATURE_RUNE, 10), new Item(Magic.DEATH_RUNE, 10)),
    COAL(7.84, new Item(454, 100)),
    GEMS(9.36, new Item(1603, 2), new Item(1601, 2)),
    TOOTH_HALF_OF_KEY(4.14, new Item(995, 750), new Item(985)),
    RUNITE_BARS(9.41, new Item(2363, 3)),
    LOOP_HALF_OF_KEY(4.0, new Item(995, 750), new Item(987)),
    IRON_ORE(7.92, new Item(441, 150)),
    ADAMANT_SQ_SHIELD(1.76, new Item(1183)),
    PLATELEGS(0.86, (player, item) -> player.getAppearance().isMale() ? new Item(1079) : new Item(1093), (Item) null),
    INFINITY_GLOVES(0.1, new Item(6922)),
    INFINITY_HAT(0.1, new Item(6918)),
    INFINITY_TOP(0.1, new Item(6916)),
    INFINITY_BOTTOMS(0.1, new Item(6924)),
    INFINITY_BOOTS(0.1, new Item(6920)),
    BEGINNER_WAND(0.1, new Item(6908)),
    APPRENTICE_WAND(0.1, new Item(6910)),
    TEACHER_WAND(0.1, new Item(6912)),
    MASTER_WAND(0.1, new Item(6914)),
    MAGES_BOOK(0.1, new Item(6889));
    private static final CrystalLoot[] values = values();
    private static final int TOTAL_WEIGHT;

    static {
        int weight = 0;
        for (final CrystalLoot value : values) {
            weight += value.weight;
        }
        TOTAL_WEIGHT = weight;
    }

    private final int weight;
    private final Item[] loot;
    private final BiFunction<Player, Item, Item> lootFunction;

    CrystalLoot(final double percentage, final Item... loot) {
        this(percentage, null, loot);
    }

    CrystalLoot(final double percentage, final BiFunction<Player, Item, Item> lootFunction, final Item... loot) {
        this.weight = (int) Math.floor(32767.0F * percentage / 100.0F);
        this.lootFunction = lootFunction;
        this.loot = loot;
    }

    public static List<Item> get(@NotNull final Player player) {
        final ObjectArrayList<Item> list = new ObjectArrayList<>();
        list.add(new Item(1631));
        final int roll = Utils.random(TOTAL_WEIGHT);
        int current = 0;
        for (final CrystalLoot loot : values) {
            if ((current += loot.weight) >= roll) {
                for (final Item item : loot.loot) {
                    list.add(loot.lootFunction == null ? item : loot.lootFunction.apply(player, item));
                }
                break;
            }
        }
        return list;
    }

    public int getWeight() {
        return this.weight;
    }

    public Item[] getLoot() {
        return this.loot;
    }

    public BiFunction<Player, Item, Item> getLootFunction() {
        return this.lootFunction;
    }
}
