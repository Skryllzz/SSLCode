package com.zenyte.game.content.minigame.castlewars;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class CastleWarsOverlay extends Interface {

    public static void processVarbits(final Player player) {
        final CastleWarsTeam team = CastleWars.getTeam(player);
        final int currentTime = Math.round((int) Math.floor(CastleWarsArea.getTicks() / 100));
        for (final CWarsOverlayVarbit varbit : CWarsOverlayVarbit.VALUES) {
            // skip the sending of any duplicate values
            if (player.getVarManager().getBitValue(varbit.getId()) == CastleWars.getVarbit(team, varbit)) continue;
            player.getVarManager().sendBit(varbit.getId(), CastleWars.getVarbit(team, varbit));
        }
        if (player.getVarManager().getValue(CastleWarsLobbyOverlay.TIMER_VARP) != currentTime) {
            player.getVarManager().sendVar(CastleWarsLobbyOverlay.TIMER_VARP, currentTime);
        }
    }

    @Override
    protected void attach() {
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    protected void build() {
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.CASTLE_WARS_OVERLAY;
    }

    public enum CWarsOverlayVarbit {
        DOOR_HEALTH(136, 100, false),
        DOOR_LOCK(137, 0, false),
        SARADOMIN_FLAG(143, 0, true),
        SARADOMIN_SCORE(145, 0, true),
        ZAMORAK_FLAG(153, 0, true),
        ZAMORAK_SCORE(155, 0, true),
        ROCKS_NS(138, 0, false),
        ROCKS_EW(139, 0, false),
        CATAPULT(140, 0, false);
        public static final CWarsOverlayVarbit[] VALUES = values();
        private final int id;
        private final int defaultV;
        private final boolean universal;

        CWarsOverlayVarbit(final int id, final int defaultV, final boolean universal) {
            this.id = id;
            this.defaultV = defaultV;
            this.universal = universal;
        }

        public int getId() {
            return this.id;
        }

        public int getDefaultV() {
            return this.defaultV;
        }

        public boolean isUniversal() {
            return this.universal;
        }
    }
}
