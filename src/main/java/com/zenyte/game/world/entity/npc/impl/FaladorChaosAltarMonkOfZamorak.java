package com.zenyte.game.world.entity.npc.impl;

import com.zenyte.Constants;
import com.zenyte.game.util.Direction;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.WalkStep;
import com.zenyte.game.world.entity.npc.NPC;
import com.zenyte.game.world.entity.npc.Spawnable;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.GlobalAreaManager;

/**
 * @author Kris | 25/04/2019 21:09
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class FaladorChaosAltarMonkOfZamorak extends NPC implements Spawnable {
    private static Area chaosTempleArea;
    private final Location stepTile = new Location(0);

    public FaladorChaosAltarMonkOfZamorak(final int id, final Location tile, final Direction facing, final int radius) {
        super(id, tile, facing, radius);
    }

    @Override
    public boolean validate(final int id, final String name) {
        return id == 8400 || id == 8401;
    }

    private Area getChaosTempleArea() {
        if (chaosTempleArea != null) {
            return chaosTempleArea;
        }
        return chaosTempleArea = GlobalAreaManager.get("Chaos Temple");
    }

    @Override
    public boolean addWalkStep(final int nextX, final int nextY, final int lastX, final int lastY, final boolean check) {
        final int dir = Utils.getMoveDirection(nextX - lastX, nextY - lastY);
        if (dir == -1 || !isMovableEntity() || Constants.SPAWN_MODE) {
            return false;
        }
        if (check && !canMove(lastX, lastY, dir)) {
            return false;
        }
        final Area temple = getChaosTempleArea();
        if (temple == null) {
            return false;
        }
        stepTile.setLocation(nextX, nextY, getPlane());
        if (!temple.inside(stepTile)) {
            return false;
        }
        walkSteps.enqueue(WalkStep.getHash(dir, nextX, nextY, check));
        return true;
    }
}
