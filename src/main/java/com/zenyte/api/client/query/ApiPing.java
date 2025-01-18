package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Corey
 * @since 01/05/19
 */
public class ApiPing {
    private static final Logger log = LoggerFactory.getLogger(ApiPing.class);

    public boolean execute() {
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("ping").build();
        final FormBody body = new FormBody.Builder().add("payload", "ping").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = APIClient.CLIENT.newCall(request).execute()) {
            final ResponseBody responseBody = response.body();
            if (responseBody == null || !response.isSuccessful()) {
                return false;
            }
            final String string = responseBody.string();
            if (!"pong".equals(string)) {
                log.error("Returned invalid response: " + string);
                return false;
            }
            return true;
        } catch (IOException e) {
            log.error("", e);
            return false;
        }
    }
}
