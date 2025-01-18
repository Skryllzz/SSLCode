package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.PunishmentLog;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author Corey
 * @since 19/06/19
 */
public class SubmitUserPunishment {
    private static final Logger log = LoggerFactory.getLogger(SubmitUserPunishment.class);
    private final PunishmentLog punishment;

    public SubmitUserPunishment(final PunishmentLog punishment) {
        this.punishment = punishment;
    }

    public void execute() {
        final OkHttpClient http = APIClient.CLIENT;
        final RequestBody body = APIClient.jsonBody(punishment);
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("user").addPathSegment("log").addPathSegment("punish").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        try {
            final Response response = http.newCall(request).execute();
            response.close();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    public PunishmentLog getPunishment() {
        return this.punishment;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof SubmitUserPunishment other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$punishment = this.getPunishment();
        final Object other$punishment = other.getPunishment();
        return Objects.equals(this$punishment, other$punishment);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof SubmitUserPunishment;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $punishment = this.getPunishment();
        result = result * PRIME + ($punishment == null ? 43 : $punishment.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "SubmitUserPunishment(punishment=" + this.getPunishment() + ")";
    }
}
