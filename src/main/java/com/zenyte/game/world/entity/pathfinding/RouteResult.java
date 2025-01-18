package com.zenyte.game.world.entity.pathfinding;

/**
 * @author Kris | 26/02/2019 22:04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class RouteResult {
    public static final RouteResult ILLEGAL = new RouteResult(-1, null, null, true);
    private final int steps;
    private final int[] xBuffer;
    private final int[] yBuffer;
    private final boolean alternative;

    public RouteResult(final int steps, final int[] xBuffer, final int[] yBuffer, final boolean alternative) {
        this.steps = steps;
        this.xBuffer = xBuffer;
        this.yBuffer = yBuffer;
        this.alternative = alternative;
    }

    public int getSteps() {
        return this.steps;
    }

    public int[] getXBuffer() {
        return this.xBuffer;
    }

    public int[] getYBuffer() {
        return this.yBuffer;
    }

    public boolean isAlternative() {
        return this.alternative;
    }
}
