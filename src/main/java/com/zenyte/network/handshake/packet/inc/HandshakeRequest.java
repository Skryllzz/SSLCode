package com.zenyte.network.handshake.packet.inc;

import com.zenyte.network.handshake.packet.HandshakePacketIn;

/**
 * @author Tommeh | 27 jul. 2018 | 21:44:25
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class HandshakeRequest implements HandshakePacketIn {
    private final HandshakeType type;
    private final int revision;

    public HandshakeRequest(final HandshakeType type, final int revision) {
        this.type = type;
        this.revision = revision;
    }

    public HandshakeType getType() {
        return this.type;
    }

    public int getRevision() {
        return this.revision;
    }
}
