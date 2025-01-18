package com.zenyte.api.client.query;

import com.zenyte.api.client.APIClient;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * @author Kris | 07/06/2019 08:48
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class ApiIPCheck {
    public static final Set<String> invalidIPs = new ObjectOpenHashSet<>(100);
    public static final Set<String> validIPs = new ObjectOpenHashSet<>(100);
    private static final Logger log = LoggerFactory.getLogger(ApiIPCheck.class);

    public boolean execute(final String ip) {
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("ip").addPathSegment("check").addPathSegment(ip).build();
        final Request request = new Request.Builder().url(url).get().build();
        try (Response response = APIClient.CLIENT.newCall(request).execute()) {
            final ResponseBody responseBody = response.body();
            if (responseBody == null || !response.isSuccessful()) {
                return true;
            }
            final String string = responseBody.string();
            if (string.equals("true")) {
                validIPs.add(ip);
                return true;
            }
            invalidIPs.add(ip);
            return false;
        } catch (IOException e) {
            log.error("", e);
            return true;
        }
    }
}
