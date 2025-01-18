package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import okhttp3.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class TotalDonatedRequest {
    private static final Logger log = LoggerFactory.getLogger(TotalDonatedRequest.class);
    private final String username;

    public TotalDonatedRequest(final String username) {
        this.username = username.replaceAll("_", " ");
    }

    public int execute() {
        final OkHttpClient http = APIClient.CLIENT;
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("account").addPathSegment("spent").addPathSegment(username).build();
        final Request request = new Request.Builder().url(url).get().build();
        try {
            try (Response response = http.newCall(request).execute()) {
                final ResponseBody responseBody = response.body();
                if (responseBody == null || !response.isSuccessful()) {
                    return -1;
                }
                final String body = responseBody.string();
                //If empty besides {}
                if (body.length() == 0) {
                    return -1;
                }
                return NumberUtils.isDigits(body) ? Integer.parseInt(body) : 0;
            }
        } catch (final Exception e) {
            log.error("", e);
        }
        return -1;
    }
}
