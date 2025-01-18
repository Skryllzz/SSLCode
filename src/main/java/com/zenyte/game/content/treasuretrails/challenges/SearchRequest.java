package com.zenyte.game.content.treasuretrails.challenges;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author Kris | 07/04/2019 13:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class SearchRequest implements ClueChallenge {
    private final GameObject[] validObjects;

    public SearchRequest(final GameObject[] validObjects) {
        this.validObjects = validObjects;
    }

    public GameObject[] getValidObjects() {
        return this.validObjects;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof SearchRequest other)) return false;
        return Arrays.deepEquals(this.getValidObjects(), other.getValidObjects());
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + Arrays.deepHashCode(this.getValidObjects());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "SearchRequest(validObjects=" + Arrays.deepToString(this.getValidObjects()) + ")";
    }
}
