package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.PlayerInformation;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Objects;

/**
 * @author Corey
 * @since 20:24 - 25/06/2019
 */
public class SubmitPlayerInformation {
    private static final Logger log = LoggerFactory.getLogger(SubmitPlayerInformation.class);
    private final PlayerInformation info;

    public SubmitPlayerInformation(final PlayerInformation info) {
        this.info = info;
    }

    public void execute() {
        final RequestBody body = APIClient.jsonBody(info);
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("user").addPathSegment("info").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        try {
            final Response response = APIClient.CLIENT.newCall(request).execute();
            response.close();
            log.info("Sent player information to api for '" + info.getUsername() + "'");
        } catch (final SocketException | SocketTimeoutException ignored) {
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    public PlayerInformation getInfo() {
        return this.info;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof SubmitPlayerInformation other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$info = this.getInfo();
        final Object other$info = other.getInfo();
        return Objects.equals(this$info, other$info);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof SubmitPlayerInformation;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $info = this.getInfo();
        result = result * PRIME + ($info == null ? 43 : $info.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "SubmitPlayerInformation(info=" + this.getInfo() + ")";
    }
}
