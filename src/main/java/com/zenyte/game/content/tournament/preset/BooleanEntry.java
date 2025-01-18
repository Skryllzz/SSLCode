package com.zenyte.game.content.tournament.preset;

/**
 * @author Kris | 09/06/2019 04:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BooleanEntry<T> {
    private final T t;
    private final boolean bool;

    public BooleanEntry(final T t, final boolean bool) {
        this.t = t;
        this.bool = bool;
    }

    public T getT() {
        return this.t;
    }

    public boolean isBool() {
        return this.bool;
    }
}
