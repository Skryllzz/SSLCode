package com.zenyte.plugins.object;

import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.world.entity.masks.Animation;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;

/**
 * Author: Kris | 24/01/2019 17:07
 * See <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AncientAltar implements ObjectAction {

    private static final Animation PRAY_ANIM = new Animation(645);

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        if (option.contains("Ancient")) {
            changeSpellbook(player, Spellbook.ANCIENT);
        } else if (option.contains("Lunar")) {
            changeSpellbook(player, Spellbook.LUNAR);
        } else if (option.contains("Arceuus")) {
            changeSpellbook(player, Spellbook.ARCEUUS);
        } else if (option.contains("Venerate")) {
            changeSpellbook(player, Spellbook.NORMAL);
        }
    }

    private void changeSpellbook(Player player, Spellbook newSpellbook) {
        player.lock(5);
        player.setAnimation(PRAY_ANIM);
        player.getCombatDefinitions().setSpellbook(newSpellbook, true);
        player.sendMessage("You have changed to the " + newSpellbook.name().toLowerCase() + " spellbook.");
    }

    @Override
    public Object[] getObjects() {
        return new Object[]{ObjectId.ALTAR_OF_THE_OCCULT};
    }
}
