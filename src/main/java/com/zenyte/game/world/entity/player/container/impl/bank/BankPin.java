package com.zenyte.game.world.entity.player.container.impl.bank;

/**
 * @author Kris | 01/03/2019 19:18
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class BankPin {

    private final PinRecoveryDelay pinRecoveryDelay = PinRecoveryDelay.THREE_DAYS;
    private final PinLoginSetting pinLoginSetting = PinLoginSetting.ALWAYS_LOCK;
    private final PinStatus pinStatus = PinStatus.DISABLED;
    private long delay;
    private transient boolean unlocked;

    private enum PinRecoveryDelay {
        THREE_DAYS,
        SEVEN_DAYS
    }

    private enum PinLoginSetting {
        ALWAYS_LOCK,
        LOCK_AFTER_FIVE_MINUTES
    }

    private enum PinStatus {
        ENABLED,
        PENDING_ENABLING,
        DISABLED,
        PENDING_DISABLING
    }

}
