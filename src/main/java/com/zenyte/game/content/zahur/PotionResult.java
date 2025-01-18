package com.zenyte.game.content.zahur;

import com.zenyte.game.content.consumables.drinks.Potion;
import com.zenyte.game.item.Item;
import mgi.types.config.items.ItemDefinitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Tommeh | 16-3-2019 | 00:35
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class PotionResult {
    private final List<Item> items;
    private final Potion potion;

    public PotionResult(final Potion potion, final Item... items) {
        this.items = new ArrayList<>();
        this.items.addAll(Arrays.asList(items));
        this.potion = potion;
    }

    public void add(final Item item) {
        items.add(item);
    }

    public int getTotalDose() {
        int dose = 0;
        for (final Item item : items) {
            final ItemDefinitions def = item.getDefinitions();
            final int id = def.getUnnotedOrDefault();
            dose += potion.getDoses(id) * item.getAmount();
        }
        return dose;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public Potion getPotion() {
        return this.potion;
    }
}
