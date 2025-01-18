package com.zenyte.game.content.theatreofblood;

import com.zenyte.game.content.theatreofblood.boss.TheatreArea;
import com.zenyte.game.content.theatreofblood.boss.maidenofsugadinti.MaidenOfSugadintiRoom;
import com.zenyte.game.content.theatreofblood.boss.nylocas.NylocasRoom;
import com.zenyte.game.content.theatreofblood.boss.pestilentbloat.PestilentBloatRoom;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.ShadowRealmArea;
import com.zenyte.game.content.theatreofblood.boss.sotetseg.SotetsegRoom;
import com.zenyte.game.content.theatreofblood.boss.verzikvitur.VerzikRoom;
import com.zenyte.game.content.theatreofblood.boss.xarpus.XarpusRoom;
import com.zenyte.game.content.theatreofblood.reward.RewardRoom;

/**
 * @author Tommeh | 5/22/2020 | 4:32 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public enum TheatreRoom {
    THE_MAIDEN_OF_SUGADINTI("The Maiden of Sugadinti", 1, 392, 552, 12, 7, MaidenOfSugadintiRoom.class), THE_PESTILENT_BLOAT("The Pestilent Bloat", 2, 408, 552, 8, 8, PestilentBloatRoom.class), THE_NYLOCAS("The Nylocas", 3, 408, 528, 8, 8, NylocasRoom.class), SOTETSEG("Sotetseg", 4, 408, 536, 8, 8, SotetsegRoom.class), SHADOW_REALM("Shadow Realm", 4, 416, 536, 8, 8, ShadowRealmArea.class), XARPUS("Xarpus", 5, 392, 544, 8, 8, XarpusRoom.class), VERZIK("The Final Challenge", 6, 392, 535, 8, 8, VerzikRoom.class), REWARD("Verzik Vitur\'s Vault", 7, 403, 538, 8, 8, RewardRoom.class);
    private final String name;
    private final int wave;
    private final int chunkX;
    private final int chunkY;
    private final int sizeX;
    private final int sizeY;
    private final Class<? extends TheatreArea> clazz;

    public String getName() {
        return this.name;
    }

    public int getWave() {
        return this.wave;
    }

    public int getChunkX() {
        return this.chunkX;
    }

    public int getChunkY() {
        return this.chunkY;
    }

    public int getSizeX() {
        return this.sizeX;
    }

    public int getSizeY() {
        return this.sizeY;
    }

    public Class<? extends TheatreArea> getClazz() {
        return this.clazz;
    }

    private TheatreRoom(final String name, final int wave, final int chunkX, final int chunkY, final int sizeX, final int sizeY, final Class<? extends TheatreArea> clazz) {
        this.name = name;
        this.wave = wave;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.clazz = clazz;
    }
}
