package com.zenyte.game.content.treasuretrails;

import com.zenyte.game.content.treasuretrails.challenges.HotColdChallenge;
import com.zenyte.game.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Kris | 10/04/2019 19:32
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class HotColdResult {
    private final int slotId;
    private final Item clue;
    private final HotColdChallenge challenge;
    private final int distance;
    private final DeviceTemperature temperature;

    public HotColdResult(final int slotId, final Item clue, final HotColdChallenge challenge, final int distance, final DeviceTemperature temperature) {
        this.slotId = slotId;
        this.clue = clue;
        this.challenge = challenge;
        this.distance = distance;
        this.temperature = temperature;
    }

    public int getSlotId() {
        return this.slotId;
    }

    public Item getClue() {
        return this.clue;
    }

    public HotColdChallenge getChallenge() {
        return this.challenge;
    }

    public int getDistance() {
        return this.distance;
    }

    public DeviceTemperature getTemperature() {
        return this.temperature;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof HotColdResult other)) return false;
        if (this.getSlotId() != other.getSlotId()) return false;
        if (this.getDistance() != other.getDistance()) return false;
        final Object this$clue = this.getClue();
        final Object other$clue = other.getClue();
        if (!Objects.equals(this$clue, other$clue)) return false;
        final Object this$challenge = this.getChallenge();
        final Object other$challenge = other.getChallenge();
        if (!Objects.equals(this$challenge, other$challenge)) return false;
        final Object this$temperature = this.getTemperature();
        final Object other$temperature = other.getTemperature();
        return Objects.equals(this$temperature, other$temperature);
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getSlotId();
        result = result * PRIME + this.getDistance();
        final Object $clue = this.getClue();
        result = result * PRIME + ($clue == null ? 43 : $clue.hashCode());
        final Object $challenge = this.getChallenge();
        result = result * PRIME + ($challenge == null ? 43 : $challenge.hashCode());
        final Object $temperature = this.getTemperature();
        result = result * PRIME + ($temperature == null ? 43 : $temperature.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "HotColdResult(slotId=" + this.getSlotId() + ", clue=" + this.getClue() + ", challenge=" + this.getChallenge() + ", distance=" + this.getDistance() + ", temperature=" + this.getTemperature() + ")";
    }
}
