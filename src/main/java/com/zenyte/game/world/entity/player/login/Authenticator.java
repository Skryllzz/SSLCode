package com.zenyte.game.world.entity.player.login;

import com.zenyte.game.util.Utils;
import com.zenyte.network.ClientResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Kris | 10. apr 2018 : 18:58.06
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class Authenticator {
    private final Map<Integer, Long> trustedPCs = new HashMap<>();
    private final transient int randomUID = Utils.random(Integer.MAX_VALUE - 1);
    private transient boolean enabled;
    private transient boolean trusted;

    public ClientResponse validate(final int identifier) {
        final Long date = trustedPCs.get(identifier);
        if (date == null || date < System.currentTimeMillis()) {
            return ClientResponse.PROMPT_AUTHENTICATOR;
        }
        return ClientResponse.LOGIN_OK;
    }

    void trust() {
        final Long date = trustedPCs.get(randomUID);
        if (date == null || date < Utils.currentTimeMillis()) {
            //Only refresh the trusted pc if the old one has expired.
            trusted = true;
            trustedPCs.put(randomUID, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30));
        }
    }

    public Map<Integer, Long> getTrustedPCs() {
        return this.trustedPCs;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isTrusted() {
        return this.trusted;
    }

    public int getRandomUID() {
        return this.randomUID;
    }
}
