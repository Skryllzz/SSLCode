package com.zenyte.game.content.skills.agility;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.MemberRank;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.plugins.item.RingOfWealthItem;

/**
 * @author Tommeh | 7 sep. 2018 | 19:15:48
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server
 * profile</a>}
 */
public class MarkOfGrace {
    private static final Item MARK_OF_GRACE = new Item(11849);

    public static void spawn(final Player player, final Location[] locations, final int rarity, final int threshold) {
        double random = 6;
        if (player.getSkills().getLevel(Skills.AGILITY) > threshold + 20) {
            random *= 0.8;
        }
        final int endRarity = player.getMemberRank().eligibleTo(MemberRank.RUBY_MEMBER) ? ((int) (0.9F * rarity)) : rarity;
        if (Utils.random(endRarity) < random) {
            if (player.getMemberRank().eligibleTo(MemberRank.SAPPHIRE_MEMBER)) {
                final Item ring = player.getRing();
                if (ring != null) {
                    if (RingOfWealthItem.isRingOfWealth(player.getRing()) && !player.getBooleanSetting(Setting.ROW_CURRENCY_COLLECTOR)) {
                        final Inventory inventory = player.getInventory();
                        if (inventory.getFreeSlots() > 0 || inventory.containsItem(ItemId.MARK_OF_GRACE)) {
                            player.getInventory().addItem(MARK_OF_GRACE);
                            return;
                        }
                    }
                }
            }
            World.spawnFloorItem(MARK_OF_GRACE, locations[Utils.random(locations.length - 1)], player, 10000, 0);
        }
    }
}
