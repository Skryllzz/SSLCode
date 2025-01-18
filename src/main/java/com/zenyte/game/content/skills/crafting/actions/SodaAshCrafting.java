package com.zenyte.game.content.skills.crafting.actions;

import com.zenyte.game.content.skills.cooking.CookingDefinitions;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.Action;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class SodaAshCrafting extends Action {
    public static final Item SEAWEED = new Item(401);
    public static final Item SODA_ASH = new Item(1781);
    private final int amount;
    private final boolean range;
    private int cycle;

    public SodaAshCrafting(final int amount, final boolean range) {
        this.amount = amount;
        this.range = range;
    }

    @Override
    public boolean start() {
        return player.getInventory().containsItem(SEAWEED);
    }

    @Override
    public boolean process() {
        if (!player.getInventory().containsItem(SEAWEED)) {
            return false;
        }
        return cycle < amount;
    }

    @Override
    public int processWithDelay() {
        player.setAnimation(range ? CookingDefinitions.STOVE : CookingDefinitions.FIRE);
        player.getInventory().deleteItemsIfContains(new Item[]{SEAWEED}, () -> {
            player.getInventory().addItem(SODA_ASH);
            player.sendFilteredMessage("You heat the seaweed and create soda ash.");
        });
        cycle++;
        return 1;
    }
}
