package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.World;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Corey
 * @since 01/05/19
 */
public class SendWorldInfo {
    private static final Logger log = LoggerFactory.getLogger(SendWorldInfo.class);
    private final World worldInfo;

    public SendWorldInfo(final World world) {
        this.worldInfo = world;
    }

    public void execute() {
        final OkHttpClient http = APIClient.CLIENT;
        final RequestBody body = APIClient.jsonBody(worldInfo);
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("worldinfo").addPathSegment("world").addPathSegment("update").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        try {
            final Response response = http.newCall(request).execute();
            response.close();
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
