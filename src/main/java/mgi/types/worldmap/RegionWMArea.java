package mgi.types.worldmap;

import mgi.utilities.ByteBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 2-12-2018 | 19:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class RegionWMArea implements WorldMapType {
    private int plane;
    private int numberOfPlanes;
    private int minRegionX;
    private int minRegionY;
    private int maxRegionX;
    private int maxRegionY;

    @Override
    public void decode(final ByteBuffer buffer) {
        plane = buffer.readUnsignedByte();
        numberOfPlanes = buffer.readUnsignedByte();
        minRegionX = buffer.readUnsignedShort();
        minRegionY = buffer.readUnsignedShort();
        maxRegionX = buffer.readUnsignedShort();
        maxRegionY = buffer.readUnsignedShort();
    }

    @Override
    public void encode(ByteBuffer buffer) {
        buffer.writeByte(1);
        buffer.writeByte(plane);
        buffer.writeByte(numberOfPlanes);
        buffer.writeShort(minRegionX);
        buffer.writeShort(minRegionY);
        buffer.writeShort(maxRegionX);
        buffer.writeShort(maxRegionY);
    }

    public int getPlane() {
        return this.plane;
    }

    public int getNumberOfPlanes() {
        return this.numberOfPlanes;
    }

    public int getMinRegionX() {
        return this.minRegionX;
    }

    public int getMinRegionY() {
        return this.minRegionY;
    }

    public int getMaxRegionX() {
        return this.maxRegionX;
    }

    public int getMaxRegionY() {
        return this.maxRegionY;
    }

    @NotNull
    @Override
    public String toString() {
        return "RegionWMArea(plane=" + this.getPlane() + ", numberOfPlanes=" + this.getNumberOfPlanes() + ", minRegionX=" + this.getMinRegionX() + ", minRegionY=" + this.getMinRegionY() + ", maxRegionX=" + this.getMaxRegionX() + ", maxRegionY=" + this.getMaxRegionY() + ")";
    }
}
