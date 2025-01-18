package com.zenyte.api.client.query.hiscores;

import com.zenyte.api.client.APIClient;
import com.zenyte.api.model.ExpMode;
import com.zenyte.api.model.ExpModeUpdate;
import com.zenyte.game.world.entity.player.Player;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author Corey
 * @since 05/05/19
 */
public class UpdateHiscoreExpMode {
    private static final Logger log = LoggerFactory.getLogger(UpdateHiscoreExpMode.class);
    private final Player player;
    private final ExpMode oldMode;
    private final ExpMode newMode;

    public UpdateHiscoreExpMode(final Player player, final ExpMode oldMode, final ExpMode newMode) {
        this.player = player;
        this.oldMode = oldMode;
        this.newMode = newMode;
    }

    public void execute() {
        if (player.inArea("Tutorial Island")) {
            log.info("User '" + player.getName() + "' in tutorial island, holding off sending hiscores data");
            return;
        }
        final String username = player.getUsername();
        final OkHttpClient http = APIClient.CLIENT;
        final RequestBody body = APIClient.jsonBody(new ExpModeUpdate(oldMode, newMode, player.getGameMode().getApiRole()));
        final HttpUrl url = APIClient.urlBuilder().addPathSegment("hiscores").addPathSegment("user").addPathSegment(username.replaceAll("_", " ")).addPathSegment("update").addPathSegment("expmode").build();
        final Request request = new Request.Builder().url(url).post(body).build();
        try {
            final Response response = http.newCall(request).execute();
            response.close();
            log.info("Sent exp mode update to the api for '" + username + "'");
        } catch (final Exception e) {
            log.error("", e);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public ExpMode getOldMode() {
        return this.oldMode;
    }

    public ExpMode getNewMode() {
        return this.newMode;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) return true;
        if (!(o instanceof UpdateHiscoreExpMode other)) return false;
        if (!other.canEqual(this)) return false;
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        if (!Objects.equals(this$player, other$player)) return false;
        final Object this$oldMode = this.getOldMode();
        final Object other$oldMode = other.getOldMode();
        if (!Objects.equals(this$oldMode, other$oldMode)) return false;
        final Object this$newMode = this.getNewMode();
        final Object other$newMode = other.getNewMode();
        return Objects.equals(this$newMode, other$newMode);
    }

    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof UpdateHiscoreExpMode;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $player = this.getPlayer();
        result = result * PRIME + ($player == null ? 43 : $player.hashCode());
        final Object $oldMode = this.getOldMode();
        result = result * PRIME + ($oldMode == null ? 43 : $oldMode.hashCode());
        final Object $newMode = this.getNewMode();
        result = result * PRIME + ($newMode == null ? 43 : $newMode.hashCode());
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "UpdateHiscoreExpMode(player=" + this.getPlayer() + ", oldMode=" + this.getOldMode() + ", newMode=" + this.getNewMode() + ")";
    }
}
