package com.zenyte.plugins.item;

import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Lamp;

import static com.zenyte.game.constants.GameInterface.EXPERIENCE_LAMP;
import static com.zenyte.game.world.entity.player.Lamp.*;

/**
 * @author Tommeh | 8-11-2018 | 18:48
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ExperienceLamp extends ItemPlugin {
    @Override
    public void handle() {
        bind("Rub", (player, item, slotId) -> {
            final Lamp lamp = Lamp.get(item.getId());
            if (lamp == null) {
                return;
            }
            final Object[] args = new Object[]{(int) lamp.getExperience(), lamp.getMinimumLevel(), slotId, item};
            if (lamp != ELITE_DIARY_LAMP) {
                if (item.getCharges() == 1) {
                    args[0] = (int) ((lamp == EASY_DIARY_LAMP ? 1000 : lamp == MEDIUM_DIARY_LAMP ? 5000 : 10000) / 5.0F);
                    args[1] = lamp == EASY_DIARY_LAMP ? 1 : lamp == MEDIUM_DIARY_LAMP ? 30 : 40;
                }
            }
            player.getTemporaryAttributes().put("experience_lamp_info", args);
            EXPERIENCE_LAMP.open(player);
        });
    }

    @Override
    public int[] getItems() {
        return new int[]{13145, 13146, 13147, 13148};
    }
}
