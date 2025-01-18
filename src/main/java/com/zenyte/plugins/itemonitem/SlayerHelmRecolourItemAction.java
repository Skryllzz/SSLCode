package com.zenyte.plugins.itemonitem;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemId;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.item.SlayerHelm;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * @author Tommeh | 19 mei 2018 | 16:51:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public final class SlayerHelmRecolourItemAction implements ItemOnItemAction {
    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        final Item helmet = from.getId() == 11864 || from.getId() == 11865 ? from : to;
        final Item recolour = from.getId() != 11864 && from.getId() != 11865 ? from : to;
        for (final SlayerHelm.SlayerHelmRecolour r : SlayerHelm.SlayerHelmRecolour.values) {
            final String reward = r.getSlayerReward();
            final int base = r.getBase();
            final int helm = r.getHelm();
            if (recolour.getId() == base && (helmet.getId() == 11864 || helmet.getId() == 11865)) {
                if (!player.getSlayer().isUnlocked(reward)) {
                    player.sendMessage("You need to unlock the slayer ability <col=00080>" + reward + "</col> before you can do this recolour.");
                    return;
                }
                player.getInventory().deleteItemsIfContains(new Item[]{new Item(base), helmet}, () -> {
                    player.getInventory().addItem(new Item(helm + (helmet.getId() == 11864 ? 0 : 2)));
                    player.sendMessage("You successfully recoloured your slayer helmet.");
                });
				/*player.getInventory().deleteItem(base, 1);
				player.getInventory().deleteItem(helmet);*/
                return;
            }
        }
    }

    @Override
    public int[] getItems() {
        final IntArrayList list = new IntArrayList();
        for (final SlayerHelm.SlayerHelmRecolour recolour : SlayerHelm.SlayerHelmRecolour.values) {
            list.add(recolour.getBase());
        }
        list.add(ItemId.SLAYER_HELMET);
        list.add(ItemId.SLAYER_HELMET_I);
        return list.toArray(new int[list.size()]);
    }
}
