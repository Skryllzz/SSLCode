package com.zenyte.network.login.packet.inc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author Tommeh | 28 jul. 2018 | 09:43:43
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public enum LoginType {
    NEW_LOGIN_CONNECTION(16),
    RECONNECT_LOGIN_CONNECTION(18);
    private static final LoginType[] values = values();
    private static final Int2ObjectMap<LoginType> valueMap = new Int2ObjectOpenHashMap<>(values.length);

    static {
        for (final LoginType type : values) {
            valueMap.put(type.getId(), type);
        }
    }

    private final int id;

    LoginType(final int id) {
        this.id = id;
    }

    public static LoginType get(final int id) {
        return valueMap.get(id);
    }

    public int getId() {
        return this.id;
    }
}
