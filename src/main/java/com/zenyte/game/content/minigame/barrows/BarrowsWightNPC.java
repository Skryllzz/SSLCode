package com.zenyte.game.content.minigame.barrows;

import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;


/**
 * @author Kris | 6. dets 2017 : 1:43.53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public abstract class BarrowsWightNPC extends BarrowsNPC {
    public BarrowsWightNPC(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
        setForceAttackable(true);
    }

    @NotNull
    BarrowsWight getWight() {
        final BarrowsWight wight = Utils.findMatching(BarrowsWight.values, npc -> npc.getNpcId() == getId());
        if (wight == null) {
            throw new RuntimeException("No matching wight found for npc: " + getId());
        }
        return wight;
    }

    @Override
    protected boolean isAcceptableTarget(final Entity entity) {
        if (owner == null || !(entity instanceof Player)) {
            return false;
        }
        final Player owner = this.owner.get();
        if (owner == null) {
            return false;
        }
        return owner.getUsername().equals(((Player) entity).getUsername());
    }

    @Override
    public void onDeath(final Entity source) {
        super.onDeath(source);
        final Player owner = this.owner.get();
        if (owner == null) {
            return;
        }
        owner.getBarrows().removeTarget();
    }

    @Override
    public void onFinish(final Entity source) {
        super.onFinish(source);
        if (source instanceof Player) {
            final Player owner = this.owner.get();
            if (owner == null) {
                return;
            }
            owner.getBarrows().onDeath(this);
        }
    }
}
