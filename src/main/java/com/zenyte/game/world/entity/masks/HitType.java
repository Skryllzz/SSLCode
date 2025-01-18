package com.zenyte.game.world.entity.masks;

import com.zenyte.network.io.RSBuffer;
import mgi.types.config.Edenify;

/**
 * @author Kris | 28. march 2018 : 0:37.40
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 * <p>
 * Mark id must be a positive integer!
 */
public enum HitType {
    MISSED(12, 13),
    //Regular does not invoke special effects such as vengeance.
    REGULAR(16, 17),
    //Default invokes special effects such as vengeance.
    DEFAULT(16, 17),
    MELEE(16, 17),
    MAGIC(16, 17),
    RANGED(16, 17),
    POISON(2),
    YELLOW(3),
    DISEASED(4),
    VENOM(5),
    HEALED(6),

    VERZIK_SHIELD(7),

    SHIELD(18, 19),
    ARMOUR(20, 21),
    CHARGE(22, 23),
    DISCHARGE(24, 25),
    CORRUPTION(0),
    SHIELD_DOWN(60)
    ;

    private final int id;
    private final int dynamicID;

    public static final HitType[] values = values();

    HitType(int id, int dynamicID, boolean edenified) {
        this.id = edenified ? Edenify.HIT_SPLATS.offsetted(id) : id;
        this.dynamicID = edenified ? Edenify.HIT_SPLATS.offsetted(dynamicID) : dynamicID;
    }

    HitType(int id, int dynamicID) {
        this(id, dynamicID, true);
    }

    HitType(int id, boolean edenified) {
        this(id, id, edenified);
    }

    HitType(int id) {
        this(id, true);
    }

    public void writeMask(RSBuffer buffer, boolean useDynamic) {
        buffer.writeSmart(useDynamic ? dynamicID : id);
    }

}
