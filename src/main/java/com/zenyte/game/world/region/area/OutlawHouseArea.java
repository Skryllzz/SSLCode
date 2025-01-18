package com.zenyte.game.world.region.area;

import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.masks.Graphics;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.region.RSPolygon;
import com.zenyte.game.world.region.area.plugins.CycleProcessPlugin;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.Optional;

/**
 * @author Kris | 10/10/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class OutlawHouseArea extends EdgevilleArea implements CycleProcessPlugin {
    private static final Graphics graphics = new Graphics(436);
    private final Object2LongMap<String> mappedPlayers = new Object2LongOpenHashMap<>();
    private int ticks;

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{new RSPolygon(new int[][]{{3112, 3489}, {3112, 3489}, {3112, 3465}, {3129, 3465}, {3129, 3489}})};
    }

    @Override
    public void enter(final Player player) {
        mappedPlayers.put(player.getUsername(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
    }

    @Override
    public void leave(final Player player, final boolean logout) {
        mappedPlayers.removeLong(player.getUsername());
    }

    @Override
    public void process() {
        if (++ticks % 5 != 0) {
            return;
        }
        final long time = System.currentTimeMillis();
        for (final Object2LongMap.Entry<String> entry : mappedPlayers.object2LongEntrySet()) {
            if (time < entry.getLongValue()) {
                continue;
            }
            final Optional<Player> player = World.getPlayer(entry.getKey());
            player.ifPresent(p -> {
                final int prayer = p.getPrayerManager().getPrayerPoints();
                final int special = p.getCombatDefinitions().getSpecialEnergy();
                if (prayer >= p.getSkills().getLevelForXp(Skills.PRAYER) && special >= 100) {
                    return;
                }
                p.setGraphics(graphics);
                p.getPrayerManager().restorePrayerPoints(10);
                p.getCombatDefinitions().setSpecialEnergy(Math.min(100, special + 10));
            });
        }
    }
}
