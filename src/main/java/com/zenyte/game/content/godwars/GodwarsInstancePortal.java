package com.zenyte.game.content.godwars;

import com.zenyte.game.content.godwars.instance.*;
import com.zenyte.game.content.godwars.npcs.KillcountNPC;
import com.zenyte.game.world.entity.ImmutableLocation;
import com.zenyte.game.world.entity.Location;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Kris | 14/04/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public enum GodwarsInstancePortal {
    ARMADYL(35016, 4674, 26365, ArmadylInstance.class, new ImmutableLocation(1194, 4243, 0), new ImmutableLocation(2828, 5290, 2), new ImmutableLocation(1197, 4233, 0), KillcountNPC.GodType.ARMADYL),
    BANDOS(35014, 4675, 26366, BandosInstance.class, new ImmutableLocation(1189, 4312, 0), new ImmutableLocation(2854, 5363, 2), new ImmutableLocation(1196, 4303, 0), KillcountNPC.GodType.BANDOS),
    ZAMORAK(35015, 4676, 26363, ZamorakInstance.class, new ImmutableLocation(1206, 4397, 0), new ImmutableLocation(2933, 5357, 2), new ImmutableLocation(1198, 4374, 0), KillcountNPC.GodType.ZAMORAK),
    SARADOMIN(35017, 4677, 26364, SaradominInstance.class, new ImmutableLocation(1199, 4426, 0), new ImmutableLocation(2927, 5258, 0), new ImmutableLocation(1182, 4433, 0), KillcountNPC.GodType.SARADOMIN);
    private static final List<GodwarsInstancePortal> values = Collections.unmodifiableList(Arrays.asList(values()));
    private final int portalObjectId;
    private final int instanceRegionId;
    private final int altarId;
    private final Class<? extends GodwarsInstance> instanceClass;
    private final Location portalLocation;
    private final Location godwarsPortalLocation;
    private final Location altarTeleportLocation;
    private final KillcountNPC.GodType god;

    GodwarsInstancePortal(final int portalObjectId, final int instanceRegionId, final int altarId, final Class<? extends GodwarsInstance> instanceClass, final Location portalLocation, final Location godwarsPortalLocation, final Location altarTeleportLocation, final KillcountNPC.GodType god) {
        this.portalObjectId = portalObjectId;
        this.instanceRegionId = instanceRegionId;
        this.altarId = altarId;
        this.instanceClass = instanceClass;
        this.portalLocation = portalLocation;
        this.godwarsPortalLocation = godwarsPortalLocation;
        this.altarTeleportLocation = altarTeleportLocation;
        this.god = god;
    }

    public static List<GodwarsInstancePortal> getValues() {
        return GodwarsInstancePortal.values;
    }

    public int getChunkX() {
        return (instanceRegionId >> 8) << 3;
    }

    public int getChunkY() {
        return (instanceRegionId & 255) << 3;
    }

    public int getPortalObjectId() {
        return this.portalObjectId;
    }

    public int getInstanceRegionId() {
        return this.instanceRegionId;
    }

    public int getAltarId() {
        return this.altarId;
    }

    public Class<? extends GodwarsInstance> getInstanceClass() {
        return this.instanceClass;
    }

    public Location getPortalLocation() {
        return this.portalLocation;
    }

    public Location getGodwarsPortalLocation() {
        return this.godwarsPortalLocation;
    }

    public Location getAltarTeleportLocation() {
        return this.altarTeleportLocation;
    }

    public KillcountNPC.GodType getGod() {
        return this.god;
    }
}
