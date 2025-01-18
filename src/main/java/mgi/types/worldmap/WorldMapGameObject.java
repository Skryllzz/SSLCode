package mgi.types.worldmap;


import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 4-12-2018 | 20:13
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class WorldMapGameObject {
    private int id;
    private int type;
    private int rotation;

    public int getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }

    public int getRotation() {
        return this.rotation;
    }

    public WorldMapGameObject(final int id, final int type, final int rotation) {
        this.id = id;
        this.type = type;
        this.rotation = rotation;
    }

    @NotNull
    @Override
    public String toString() {
        return "WorldMapGameObject(id=" + this.getId() + ", type=" + this.getType() + ", rotation=" + this.getRotation() + ")";
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public void setRotation(final int rotation) {
        this.rotation = rotation;
    }
}
