package com.zenyte.network.login.packet;

import com.zenyte.network.ClientResponse;
import com.zenyte.network.PacketOut;

/**
 * @author Tommeh | 27 jul. 2018 | 19:47:44
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class LoginPacketOut implements PacketOut {
    private final ClientResponse response;

    public LoginPacketOut(final ClientResponse response) {
        this.response = response;
    }

    public ClientResponse getResponse() {
        return this.response;
    }

}
