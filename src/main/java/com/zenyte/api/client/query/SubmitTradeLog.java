package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.TradeLog;
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
public class SubmitTradeLog {
    private static final Logger log = LoggerFactory.getLogger(SubmitTradeLog.class);
    private final TradeLog transaction;

    public SubmitTradeLog(final TradeLog transaction) {
        this.transaction = transaction;
    }

    public void execute() {
        final OkHttpClient http = APIClient.CLIENT;
        final RequestBody body = APIClient.jsonBody(transaction);
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("user").addPathSegment("log").addPathSegment("trade").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        try {
            final Response response = http.newCall(request).execute();
            response.close();
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    public TradeLog getTransaction() {
        return this.transaction;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof SubmitTradeLog other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$transaction = this.getTransaction();
        final Object other$transaction = other.getTransaction();
        return Objects.equals(this$transaction, other$transaction);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof SubmitTradeLog;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $transaction = this.getTransaction();
        result = result * PRIME + ($transaction == null ? 43 : $transaction.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "SubmitTradeLog(transaction=" + this.getTransaction() + ")";
    }
}
