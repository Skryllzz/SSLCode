package com.zenyte.game.content.pyramidplunder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Christopher
 * @since 4/4/2020
 */
public class WeightedPlunderRewardTier {
    private final PlunderRewardTier tier;
    private final int weight;

    public WeightedPlunderRewardTier(final PlunderRewardTier tier, final int weight) {
        this.tier = tier;
        this.weight = weight;
    }

    public PlunderRewardTier getTier() {
        return this.tier;
    }

    public int getWeight() {
        return this.weight;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof WeightedPlunderRewardTier other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getWeight() != other.getWeight()) return false;
        final Object this$tier = this.getTier();
        final Object other$tier = other.getTier();
        return Objects.equals(this$tier, other$tier);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof WeightedPlunderRewardTier;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getWeight();
        final Object $tier = this.getTier();
        result = result * PRIME + ($tier == null ? 43 : $tier.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "WeightedPlunderRewardTier(tier=" + this.getTier() + ", weight=" + this.getWeight() + ")";
    }
}
