package com.zenyte.game.world.info;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Corey
 * @since 20/05/19
 */
public class ApiSettings {
    private boolean enabled;
    private String scheme;
    private String host;
    private int port;
    private String token;

    public ApiSettings(final boolean enabled, final String scheme, final String host, final int port, final String token) {
        this.enabled = enabled;
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.token = token;
    }

    public ApiSettings() {
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof ApiSettings other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.isEnabled() != other.isEnabled()) return false;
        if (this.getPort() != other.getPort()) return false;
        final Object this$scheme = this.getScheme();
        final Object other$scheme = other.getScheme();
        if (!Objects.equals(this$scheme, other$scheme)) return false;
        final Object this$host = this.getHost();
        final Object other$host = other.getHost();
        if (!Objects.equals(this$host, other$host)) return false;
        final Object this$token = this.getToken();
        final Object other$token = other.getToken();
        return Objects.equals(this$token, other$token);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof ApiSettings;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isEnabled() ? 79 : 97);
        result = result * PRIME + this.getPort();
        final Object $scheme = this.getScheme();
        result = result * PRIME + ($scheme == null ? 43 : $scheme.hashCode());
        final Object $host = this.getHost();
        result = result * PRIME + ($host == null ? 43 : $host.hashCode());
        final Object $token = this.getToken();
        result = result * PRIME + ($token == null ? 43 : $token.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "ApiSettings(enabled=" + this.isEnabled() + ", scheme=" + this.getScheme() + ", host=" + this.getHost() + ", port=" + this.getPort() + ", token=" + this.getToken() + ")";
    }
}
