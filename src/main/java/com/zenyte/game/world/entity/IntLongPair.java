package com.zenyte.game.world.entity;

/**
 * @author Kris | 20/06/2019 17:35
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class IntLongPair {
    private final long left;
    private final int right;

    public IntLongPair(final long left, final int right) {
        this.left = left;
        this.right = right;
    }

    public long getLeft() {
        return this.left;
    }

    public int getRight() {
        return this.right;
    }
}
