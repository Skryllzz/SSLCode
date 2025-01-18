package com.zenyte.network.handshake.packet;

import com.zenyte.network.ClientResponse;
import com.zenyte.network.handshake.packet.inc.HandshakeType;

/**
 * @author Jire
 */
public class DefaultHandshakePacketOut implements HandshakePacketOut {

    private final HandshakeType type;
    private final ClientResponse response;

    public DefaultHandshakePacketOut(HandshakeType type, ClientResponse response) {
        this.type = type;
        this.response = response;
    }

    @Override
    public HandshakeType getType() {
        return type;
    }

    @Override
    public ClientResponse getResponse() {
        return response;
    }

}
