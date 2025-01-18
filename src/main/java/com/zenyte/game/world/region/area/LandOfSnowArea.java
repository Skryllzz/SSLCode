package com.zenyte.game.world.region.area;

import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.region.Area;
import com.zenyte.game.world.region.RSPolygon;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 13/12/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class LandOfSnowArea extends Area {
    public static void spawnPet(@NotNull final Player player) {
        //Just in case, not to override the player's own pet.
        if (player.getFollower() != null) {
            return;
        }
    }

    @Override
    public RSPolygon[] polygons() {
        return new RSPolygon[]{
                new RSPolygon(new int[][]{
                        {2048, 5440},
                        {2048, 5376},
                        {2112, 5376},
                        {2112, 5440}
                }), new RSPolygon(new int[][]{
                {2432, 5440},
                {2432, 5376},
                {2496, 5376},
                {2496, 5440}
        })
        };
    }

    @Override
    public void enter(Player player) {
        if (!player.getPrivilege().eligibleTo(Privilege.SPAWN_ADMINISTRATOR)) {
            player.lock(2);
            player.setLocation(new Location(3091, 3503, 0));
            return;
        }
        //player.getInterfaceHandler().sendInterface(InterfacePosition.OVERLAY, 167);

        //If further enough in quest condition; use static method above to spawn the pet once the player progresses to that point in area.
    }

    @Override
    public void leave(Player player, boolean logout) {
        //player.getInterfaceHandler().closeInterface(InterfacePosition.OVERLAY);
    }

    @Override
    public String name() {
        return "Land of Snow";
    }
}
