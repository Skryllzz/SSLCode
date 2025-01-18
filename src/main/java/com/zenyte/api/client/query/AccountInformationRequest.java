package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author Kris | 03/05/2019 21:22
 * @author Corey
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class AccountInformationRequest {
    private static final Logger log = LoggerFactory.getLogger(AccountInformationRequest.class);
    private final String username;

    public AccountInformationRequest(final String username) {
        this.username = username;
    }

    public AccountInformationRequestResults execute() {
        final OkHttpClient http = APIClient.CLIENT;
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("user").addPathSegment("columns").addPathSegment(username.replaceAll("_", " ")).addQueryParameter("columns", "joined,member_id,msg_count_new,members_pass_hash,mfa_details").build();
        final Request request = new Request.Builder().url(url).get().build();
        try {
            try (Response response = http.newCall(request).execute()) {
                final ResponseBody responseBody = response.body();
                if (responseBody == null || !response.isSuccessful()) {
                    return null;
                }
                final String body = responseBody.string();
                //If empty besides {}
                if (body.length() == 2) {
                    return null;
                }
                return new AccountInformationRequestResults(body);
            }
        } catch (final Exception e) {
            log.error("", e);
        }
        return null;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof AccountInformationRequest other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        return Objects.equals(this$username, other$username);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof AccountInformationRequest;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "AccountInformationRequest(username=" + this.getUsername() + ")";
    }
}
