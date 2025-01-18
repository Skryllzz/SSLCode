package com.zenyte.network.handshake.packet.inc;

import com.zenyte.network.handshake.packet.HandshakePacketIn;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author Tommeh | 27 jul. 2018 | 21:43:23
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum HandshakeType implements HandshakePacketIn {
    GAME_CONNECTION(14),
    UPDATE_CONNECTION(15),
    UNKNOWN(-1);
    private static final HandshakeType[] values = values();
    private static final Int2ObjectMap<HandshakeType> valueMap = new Int2ObjectOpenHashMap<>(values.length);

    static {
        for (final HandshakeType type : values) {
            valueMap.put(type.getId(), type);
        }
    }

    private final int id;

    HandshakeType(final int id) {
        this.id = id;
    }

    public static HandshakeType get(final int id) {
        return valueMap.getOrDefault(id, UNKNOWN);
    }

    public int getId() {
        return this.id;
    }
}
