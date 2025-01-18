package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.WalkStep;
import com.zenyte.game.world.entity.pathfinding.RouteFinder;
import com.zenyte.game.world.entity.pathfinding.RouteResult;
import com.zenyte.game.world.entity.pathfinding.strategy.TileStrategy;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.utils.IntLinkedList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 25-1-2019 | 21:33
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MoveGameClickEvent implements ClientProtEvent {
    private final int offsetX;
    private final int offsetY;
    private final int type;

    public MoveGameClickEvent(final int offsetX, final int offsetY, final int type) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.type = type;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "[" + player.getX() + ", " + player.getY() + ", " + player.getPlane() + "] -> [" + offsetX + ", " + offsetY + ", " + player.getPlane() + "]; type: " + type);
    }

    @Override
    public void handle(Player player) {
        if (player.isLocked() || type < 0 || type > 2 || player.isFullMovementLocked()) {
            player.getPacketDispatcher().resetMapFlag();
            return;
        }
        final RouteResult route = RouteFinder.findRoute(player.getX(), player.getY(), player.getPlane(), player.getSize(), new TileStrategy(offsetX, offsetY), true);
        final int steps = route.getSteps();
        final int[] bufferX = route.getXBuffer();
        final int[] bufferY = route.getYBuffer();
        player.stop(Player.StopType.INTERFACES, Player.StopType.WALK, Player.StopType.ACTIONS, Player.StopType.ROUTE_EVENT);
        if (type == 2) {
            if (player.eligibleForShiftTeleportation()) {
                player.setLocation(new Location(offsetX, offsetY, player.getPlane()));
                return;
            }
            player.setRun(true);
        }
        if (player.isFrozen()) {
            player.sendMessage("A magical force stops you from moving.");
            return;
        }
        if (player.isStunned()) {
            player.sendMessage("You're stunned.");
            return;
        }
        if (player.isMovementLocked(true) || player.isLocked()) {
            return;
        }
        for (int i = steps - 1; i >= 0; i--) {
            if (!player.addWalkSteps(bufferX[i], bufferY[i], 60, true)) {
                break;
            }
        }
        final IntLinkedList walksteps = player.getWalkSteps();
        if (walksteps.isEmpty()) {
            return;
        }
        final int hash = walksteps.getLast();
        final Location tile = new Location(WalkStep.getNextX(hash), WalkStep.getNextY(hash), player.getPlane());
        player.getPacketDispatcher().sendMapFlag(tile.getXInScene(player), tile.getYInScene(player));
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    @NotNull
    @Override
    public String toString() {
        return "MoveGameClickEvent(offsetX=" + this.offsetX + ", offsetY=" + this.offsetY + ", type=" + this.type + ")";
    }
}
