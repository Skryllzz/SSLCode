package com.zenyte.game.world.entity.player.dailychallenge;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Tommeh | 03/05/2019 | 22:16
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public class ChallengeDetails {
    private final ChallengeDifficulty difficulty;
    private final Object[] additionalInformation;

    public ChallengeDetails(final ChallengeDifficulty difficulty, final Object... additionalInformation) {
        this.difficulty = difficulty;
        this.additionalInformation = additionalInformation;
    }

    public ChallengeDifficulty getDifficulty() {
        return this.difficulty;
    }

    public Object[] getAdditionalInformation() {
        return this.additionalInformation;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof ChallengeDetails other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$difficulty = this.getDifficulty();
        final Object other$difficulty = other.getDifficulty();
        if (!Objects.equals(this$difficulty, other$difficulty)) return false;
        return Arrays.deepEquals(this.getAdditionalInformation(), other.getAdditionalInformation());
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof ChallengeDetails;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $difficulty = this.getDifficulty();
        result = result * PRIME + ($difficulty == null ? 43 : $difficulty.hashCode());
        result = result * PRIME + Arrays.deepHashCode(this.getAdditionalInformation());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "ChallengeDetails(difficulty=" + this.getDifficulty() + ", additionalInformation=" + Arrays.deepToString(this.getAdditionalInformation()) + ")";
    }
}
