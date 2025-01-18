package com.zenyte.game.content.treasuretrails.coordinateutils;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Kris | 02/03/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public final class SextantPosition {
    static final int BASE_FRAME_POSITION = 934;
    static final int BASE_ARM_POSITION = 949;
    static final int FRAME_POSITIONS_COUNT = 15;
    static final int ARM_POSITIONS_COUNT = 58;
    private final int framePosition;
    private final int armPosition;

    SextantPosition(final int framePosition, final int armPosition) {
        Preconditions.checkArgument(framePosition >= 0);
        Preconditions.checkArgument(armPosition >= 0);
        Preconditions.checkArgument(framePosition < FRAME_POSITIONS_COUNT);
        Preconditions.checkArgument(armPosition < ARM_POSITIONS_COUNT);
        this.framePosition = framePosition;
        this.armPosition = armPosition;
    }

    public int getFramePosition() {
        return this.framePosition;
    }

    public int getArmPosition() {
        return this.armPosition;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof SextantPosition other)) return false;
        if (this.getFramePosition() != other.getFramePosition()) return false;
        return this.getArmPosition() == other.getArmPosition();
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getFramePosition();
        result = result * PRIME + this.getArmPosition();
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "SextantPosition(framePosition=" + this.getFramePosition() + ", armPosition=" + this.getArmPosition() + ")";
    }
}
