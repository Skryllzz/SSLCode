package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Corey
 * @since 01/06/19
 */
public class Valid2FACodeQuery {
    private static final Logger log = LoggerFactory.getLogger(Valid2FACodeQuery.class);
    private final int memberId;
    private final String code;

    public Valid2FACodeQuery(final int memberId, final String code) {
        this.memberId = memberId;
        this.code = code;
    }

    public boolean execute() {
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("user").addPathSegment(String.valueOf(memberId)).addPathSegment("check2fa").addQueryParameter("code", code).build();
        final Request request = new Request.Builder().url(url).get().build();
        try (Response response = APIClient.CLIENT.newCall(request).execute()) {
            final ResponseBody responseBody = response.body();
            if (responseBody == null || !response.isSuccessful()) {
                return false;
            }
            final String string = responseBody.string();
            if (!"true".equals(string)) {
                log.error("[member_id=" + memberId + ", code=" + code + "] Invalid response or code; response: " + string);
                return false;
            }
            return true;
        } catch (IOException e) {
            log.error("", e);
            return false;
        }
    }

    public int getMemberId() {
        return this.memberId;
    }

    public String getCode() {
        return this.code;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof Valid2FACodeQuery other)) return false;
        if (!other.canEqual(this)) return false;
        if (this.getMemberId() != other.getMemberId()) return false;
        final Object this$code = this.getCode();
        final Object other$code = other.getCode();
        return Objects.equals(this$code, other$code);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof Valid2FACodeQuery;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getMemberId();
        final Object $code = this.getCode();
        result = result * PRIME + ($code == null ? 43 : $code.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "Valid2FACodeQuery(memberId=" + this.getMemberId() + ", code=" + this.getCode() + ")";
    }
}
