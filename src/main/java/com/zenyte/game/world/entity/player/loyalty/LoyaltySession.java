package com.zenyte.game.world.entity.player.loyalty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Objects;

/**
 * @author Kris | 29/04/2019 13:23
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class LoyaltySession {
    private final Date login;
    private final Date logout;

    public LoyaltySession(final Date login, final Date logout) {
        this.login = login;
        this.logout = logout;
    }

    public Date getLogin() {
        return this.login;
    }

    public Date getLogout() {
        return this.logout;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof LoyaltySession other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$login = this.getLogin();
        final Object other$login = other.getLogin();
        if (!Objects.equals(this$login, other$login)) return false;
        final Object this$logout = this.getLogout();
        final Object other$logout = other.getLogout();
        return Objects.equals(this$logout, other$logout);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof LoyaltySession;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $login = this.getLogin();
        result = result * PRIME + ($login == null ? 43 : $login.hashCode());
        final Object $logout = this.getLogout();
        result = result * PRIME + ($logout == null ? 43 : $logout.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "LoyaltySession(login=" + this.getLogin() + ", logout=" + this.getLogout() + ")";
    }
}
