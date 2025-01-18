package com.zenyte.plugins.events;

import com.zenyte.game.content.skills.magic.Spellbook;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Kris | 26/04/2019 19:46
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SpellbookChangeEvent implements Event {
    private final Player player;
    private final Spellbook oldSpellbook;

    public SpellbookChangeEvent(final Player player, final Spellbook oldSpellbook) {
        this.player = player;
        this.oldSpellbook = oldSpellbook;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Spellbook getOldSpellbook() {
        return this.oldSpellbook;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof SpellbookChangeEvent other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (!Objects.equals(this$player, other$player)) return false;
        final Object this$oldSpellbook = this.getOldSpellbook();
        final Object other$oldSpellbook = other.getOldSpellbook();
        return Objects.equals(this$oldSpellbook, other$oldSpellbook);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof SpellbookChangeEvent;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        final Object $oldSpellbook = this.getOldSpellbook();
        result = result * PRIME + ($oldSpellbook == null ? 43 : $oldSpellbook.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "SpellbookChangeEvent(player=" + this.getPlayer() + ", oldSpellbook=" + this.getOldSpellbook() + ")";
    }
}
