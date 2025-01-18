package com.zenyte.network.update.packet;

import com.zenyte.network.PacketOut;
import com.zenyte.network.update.packet.inc.FileRequest;
import io.netty.buffer.ByteBuf;

/**
 * @author Tommeh | 27 jul. 2018 | 20:26:22
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class UpdatePacketOut implements PacketOut {
    private final ByteBuf container;
    private final FileRequest request;

    public UpdatePacketOut(final FileRequest request, final ByteBuf container) {
        this.request = request;
        this.container = container;
    }

    public ByteBuf getContainer() {
        return this.container;
    }

    public FileRequest getRequest() {
        return this.request;
    }
}
