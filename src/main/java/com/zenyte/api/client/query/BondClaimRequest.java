package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.ClaimBondRequest;
import com.zenyte.game.world.entity.player.Player;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Noele
 * see https://noeles.life || noele@zenyte.com
 */
public class BondClaimRequest {
    private static final Logger log = LoggerFactory.getLogger(BondClaimRequest.class);
    private final ClaimBondRequest entry;

    public BondClaimRequest(final Player player, final int bond) {
        this.entry = new ClaimBondRequest(player.getPlayerInformation().getUserIdentifier(), player.getUsername().replaceAll("_", " "), bond, player.getIP());
    }

    public boolean execute() {
        final OkHttpClient http = APIClient.CLIENT;
        final RequestBody body = APIClient.jsonBody(entry);
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("account").addPathSegment("bond").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        // Make the API request
        try {
            try (Response response = http.newCall(request).execute()) {
                final ResponseBody responseBody = response.body();
                if (responseBody == null || !response.isSuccessful()) {
                    return false;
                }
                return Boolean.parseBoolean(responseBody.string());
            }
        } catch (final Exception e) {
            log.error("", e);
        }
        return false;
    }
}
