package com.zenyte.game.content.skills.agility.pyramid;

import com.zenyte.game.content.skills.agility.Failable;
import com.zenyte.game.content.skills.agility.Obstacle;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Skills;
import com.zenyte.game.world.object.WorldObject;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 21/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public interface FailableAgilityPyramidObstacle extends Obstacle, Failable {
    default int permanentSuccessLevel() {
        return 70;
    }

    @Override
    default boolean successful(@NotNull final Player player, @NotNull final WorldObject object) {
        final int level = player.getSkills().getLevel(Skills.AGILITY);
        final int baseRequirement = 30;
        final int baseChance = 75;//Base chance % to not fail minimum level.
        final int neverFailLevel = permanentSuccessLevel();
        final int adjustmentPercentage = 100 - baseChance;
        final float successPerLevel = (float) adjustmentPercentage / ((float) neverFailLevel - baseRequirement);
        final float successChance = baseChance + Math.max(0, (level - baseRequirement)) * successPerLevel;
        return Utils.random(100) < successChance;
    }
}
