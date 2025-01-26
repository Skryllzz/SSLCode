package com.zenyte.plugins.renewednpc;

import com.zenyte.game.world.entity.npc.NpcId;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum ShopNPCHandler {

    AUBURY_RUNES_SHOP("Aubury's Rune Shop", NpcId.AUBURY_11435),
    GORMANS_GOURMET_FOODS("Gorman’s Gourmet Foods", NpcId.GORMAN),
    IRONCLAD_EMPORIUM("Ironclad Emporium", NpcId.TIMOTHY),
    HOSIDIUS_GENERAL_STORE("Hosidius General Store", NpcId.SHOP_KEEPER);

    ShopNPCHandler(final String shop, final int... npcIds) {
        this.npcIds = npcIds;
        this.shop = shop;
    }

    final int[] npcIds;

    final String shop;

    static final ShopNPCHandler[] values = values();

    static final Int2ObjectOpenHashMap<ShopNPCHandler> map = new Int2ObjectOpenHashMap<>();

    static {
        for (final ShopNPCHandler shop : values) {
            for (final int id : shop.npcIds) {
                map.put(id, shop);
            }
        }
    }
}
