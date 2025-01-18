package com.zenyte.api.client.query.hiscores;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.SkillHiscore;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * @author Corey
 * @since 05/05/19
 */
public class SendPlayerHiscores {
    private static final Logger log = LoggerFactory.getLogger(SendPlayerHiscores.class);
    private final String username;
    private final List<SkillHiscore> hiscores;

    public SendPlayerHiscores(final String username, final List<SkillHiscore> hiscores) {
        this.username = username;
        this.hiscores = hiscores;
    }

    public void execute() {
        final OkHttpClient http = APIClient.CLIENT;
        final RequestBody body = APIClient.jsonBody(hiscores);
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("hiscores").addPathSegment("user").addPathSegment(username.replaceAll("_", " ")).addPathSegment("update").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        try {
            final Response response = http.newCall(request).execute();
            response.close();
            log.info("Sent hiscores data to api for '" + username + "'");
        } catch (final SocketException | SocketTimeoutException ignored) {
        } catch (final Exception e) {
            log.error("", e);
        }
    }
}
