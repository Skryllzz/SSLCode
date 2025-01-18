package com.zenyte.api.client.webhook;

import com.zenyte.api.client.webhook.model.DiscordWebhook;
import com.zenyte.api.client.webhook.model.EmbedObject;
import com.zenyte.game.world.entity.player.GameMode;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalBroadcastWebhook extends Webhook {
    private static final Logger log = LoggerFactory.getLogger(GlobalBroadcastWebhook.class);
    private static final String ICON_URL_TEMPLATE = "https://zenyte.com/img/adventure-icons/%s";
    private static boolean disabled;
    private final String title;
    private final String icon;
    private final String message;

    public GlobalBroadcastWebhook(final String icon, final String message, final String title) {
        super("620329853362634803", "6H8FMEK9DA9p5PExt0ILRQGShSqrvD6rrTo4noNpa6gTZj4kR-eRm7CQqwJ1AWOLHrgO");
        this.title = title;
        this.icon = String.format(ICON_URL_TEMPLATE, icon);
        this.message = message;
    }

    public GlobalBroadcastWebhook(final int icon, final String message, final String title) {
        this(icon + ".png", message, title);
    }

    private static String emojiFromCrownId(final int crownId) {
        if (crownId == GameMode.STANDARD_IRON_MAN.getIcon()) {
            return "<:ironman:633279727641165825>";
        } else if (crownId == GameMode.ULTIMATE_IRON_MAN.getIcon()) {
            return "<:ultimate_ironman:633279727938961408>";
        } else if (crownId == GameMode.HARDCORE_IRON_MAN.getIcon()) {
            return "<:hardcore_ironman:633279727938830356>";
        }
        return "";
    }

    private static String replaceImgWithEmoji(String toReplace) {
        final String pattern = "(<img=(\\d+)>)";
        final Pattern r = Pattern.compile(pattern);
        final Matcher m = r.matcher(toReplace);
        while (m.find()) {
            final String emoji = emojiFromCrownId(NumberUtils.toInt(m.group(2), -1));
            toReplace = toReplace.replace(m.group(1), emoji);
        }
        return toReplace.replaceAll("<(shad|img|col)=.*>", ""); // remove any tags left over that we haven't already replaced
    }

    public static boolean isDisabled() {
        return GlobalBroadcastWebhook.disabled;
    }

    public static void setDisabled(final boolean disabled) {
        GlobalBroadcastWebhook.disabled = disabled;
    }

    @Override
    public DiscordWebhook buildMessage() {
        return DiscordWebhook.builder().embed(EmbedObject.builder().title(title).description(replaceImgWithEmoji(message)).color(Webhook.EMBED_COLOUR).thumbnail(new EmbedObject.Thumbnail(icon)).build()).build();
    }
}
