package com.zenyte.game.world.entity.player.action.combat.melee;

import com.zenyte.Game;
import com.zenyte.game.world.entity.Entity;
import com.zenyte.game.world.entity.player.ActionManager;
import com.zenyte.game.world.entity.player.CombatDefinitions;
import com.zenyte.game.world.entity.player.action.combat.MeleeCombat;
import it.unimi.dsi.fastutil.longs.LongArrayList;

/**
 * @author Kris | 2. juuni 2018 : 22:50:30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class GraniteMaulCombat extends MeleeCombat {
    public GraniteMaulCombat(final Entity target) {
        super(target);
    }

    @Override
    public boolean process() {
        if (!initiateCombat(player)) {
            return false;
        }
        final Object attribute = player.getTemporaryAttributes().get("cached granite maul specials");
        final LongArrayList cachedMaulSpecials = attribute instanceof LongArrayList ? (LongArrayList) attribute : null;
        if (cachedMaulSpecials == null || cachedMaulSpecials.isEmpty()) {
            return true;
        }
        final long latestTick = cachedMaulSpecials.getLong(cachedMaulSpecials.size() - 1);
        if (latestTick <= Game.getCurrentCycle() - 2) {
            cachedMaulSpecials.clear();
            return true;
        }
        int countOfCachedSpecials = 0;
        for (final Long special : cachedMaulSpecials) {
            if (special == latestTick) {
                countOfCachedSpecials++;
            }
        }
        cachedMaulSpecials.clear();
        final int numberOfSpecials = Math.min(countOfCachedSpecials, 2);
        final CombatDefinitions combatDefinitions = player.getCombatDefinitions();
        int numberOfSpecialsUsed = 0;
        for (int i = 0; i < numberOfSpecials; i++) {
            combatDefinitions.setUsingSpecial(true);
            if (combatDefinitions.getSpecialEnergy() < 50) {
                continue;
            }
            processWithDelay();
            numberOfSpecialsUsed++;
        }
        combatDefinitions.setUsingSpecial(false);
        combatDefinitions.refresh();
        if (numberOfSpecialsUsed > 0) {
            final ActionManager actionManager = player.getActionManager();
            //Avoid it running the processWithDelay method on-top of the special attack executions.
            if (actionManager.getActionDelay() == 0) {
                actionManager.setActionDelay(1);
            }
        }
        return true;
    }
}
