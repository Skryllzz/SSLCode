package com.zenyte.game.content.theatreofblood.interfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.theatreofblood.area.VerSinhazaArea;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.util.Colour;
import com.zenyte.game.world.entity.player.Player;

/**
 * @author Tommeh | 5/21/2020 | 5:55 PM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class PartiesOverviewInterface extends Interface {
    @Override
    protected void attach() {
        put(3, 0, "Refresh");
        put(3, 1, "My/Make Party");
        put(3, 2, "Friends/Clan");
        put(17, "View Party");
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
    }

    @Override
    protected void build() {
        bind("Refresh", PartiesOverviewInterface::refresh);
        bind("My/Make Party", player -> {
            var party = VerSinhazaArea.getParty(player);
            if (party == null) {
                party = VerSinhazaArea.createParty(player);
            }
            party.updateInformation(player);
            PartyOverlayInterface.refresh(player, party);
        });
        bind("Friends/Clan", player -> {
           player.getVarManager().flipBit(12988);
           refresh(player);
        });
        bind("View Party", (player, slotId, itemId, option) -> {
            final var party = VerSinhazaArea.getPartyByIndex(slotId);
            if (party == null) {
                refresh(player);
                return;
            }
            party.updateInformation(player);
        });
    }

    public static void refresh(final Player player) {
        final var dispatcher = player.getPacketDispatcher();
        player.getVarManager().sendVar(1740, VerSinhazaArea.getParty(player) != null ? 0 : -1);
        GameInterface.TOB_PARTIES_OVERVIEW.open(player);
        //player.getPacketDispatcher().sendClientScript(2524, -1, -1);
        final var currentParty = VerSinhazaArea.getParty(player);
        for (int index = 0; index < 46; index++) {
            final var party = VerSinhazaArea.getPartyByIndex(index);
            if (party == null || party.getRaid() != null) {
                dispatcher.sendClientScript(2340, index, "");
                continue;
            }
            if (party.getLeader() == null) {
                continue;
            }
            String builder = (currentParty == party ? Colour.WHITE.wrap(party.getLeader().getName()) : party.getLeader().getName()) + "|" +
                    party.getMembers().size() + "|" +
                    party.getPreferredSize() + "|" +
                    party.getPreferredCombatLevel() + "|" +
                    (party.isHardMode() ? "Hard" : "Normal") + "|" +
                    party.getAge() + "|";
            player.getPacketDispatcher().sendClientScript(2340, index, builder);
        }
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.TOB_PARTIES_OVERVIEW;
    }
}
