package com.zenyte.api.client.query.adventurerslog;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.SubmitGamelogRequest;
import com.zenyte.game.world.entity.player.Player;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Kris | 15/05/2019 14:21
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ApiAdventurersLogRequest {
    private static final Logger log = LoggerFactory.getLogger(ApiAdventurersLogRequest.class);
    private final SubmitGamelogRequest entry;

    public ApiAdventurersLogRequest(final Player player, final AdventurersLogIcon icon, final String message) {
        entry = new SubmitGamelogRequest(player.getUsername(), icon.getLink(), message, false);
    }

    public ApiAdventurersLogRequest(final Player player, final String icon, final String message) {
        entry = new SubmitGamelogRequest(player.getUsername(), icon, message, false);
    }

    public ApiAdventurersLogRequest(final Player player, final AdventurersLogIcon icon, final String message, final boolean pvp) {
        entry = new SubmitGamelogRequest(player.getUsername(), icon.getLink(), message, pvp);
    }

    public String execute() {
        final OkHttpClient http = APIClient.CLIENT;
        final RequestBody body = APIClient.jsonBody(entry);
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("account").addPathSegment("submitGamelog").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        // Make the API request
        try {
            try (Response response = http.newCall(request).execute()) {
                final ResponseBody responseBody = response.body();
                if (responseBody == null || !response.isSuccessful()) {
                    return "empty";
                }
                return responseBody.string();
            }
        } catch (final Exception e) {
            log.error("", e);
        }
        return "empty";
    }
}
