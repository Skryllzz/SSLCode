package com.zenyte.network.update.packet.inc;

import com.zenyte.network.update.packet.UpdatePacketIn;

/**
 * @author Tommeh | 27 jul. 2018 | 21:24:25
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ConnectionUpdate implements UpdatePacketIn {
    private final boolean connected;
    private final int value;

    public ConnectionUpdate(final boolean connected, final int value) {
        this.connected = connected;
        this.value = value;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public int getValue() {
        return this.value;
    }
}
