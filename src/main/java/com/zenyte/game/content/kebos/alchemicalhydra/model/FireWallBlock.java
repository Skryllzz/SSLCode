package com.zenyte.game.content.kebos.alchemicalhydra.model;

import com.zenyte.game.util.Direction;
import com.zenyte.game.world.entity.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * @author Kris | 10/11/2019
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class FireWallBlock {
    private final Set<Location> tiles;
    private final Direction direction;
    private final Location movingFireLocation;

    public FireWallBlock(final Set<Location> tiles, final Direction direction, final Location movingFireLocation) {
        this.tiles = tiles;
        this.direction = direction;
        this.movingFireLocation = movingFireLocation;
    }

    public Set<Location> getTiles() {
        return this.tiles;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Location getMovingFireLocation() {
        return this.movingFireLocation;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof FireWallBlock other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$tiles = this.getTiles();
        final Object other$tiles = other.getTiles();
        if (!Objects.equals(this$tiles, other$tiles)) return false;
        final Object this$direction = this.getDirection();
        final Object other$direction = other.getDirection();
        if (!Objects.equals(this$direction, other$direction)) return false;
        final Object this$movingFireLocation = this.getMovingFireLocation();
        final Object other$movingFireLocation = other.getMovingFireLocation();
        return Objects.equals(this$movingFireLocation, other$movingFireLocation);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof FireWallBlock;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $tiles = this.getTiles();
        result = result * PRIME + ($tiles == null ? 43 : $tiles.hashCode());
        final Object $direction = this.getDirection();
        result = result * PRIME + ($direction == null ? 43 : $direction.hashCode());
        final Object $movingFireLocation = this.getMovingFireLocation();
        result = result * PRIME + ($movingFireLocation == null ? 43 : $movingFireLocation.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "FireWallBlock(tiles=" + this.getTiles() + ", direction=" + this.getDirection() + ", movingFireLocation=" + this.getMovingFireLocation() + ")";
    }
}
