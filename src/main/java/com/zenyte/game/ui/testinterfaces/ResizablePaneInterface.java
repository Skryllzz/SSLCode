package com.zenyte.game.ui.testinterfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Setting;

/**
 * @author Kris | 07/01/2019 15:42
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ResizablePaneInterface extends Interface {

    @Override
    protected void attach() {
        put(20, "Gravestone info");
        put(43, "Open Game Noticeboard");
        put(60, "Character Summary");
        put(64, "Toggle spell filtering");
    }

    @Override
    public void open(Player player) {
        throw new IllegalStateException("Panes cannot be opened as interfaces.");
    }

    @Override
    protected void build() {
        bind("Toggle spell filtering", (player, slotId, itemId, option) -> {
            if (option == 2) {
                player.getSettings().toggleSetting(Setting.SPELL_FILTERING_DISABLED);
            }
        });
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.RESIZABLE_PANE;
    }
}
