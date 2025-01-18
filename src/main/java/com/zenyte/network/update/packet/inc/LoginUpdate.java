package com.zenyte.network.update.packet.inc;

import com.zenyte.network.update.packet.UpdatePacketIn;

/**
 * @author Tommeh | 27 jul. 2018 | 20:47:19
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class LoginUpdate implements UpdatePacketIn {
    private final boolean loggedIn;
    private final int value;

    public LoginUpdate(final boolean loggedIn, final int value) {
        this.loggedIn = loggedIn;
        this.value = value;
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public int getValue() {
        return this.value;
    }
}
