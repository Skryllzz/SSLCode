package com.zenyte.game.content.boss.cerberus;

import com.zenyte.game.content.boss.cerberus.area.CerberusLair;
import com.zenyte.game.content.boss.cerberus.area.EasternCerberusLair;
import com.zenyte.game.content.boss.cerberus.area.NorthernCerberusArea;
import com.zenyte.game.content.boss.cerberus.area.WesternCerberusArea;
import com.zenyte.game.world.entity.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Kris | 22. march 2018 : 16:17.44
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum CerberusRoom {
    WEST(4883, WesternCerberusArea.class, new Location(1289, 1252, 0), new Location(1240, 1226, 0)),
    NORTH(5140, NorthernCerberusArea.class, new Location(1310, 1273, 0), new Location(1304, 1290, 0)),
    EAST(5395, EasternCerberusLair.class, new Location(1332, 1252, 0), new Location(1368, 1226, 0));
    private static final List<CerberusRoom> values = Collections.unmodifiableList(Arrays.asList(values()));
    private final int regionId;
    private final Class<? extends CerberusLair> clazz;
    private final Location entrance;
    private final Location exit;

    CerberusRoom(final int regionId, final Class<? extends CerberusLair> clazz, final Location entrance, final Location exit) {
        this.regionId = regionId;
        this.clazz = clazz;
        this.entrance = entrance;
        this.exit = exit;
    }

    public static List<CerberusRoom> getValues() {
        return CerberusRoom.values;
    }

    public Location getWesternFireLocation() {
        return exit.transform(-1, 16, 0);
    }

    public Location getWesternSoulLocation() {
        return exit.transform(-1, 39, 0);
    }

    public int getRegionId() {
        return this.regionId;
    }

    public Class<? extends CerberusLair> getClazz() {
        return this.clazz;
    }

    public Location getEntrance() {
        return this.entrance;
    }

    public Location getExit() {
        return this.exit;
    }
}
