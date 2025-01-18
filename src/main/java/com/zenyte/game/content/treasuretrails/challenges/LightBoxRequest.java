package com.zenyte.game.content.treasuretrails.challenges;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author Kris | 08/04/2019 20:53
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class LightBoxRequest implements ClueChallenge {
    private final int[] validNPCs;

    public LightBoxRequest(final int[] validNPCs) {
        this.validNPCs = validNPCs;
    }

    public int[] getValidNPCs() {
        return this.validNPCs;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof LightBoxRequest other)) return false;
        return Arrays.equals(this.getValidNPCs(), other.getValidNPCs());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + Arrays.hashCode(this.getValidNPCs());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "LightBoxRequest(validNPCs=" + Arrays.toString(this.getValidNPCs()) + ")";
    }
}
