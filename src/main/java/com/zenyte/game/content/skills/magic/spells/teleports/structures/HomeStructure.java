package com.zenyte.game.content.skills.magic.spells.teleports.structures;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.content.skills.magic.spells.teleports.TeleportType;
import com.zenyte.game.item.Item;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Action;
import com.zenyte.game.world.entity.player.Player;

import com.zenyte.game.content.skills.magic.spells.teleports.Teleport;
import com.zenyte.game.world.entity.player.Player;

/**
 * HomeStructure implementation for proper teleportation with animations, as defined in the 'home' command.
 */
public class HomeStructure implements TeleportStructure {

    @Override
    public void start(final Player player, final Teleport givenTeleport) {
        // Define the home teleport with the desired type and destination.
        final Teleport teleport = new Teleport() {
            @Override
            public TeleportType getType() {
                return TeleportType.ZENYTE_PORTAL_TELEPORT;
            }

            @Override
            public Location getDestination() {
                return new Location(1757, 3592, 0);
            }

            @Override
            public int getLevel() {
                return 0;
            }

            @Override
            public double getExperience() {
                return 0;
            }

            @Override
            public int getRandomizationDistance() {
                return 3; // Randomization distance for the destination (3 tiles max).
            }

            @Override
            public Item[] getRunes() {
                return new Item[0];
            }

            @Override
            public int getWildernessLevel() {
                return 20;
            }

            @Override
            public boolean isCombatRestricted() {
                return true;
            }
        };
        teleport.teleport(player);
    }

    @Override
    public void end(final Player player, final Teleport teleport) {
        // No additional logic is required after completing the teleport.
    }
}