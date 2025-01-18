package mgi.types.config;

/**
 * @author Jire
 */
public enum Edenify {
    //@formatter:off
    MODELS(7, -1, 100_000),
    ITEMS(2, 10, 30223 + 1),
    ANIMS(2, 12, 0),
    NPCS(2, 9, 20_000),
    SKELETONS(0, -1, 0),
    VARBIT(2, 14, 0),
    VARCLIENT(2, 19, 0),
    VARPLAYER(2, 16, 20_000),
    OBJECTS(2, 6, 100_000),
    UNDERLAYS(2, 1, 0),
    OVERLAYS(2, 4, 0),
    SKINS(1, -1, 0),
    MAPS(5, -1, 20_000),
    GRAPHICS(2, 13, 0),
    SPRITES(8, -1, 10_000),
    INTERFACES(3, -1, 10_000),
    CLIENTSCRIPTS(12, -1, 20_000),
    ENUMS(2, 8, 20_000),
    FONTS(13, -1, 0),
    SOUND_EFFECTS_1(4, -1, 0),
    SOUND_EFFECTS_2(14, -1, 0),
    SOUND_EFFECTS_3(15, -1, 0),
    MUSIC_1(6, -1, 0),
    MUSIC_2(11, -1, 0),
    HIT_SPLATS(2, 32, 64),
    HIT_BARS(2, 33, 64),
    MAP_LABELS(2, 35, 10_000),
    STRUCTS(2, 34, 20_000),
    PARAMS(2, 11, 20_000),
    INV(2, 5, 20_000),
    ;
    //@formatter:on

    private final int archive;
    private final int group;
    private final int offset;

    Edenify(int archive, int group, int offset) {
        this.archive = archive;
        this.group = group;
        this.offset = 0;//offset;
    }

    public int getArchive() {
        return archive;
    }

    public int getGroup() {
        return group;
    }

    public int getOffset() {
        return offset;
    }

    public boolean is(int id) {
        return id >= getOffset();
    }

    public int offsetted(int value, int offset) {
        return value + offset;
    }

    public int offsetted(int value) {
        return offsetted(value, getOffset());
    }

    public int offset(boolean condition, int value, Edenify by) {
        return condition ? by.offsetted(value) : value;
    }

    public int offset(int id, int value, Edenify by) {
        return offset(is(id), value, by);
    }

    public int offset(int id, int value) {
        return offset(id, value, this);
    }

    public boolean is65535(int value) {
        return value == 65535;
    }

    public int offset65535(int id, int value, Edenify by) {
        return is65535(value) ? -1 : offset(id, value, by);
    }

    public int offset65535(int id, int value) {
        return is65535(value) ? -1 : offset(id, value);
    }

    public static int npc(int id) {
        return NPCS.offsetted(id);
    }

    public static int animation(int id) {
        return ANIMS.offsetted(id);
    }

    public static int graphics(int id) {
        return GRAPHICS.offsetted(id);
    }

    public static int object(int id) {
        return OBJECTS.offsetted(id);
    }

    public static int item(int id) {
        return Edenify.ITEMS.offsetted(id);
    }

    public static boolean offsetObjects(int regionID) {
        switch (regionID) {
            case 11828:
            case 11829:
            case 11830:
            case 11831:
            case 12084:
            case 12085:
            case 12086:
            case 12087:
            case 12340:
            case 12341:
            case 12342:
            case 12343:
            case 12596:
            case 12597:
            case 12598:
            case 12599:
                return true;
            default:
                return MAPS.is(regionID);
        }
    }

    public static int offsetObject(int regionID, int objectID) {
        return Edenify.MAPS.offset(offsetObjects(regionID), objectID, Edenify.OBJECTS);
    }

}
