package com.zenyte.api.client.query;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zenyte.api.client.APIClient;
import com.zenyte.game.world.entity.player.Player;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Corey
 * @since 31/05/19
 */
public class DiscordVerificationPost {
    private static final Logger log = LoggerFactory.getLogger(DiscordVerificationPost.class);
    private final Player player;
    private final String verificationCode;

    public DiscordVerificationPost(final Player player, final String verificationCode) {
        this.player = player;
        this.verificationCode = verificationCode;
    }

    public String execute() {
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("discord").addPathSegment("verify").addPathSegment(String.valueOf(player.getPlayerInformation().getUserIdentifier())).build();
        final FormBody body = new FormBody.Builder().add("verificationCode", verificationCode).build();
        final Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = APIClient.CLIENT.newCall(request).execute()) {
            if (response.code() == 200) {
                return "OK";
            }
            try {
                if (response.body() != null) {
                    final String responseBody = response.body().string();
                    final JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
                    return jsonObject.get("message").getAsString();
                } else {
                    log.warn("Invalid response from server; response: " + response);
                    return "Invalid response from server";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Invalid response from server";
            }
        } catch (IOException e) {
            log.error("", e);
            return e.getMessage();
        }
    }
}
