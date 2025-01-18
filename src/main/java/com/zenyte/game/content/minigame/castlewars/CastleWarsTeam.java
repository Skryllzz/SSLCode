package com.zenyte.game.content.minigame.castlewars;

import com.zenyte.game.world.entity.Location;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.Arrays;
import java.util.List;

import static com.zenyte.game.content.minigame.castlewars.CastleWarsArea.SARADOMIN_RESPAWN;
import static com.zenyte.game.content.minigame.castlewars.CastleWarsArea.ZAMORAK_RESPAWN;
import static com.zenyte.game.content.minigame.castlewars.CastleWarsLobby.SARADOMIN_LOBBY_SPAWN;
import static com.zenyte.game.content.minigame.castlewars.CastleWarsLobby.ZAMORAK_LOBBY_SPAWN;
import static com.zenyte.plugins.object.CastleWarsLargeDoor.SARADOMIN_DOORS;
import static com.zenyte.plugins.object.CastleWarsLargeDoor.ZAMORAK_DOORS;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public enum CastleWarsTeam {
    SARADOMIN(SARADOMIN_RESPAWN, SARADOMIN_LOBBY_SPAWN, new IntArrayList(SARADOMIN_DOORS), Arrays.asList(CastlewarsRockPatch.SOUTH, CastlewarsRockPatch.EAST)),
    ZAMORAK(ZAMORAK_RESPAWN, ZAMORAK_LOBBY_SPAWN, new IntArrayList(ZAMORAK_DOORS), Arrays.asList(CastlewarsRockPatch.NORTH, CastlewarsRockPatch.WEST));
    public static final CastleWarsTeam[] VALUES = values();
    private static int saraBarricades = 0;
    private static int zamBarricades = 0;
    private final Location respawn;
    private final Location lobbySpawn;
    private final IntList largeCastleDoors;
    private final List<CastlewarsRockPatch> rockPatches;

    CastleWarsTeam(final Location respawn, final Location lobbySpawn, final IntList largeCastleDoors, final List<CastlewarsRockPatch> rockPatches) {
        this.respawn = respawn;
        this.lobbySpawn = lobbySpawn;
        this.largeCastleDoors = largeCastleDoors;
        this.rockPatches = rockPatches;
    }

    public static int getSaraBarricades() {
        return CastleWarsTeam.saraBarricades;
    }

    public static void setSaraBarricades(final int saraBarricades) {
        CastleWarsTeam.saraBarricades = saraBarricades;
    }

    public static int getZamBarricades() {
        return CastleWarsTeam.zamBarricades;
    }

    public static void setZamBarricades(final int zamBarricades) {
        CastleWarsTeam.zamBarricades = zamBarricades;
    }

    public Location getRespawn() {
        return this.respawn;
    }

    public Location getLobbySpawn() {
        return this.lobbySpawn;
    }

    public IntList getLargeCastleDoors() {
        return this.largeCastleDoors;
    }

    public List<CastlewarsRockPatch> getRockPatches() {
        return this.rockPatches;
    }
}
