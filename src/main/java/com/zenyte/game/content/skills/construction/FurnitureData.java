package com.zenyte.game.content.skills.construction;

import com.google.gson.annotations.Expose;
import com.zenyte.game.content.skills.construction.constants.Furniture;
import com.zenyte.game.content.skills.construction.constants.FurnitureSpace;
import com.zenyte.game.world.entity.Location;

public class FurnitureData {
    @Expose
    private FurnitureSpace space;
    @Expose
    private Furniture furniture;
    @Expose
    private Location location;
    @Expose
    private int type;
    @Expose
    private int rotation;

    public FurnitureData(FurnitureSpace space, Furniture furniture, Location location, int type, int rotation) {
        this.space = space;
        this.furniture = furniture;
        this.location = location;
        this.type = type;
        this.rotation = rotation;
    }

    public FurnitureSpace getSpace() {
        return this.space;
    }

    public void setSpace(final FurnitureSpace space) {
        this.space = space;
    }

    public Furniture getFurniture() {
        return this.furniture;
    }

    public void setFurniture(final Furniture furniture) {
        this.furniture = furniture;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    public int getType() {
        return this.type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(final int rotation) {
        this.rotation = rotation;
    }
}
