package com.zenyte.plugins.itemonitem;

import com.zenyte.game.content.skills.magic.Rune;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.ItemOnItemAction;
import com.zenyte.game.item.PairedItemOnItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.RunePouch;

/**
 * @author Kris | 11. nov 2017 : 0:19.35
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class RuneOnRunePouchItemAction implements PairedItemOnItemPlugin {
    @Override
    public void handleItemOnItemAction(final Player player, final Item from, final Item to, final int fromSlot, final int toSlot) {
        final int id = to.getId() == RunePouch.RUNE_POUCH.getId() ? from.getId() : to.getId();
        final Item rune = new Item(id, player.getInventory().getAmountOf(id));
        int amount = rune.getAmount();
        final int inPouch = player.getRunePouch().getAmountOf(id);
        if ((amount + (long) inPouch) >= 16000) {
            amount = 16000 - inPouch;
        }
        rune.setAmount(amount);
        if (amount <= 0) {
            player.sendMessage("You can't put that many runes in your pouch.");
            return;
        }
        final int slot = to.getId() == RunePouch.RUNE_POUCH.getId() ? fromSlot : toSlot;
        final Rune r = Rune.getRune(rune);
        if (r == null) {
            player.sendMessage("You can only add runes to the rune pouch.");
            return;
        }
        if (player.getRunePouch().getContainer().getSize() == 3) {
            if (player.getRunePouch().getAmountOf(id) == 0) {
                player.sendMessage("You can only carry three different types of runes in your rune pouch at a time.");
                return;
            }
        }
        player.getRunePouch().getContainer().deposit(player, player.getInventory().getContainer(), slot, rune.getAmount());
        player.getInventory().refreshAll();
        player.getRunePouch().getContainer().refresh(player);
    }

    @Override
    public ItemPair[] getMatchingPairs() {
        final ItemOnItemAction.ItemPair[] pairs = new ItemPair[Rune.values.length];
        for (int i = 0; i < pairs.length; i++) {
            final Rune rune = Rune.values[i];
            pairs[i] = new ItemPair(RunePouch.RUNE_POUCH.getId(), rune.getId());
        }
        return pairs;
    }

    @Override
    public int[] getItems() {
        return null;
    }
}
