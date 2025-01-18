package com.zenyte.game.util;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.player.MessageType;
import com.zenyte.game.world.entity.player.Player;
import mgi.types.config.items.ItemDefinitions;

public class Examine {
    private final int id;
    private final String examine;

    public Examine(final int id, final String examine) {
        this.id = id;
        this.examine = examine;
    }

    public static void sendItemExamine(final Player player, final Item item) {
        if (item == null) {
            return;
        }
        sendItemExamine(player, item.getId());
    }

    public static void sendItemExamine(final Player player, final int id) {
        final ItemDefinitions definitions = ItemDefinitions.getOrThrow(id);
        if (definitions.isNoted()) {
            player.sendMessage("Swap this note at any bank for the equivalent item", MessageType.EXAMINE_ITEM);
            return;
        }
        final String examine = definitions.getExamine();
        if (examine == null || examine.isEmpty()) {
            return;
        }
        player.sendMessage(examine, MessageType.EXAMINE_ITEM);
    }

    public int getId() {
        return this.id;
    }

    public String getExamine() {
        return this.examine;
    }
}
