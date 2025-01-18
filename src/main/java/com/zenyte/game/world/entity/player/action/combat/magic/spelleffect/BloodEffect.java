package com.zenyte.game.world.entity.player.action.combat.magic.spelleffect;

import com.zenyte.game.world.entity.Entity;

public class BloodEffect implements SpellEffect {

    @Override
    public void spellEffect(final Entity player, final Entity target, final int damage) {
        if (damage < 4) {
            return;
        }
        player.heal(damage / 4);
    }

}
