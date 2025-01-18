package mgi.types.worldmap;

import com.zenyte.Game;
import mgi.tools.jagcached.cache.Archive;
import mgi.tools.jagcached.cache.File;
import mgi.tools.jagcached.cache.Group;
import mgi.utilities.ByteBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 4-12-2018 | 18:45
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class WorldMapRegion extends WorldMapNode {
    public int compositeMapCheck;
    public int areaCheck;
    public int expectedRegionX;
    public int expectedRegionY;
    public int a;
    public int b;

    public void decode(final ByteBuffer compositeMapBuffer) {
        compositeMapCheck = compositeMapBuffer.readUnsignedByte();
        if (compositeMapCheck != 0) {
            throw new IllegalStateException();
        } else {
            super.minPlane = compositeMapBuffer.readUnsignedByte();
            super.maxPlane = compositeMapBuffer.readUnsignedByte();
            super.centerRegionX = compositeMapBuffer.readUnsignedShort();
            super.centerRegionY = compositeMapBuffer.readUnsignedShort();
            super.regionX = compositeMapBuffer.readUnsignedShort();
            super.regionY = compositeMapBuffer.readUnsignedShort();
            a = compositeMapBuffer.readBigSmart();
            b = compositeMapBuffer.readBigSmart();
            final Archive archive = Game.getCacheMgi().getArchive(18);
            final Group group = archive.findGroupByID(a);
            final File file = group.findFileByID(b);
            final ByteBuffer areaBuffer = file.getData();
            areaBuffer.setPosition(0);
            super.maxPlane = Math.min(maxPlane, 4);
            super.underlays = new short[1][64][64];
            super.overlays = new short[maxPlane][64][64];
            super.overlayShapes = new byte[maxPlane][64][64];
            super.overlayRotations = new byte[maxPlane][64][64];
            super.objects = new WorldMapGameObject[maxPlane][64][64][];
            areaCheck = areaBuffer.readUnsignedByte();
            if (areaCheck != 0) {
                throw new IllegalStateException();
            } else {
                expectedRegionX = areaBuffer.readUnsignedByte();
                expectedRegionY = areaBuffer.readUnsignedByte();
                if (expectedRegionX == regionX && expectedRegionY == regionY) {
                    for (int x = 0; x < 64; ++x) {
                        for (int y = 0; y < 64; ++y) {
                            this.decodeTile(x, y, areaBuffer);
                        }
                    }
                } else {
                    throw new IllegalStateException();
                }
            }
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "WorldMapRegion(super=" + super.toString() + ", compositeMapCheck=" + this.compositeMapCheck + ", areaCheck=" + this.areaCheck + ", expectedRegionX=" + this.expectedRegionX + ", expectedRegionY=" + this.expectedRegionY + ", a=" + this.a + ", b=" + this.b + ")";
    }

    public void setCompositeMapCheck(final int compositeMapCheck) {
        this.compositeMapCheck = compositeMapCheck;
    }

    public void setAreaCheck(final int areaCheck) {
        this.areaCheck = areaCheck;
    }

    public void setExpectedRegionX(final int expectedRegionX) {
        this.expectedRegionX = expectedRegionX;
    }

    public void setExpectedRegionY(final int expectedRegionY) {
        this.expectedRegionY = expectedRegionY;
    }

    public void setA(final int a) {
        this.a = a;
    }

    public void setB(final int b) {
        this.b = b;
    }
}
