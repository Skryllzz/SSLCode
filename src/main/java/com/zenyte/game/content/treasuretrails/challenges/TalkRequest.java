package com.zenyte.game.content.treasuretrails.challenges;

import com.zenyte.game.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Kris | 07/04/2019 13:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class TalkRequest implements ClueChallenge {
    private final int[] validNPCs;
    private Predicate<Player> predicate;

    public TalkRequest(final int[] validNPCs) {
        this.validNPCs = validNPCs;
    }

    public TalkRequest(final int[] validNPCs, final Predicate<Player> predicate) {
        this.validNPCs = validNPCs;
        this.predicate = predicate;
    }

    public int[] getValidNPCs() {
        return this.validNPCs;
    }

    public Predicate<Player> getPredicate() {
        return this.predicate;
    }

    public void setPredicate(final Predicate<Player> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof TalkRequest other)) return false;
        if (!Arrays.equals(this.getValidNPCs(), other.getValidNPCs())) return false;
        final Object this$predicate = this.getPredicate();
        final Object other$predicate = other.getPredicate();
        return Objects.equals(this$predicate, other$predicate);
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + Arrays.hashCode(this.getValidNPCs());
        final Object $predicate = this.getPredicate();
        result = result * PRIME + ($predicate == null ? 43 : $predicate.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "TalkRequest(validNPCs=" + Arrays.toString(this.getValidNPCs()) + ", predicate=" + this.getPredicate() + ")";
    }
}
