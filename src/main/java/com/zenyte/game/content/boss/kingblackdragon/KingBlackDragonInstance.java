package com.zenyte.game.content.boss.kingblackdragon;

import com.zenyte.game.content.achievementdiary.diaries.WildernessDiary;
import com.zenyte.game.item.Item;
import com.zenyte.game.util.Colour;
import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.region.DynamicArea;
import com.zenyte.game.world.region.area.plugins.LootBroadcastPlugin;
import com.zenyte.game.world.region.dynamicregion.AllocatedArea;

import java.lang.ref.WeakReference;

/**
 * @author Kris | 28/11/2018 17:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class KingBlackDragonInstance extends DynamicArea implements LootBroadcastPlugin {
    public static final Item price = new Item(995, 50000);
    public static final Location outsideTile = new Location(3067, 10253, 0);
    public static final Location insideTile = new Location(2271, 4680, 0);
    private final WeakReference<Player> player;

    public KingBlackDragonInstance(final Player player, final AllocatedArea allocatedArea, final int copiedChunkX, final int copiedChunkY) {
        super(allocatedArea, copiedChunkX, copiedChunkY);
        this.player = new WeakReference<>(player);
    }

    @Override
    public void constructed() {
        new KingBlackDragon(239, getLocation(2270, 4695, 0), Direction.SOUTH, 5).spawn();
        final Player p = player.get();
        if (p != null) {
            p.setLocation(getLocation(insideTile));
        }
    }

    @Override
    public void enter(Player player) {
        player.setViewDistance(Player.LARGE_VIEWPORT_RADIUS);
        player.getAchievementDiaries().update(WildernessDiary.ENTER_KING_BLACK_DRAGON_LAIR);
        player.sendMessage(Colour.RED.wrap("Should you die in the instance, your items will be permanently lost!"));
        if (player.getNumericAttribute("king black dragon instance warning").intValue() == 0) {
            player.addAttribute("king black dragon instance warning", 1);
            player.getDialogueManager().start(new Dialogue(player) {
                @Override
                public void buildDialogue() {
                    plain(Colour.RED.wrap("WARNING: ") + "If you die in this instance, your items will be permanently lost!");
                }
            });
        }
    }

    @Override
    public void leave(Player player, boolean logout) {
        player.resetViewDistance();
    }

    @Override
    public Location onLoginLocation() {
        return outsideTile;
    }

    @Override
    public String name() {
        return player.get() + "'s King Black Dragon instance";
    }
}
