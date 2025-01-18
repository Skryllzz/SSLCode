package com.zenyte.game.world.flooritem;

import com.zenyte.game.item.Item;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Kris | 11. march 2018 : 22:55.48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public class FloorItem extends Item {
    protected final Location location;
    protected transient String ownerName;
    protected transient String receiverName;
    protected transient int invisibleTicks;
    protected transient int visibleTicks;
    private transient boolean visibleToIronmenOnly;

    public FloorItem(final Item item, final Location location, final Player owner, final Player receiver, final int invisibleTicks, final int visibleTicks) {
        super(item.getId(), item.getAmount(), item.getAttributesCopy());
        this.location = new Location(location);
        if (owner != null) {
            ownerName = owner.getUsername();
        }
        if (receiver != null) {
            receiverName = receiver.getUsername();
        }
        this.invisibleTicks = invisibleTicks;
        this.visibleTicks = visibleTicks;
    }

    public final boolean isVisibleTo(final Player player) {
        if (invisibleTicks <= 0) {
            return true;
        }
        if (receiverName == null) {
            if (visibleToIronmenOnly) {
                return player != null && player.isIronman();
            }
            return true;
        }
        if (player == null) {
            return false;
        }
        return receiverName.equals(player.getUsername());
    }

    public boolean isReceiver(final Player player) {
        if (receiverName == null || player == null) {
            return false;
        }
        return receiverName.equals(player.getUsername());
    }

    public boolean isOwner(final Player player) {
        if (ownerName == null || player == null) {
            return false;
        }
        return ownerName.equals(player.getUsername());
    }

    public boolean hasOwner() {
        return ownerName != null;
    }

    @Override
    public String toString() {
        return getName() + " [id: " + getId() + ", amount: " + getAmount() + "] - " + location.toString();
    }

    public boolean isPresent() {
        return World.getRegion(location.getRegionId(), true).getFloorItem(this.getId(), location) != null;
    }

    public Location getLocation() {
        return this.location;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

    public int getInvisibleTicks() {
        return this.invisibleTicks;
    }

    public void setInvisibleTicks(final int invisibleTicks) {
        this.invisibleTicks = invisibleTicks;
    }

    public int getVisibleTicks() {
        return this.visibleTicks;
    }

    public void setVisibleTicks(final int visibleTicks) {
        this.visibleTicks = visibleTicks;
    }

    public boolean isVisibleToIronmenOnly() {
        return this.visibleToIronmenOnly;
    }

    public void setVisibleToIronmenOnly(final boolean visibleToIronmenOnly) {
        this.visibleToIronmenOnly = visibleToIronmenOnly;
    }
}
