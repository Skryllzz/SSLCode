package com.zenyte.game.content.treasuretrails.challenges;

import com.zenyte.game.content.treasuretrails.clues.CharlieTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Kris | 04/01/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class CharlieRequest implements ClueChallenge {
    private final CharlieTask task;

    public CharlieRequest(final CharlieTask task) {
        this.task = task;
    }

    public CharlieTask getTask() {
        return this.task;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof CharlieRequest other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$task = this.getTask();
        final Object other$task = other.getTask();
        return Objects.equals(this$task, other$task);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof CharlieRequest;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $task = this.getTask();
        result = result * PRIME + ($task == null ? 43 : $task.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "CharlieRequest(task=" + this.getTask() + ")";
    }
}
