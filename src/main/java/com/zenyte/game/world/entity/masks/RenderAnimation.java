package com.zenyte.game.world.entity.masks;

/**
 * @author Kris | 6. nov 2017 : 14:36.04
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 */
public final class RenderAnimation implements RenderType {
    public static final int STAND = 808;
    public static final int STAND_TURN = 823;
    public static final int WALK = 819;
    public static final int ROTATE180 = 820;
    public static final int ROTATE90 = 821;
    public static final int ROTATE270 = 822;
    public static final int RUN = 824;
    public static final RenderAnimation DEFAULT_RENDER = new RenderAnimation(STAND, STAND_TURN, WALK, ROTATE180, ROTATE90, ROTATE270, RUN);
    private final int stand;
    private final int standTurn;
    private final int walk;
    private final int rotate180;
    private final int rotate90;
    private final int rotate270;
    private final int run;

    public RenderAnimation(final int stand, final int walk, final int run) {
        this(stand, STAND_TURN, walk, ROTATE180, ROTATE90, ROTATE270, run);
    }

    public RenderAnimation(final int stand, final int standTurn, final int walk, final int rotate180, final int rotate90, final int rotate270, final int run) {
        this.stand = stand;
        this.standTurn = standTurn;
        this.walk = walk;
        this.rotate180 = rotate180;
        this.rotate90 = rotate90;
        this.rotate270 = rotate270;
        this.run = run;
    }

    public int getStand() {
        return this.stand;
    }

    public int getStandTurn() {
        return this.standTurn;
    }

    public int getWalk() {
        return this.walk;
    }

    public int getRotate180() {
        return this.rotate180;
    }

    public int getRotate90() {
        return this.rotate90;
    }

    public int getRotate270() {
        return this.rotate270;
    }

    public int getRun() {
        return this.run;
    }
}
