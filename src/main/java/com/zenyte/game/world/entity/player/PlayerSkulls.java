package com.zenyte.game.world.entity.player;

public enum PlayerSkulls {

    NONE(-1),
    DEFAULT(0, 3),
    RED_DEFAULT(1),
    HIGH_RISK(2),
    LOOT_KEY_1(8, 15),
    LOOT_KEY_2(9, 16),
    LOOT_KEY_3(10, 17),
    LOOT_KEY_4(11, 18),
    LOOT_KEY_5(12, 19),

    ;

    private final int skullStatusId;
    private final int forinthrySurgeSkullStatusId;

    PlayerSkulls(int skullStatusId, int forinthrySurgeSkullStatusId) {
        this.skullStatusId = skullStatusId;
        this.forinthrySurgeSkullStatusId = forinthrySurgeSkullStatusId;
    }

    PlayerSkulls(int skullStatusId) {
        this.skullStatusId = skullStatusId;
        this.forinthrySurgeSkullStatusId = skullStatusId;
    }

    public final static int LOOT_KEY_ITEM_ID = 26_651;

    public static int getSkull(Player player) {
        if (!player.getVariables().isSkulled()) return NONE.skullStatusId;
        int keyCount = player.getInventory().getAmountOf(LOOT_KEY_ITEM_ID);
        PlayerSkulls skull;
        switch (keyCount) {
            case 1:
                skull = LOOT_KEY_1;
                break;
            case 2:
                skull = LOOT_KEY_2;
                break;
            case 3:
                skull = LOOT_KEY_3;
                break;
            case 4:
                skull = LOOT_KEY_4;
                break;
            case 5:
                skull = LOOT_KEY_5;
                break;
            default:
                skull = DEFAULT;
                break;
        }

        return skull.skullStatusId;
    }

}
