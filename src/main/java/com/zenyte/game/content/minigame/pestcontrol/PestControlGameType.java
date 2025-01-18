package com.zenyte.game.content.minigame.pestcontrol;

import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.Location;

/**
 * @author Kris | 26. juuni 2018 : 18:17:30
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public enum PestControlGameType {
    NOVICE(6, 40, PestControlUtilities.NOVICE_FLAG_MODEL, 1771, new Location(2657, 2639, 0), new Location(2661, 2639, 0)),
    INTERMEDIATE(8, 70, PestControlUtilities.INTERMEDIATE_FLAG_MODEL, 1772, new Location(2644, 2644, 0), new Location(2640, 2644, 0)),
    VETERAN(10, 100, PestControlUtilities.VETERAN_FLAG_MODEL, 1773, new Location(2638, 2653, 0), new Location(2634, 2653, 0));
    private final int pointsPerGame;
    private final int combatLevelRequirement;
    private final int flagModel;
    private final int squireId;
    private final Location exitPoint;
    private final Location enterPoint;
    private final String name;

    PestControlGameType(final int pointsPerGame, final int combatLevelRequirement, final int flagModel, final int squireId, final Location exitPoint, final Location enterPoint) {
        this.pointsPerGame = pointsPerGame;
        this.combatLevelRequirement = combatLevelRequirement;
        this.flagModel = flagModel;
        this.squireId = squireId;
        this.exitPoint = exitPoint;
        this.enterPoint = enterPoint;
        name = Utils.formatString(name());
    }

    @Override
    public String toString() {
        return name;
    }

    public int getPointsPerGame() {
        return this.pointsPerGame;
    }

    public int getCombatLevelRequirement() {
        return this.combatLevelRequirement;
    }

    public int getFlagModel() {
        return this.flagModel;
    }

    public int getSquireId() {
        return this.squireId;
    }

    public Location getExitPoint() {
        return this.exitPoint;
    }

    public Location getEnterPoint() {
        return this.enterPoint;
    }
}
