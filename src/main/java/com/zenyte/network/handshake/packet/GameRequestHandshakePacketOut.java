package com.zenyte.network.handshake.packet;

import com.zenyte.network.ClientResponse;
import com.zenyte.network.handshake.packet.inc.HandshakeType;

/**
 * @author Jire
 */
public final class GameRequestHandshakePacketOut extends DefaultHandshakePacketOut {

    private final long sessionKey;

    public GameRequestHandshakePacketOut(
            HandshakeType type, ClientResponse response,
            long sessionKey) {

        super(type, response);

        this.sessionKey = sessionKey;
    }

    public long getSessionKey() {
        return sessionKey;
    }

}
