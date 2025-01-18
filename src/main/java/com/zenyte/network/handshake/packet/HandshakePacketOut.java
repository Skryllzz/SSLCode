package com.zenyte.network.handshake.packet;

import com.zenyte.network.ClientResponse;
import com.zenyte.network.handshake.packet.inc.HandshakeType;

/**
 * @author Jire
 */
public interface HandshakePacketOut {

    HandshakeType getType();

    ClientResponse getResponse();

}
