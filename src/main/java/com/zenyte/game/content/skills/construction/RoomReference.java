package com.zenyte.game.content.skills.construction;

import com.google.gson.annotations.Expose;
import com.zenyte.game.content.skills.construction.constants.Furniture;
import com.zenyte.game.content.skills.construction.constants.FurnitureSpace;
import com.zenyte.game.content.skills.construction.constants.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomReference {
    @Expose
    private Room room;
    @Expose
    private int x;
    @Expose
    private int y;
    @Expose
    private int plane;
    @Expose
    private int rotation;
    @Expose
    private List<FurnitureData> furnitureData;

    public RoomReference(Room room, int positionX, int positionY, int plane, int rotation) {
        this.room = room;
        this.x = positionX;
        this.y = positionY;
        this.plane = plane;
        this.rotation = rotation;
        furnitureData = new ArrayList<>();
    }

    public FurnitureData getFurniture(Furniture furniture) {
        for (FurnitureData data : furnitureData) {
            if (data.getFurniture() == furniture) return data;
        }
        return null;
    }

    public FurnitureData getFurniture(FurnitureSpace space) {
        for (FurnitureData data : furnitureData) {
            if (data.getSpace() == space) return data;
        }
        return null;
    }

    public FurnitureData getFurniture(final String... furniture) {
        for (FurnitureData data : furnitureData) {
            for (String furn : furniture) {
                if (data.getFurniture().name().contains(furn)) return data;
            }
        }
        return null;
    }

    public FurnitureData getStaircase() {
        for (FurnitureData data : furnitureData) {
            final Furniture furn = data.getFurniture();
            if (furn.ordinal() >= Furniture.OAK_STAIRCASE_DS.ordinal() && furn.ordinal() <= Furniture.MARBLE_SPIRAL.ordinal())
                return data;
        }
        return null;
    }

    public FurnitureData getCarpet() {
        for (FurnitureData data : furnitureData) {
            final Furniture furn = data.getFurniture();
            if (furn == Furniture.RUG || furn == Furniture.BROWN_RUG || furn == Furniture.OPULENT_RUG) return data;
        }
        return null;
    }

    public Room getRoom() {
        return this.room;
    }

    public void setRoom(final Room room) {
        this.room = room;
    }

    public int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public int getPlane() {
        return this.plane;
    }

    public void setPlane(final int plane) {
        this.plane = plane;
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(final int rotation) {
        this.rotation = rotation;
    }

    public List<FurnitureData> getFurnitureData() {
        return this.furnitureData;
    }

    public void setFurnitureData(final List<FurnitureData> furnitureData) {
        this.furnitureData = furnitureData;
    }
}
