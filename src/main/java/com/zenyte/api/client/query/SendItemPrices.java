package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.game.content.grandexchange.JSONGEItemDefinitions;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Kris | 16/08/2019 16:29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class SendItemPrices {
    private static final Logger log = LoggerFactory.getLogger(SendItemPrices.class);
    private final List<JSONGEItemDefinitions> prices;

    public SendItemPrices(final List<JSONGEItemDefinitions> prices) {
        this.prices = prices;
    }

    public void execute() {
        final OkHttpClient http = APIClient.CLIENT;
        final RequestBody body = APIClient.jsonBody(prices);
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("runelite").addPathSegment("items").addPathSegment("prices").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        try {
            final Response response = http.newCall(request).execute();
            response.close();
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
