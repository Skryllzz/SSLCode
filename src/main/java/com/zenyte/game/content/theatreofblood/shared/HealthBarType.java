package com.zenyte.game.content.theatreofblood.shared;

/**
 * @author Tommeh | 5/31/2020 | 12:17 AM
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public enum HealthBarType {
    REMOVED(0), REGULAR(1), DISABLED(2), CYAN(3);
    private final int id;

    public int getId() {
        return this.id;
    }

    private HealthBarType(final int id) {
        this.id = id;
    }
}
