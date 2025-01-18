package com.zenyte.network.update.packet.inc;

import com.zenyte.network.update.packet.UpdatePacketIn;

/**
 * @author Tommeh | 27 jul. 2018 | 20:31:21
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class FileRequest implements UpdatePacketIn {
    private final boolean priority;
    private final int index;
    private final int file;

    public FileRequest(final boolean priority, final int index, final int file) {
        this.priority = priority;
        this.index = index;
        this.file = file;
    }

    public boolean isPriority() {
        return this.priority;
    }

    public int getIndex() {
        return this.index;
    }

    public int getFile() {
        return this.file;
    }
}
