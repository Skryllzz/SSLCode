package com.zenyte.game.packet.in.event;

import com.zenyte.Constants;
import com.zenyte.database.structs.PrivateMessage;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.world.entity.masks.ChatMessage;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.punishments.Punishment;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Tommeh | 25-1-2019 | 21:20
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class MessagePrivateEvent implements ClientProtEvent {
    private final ChatMessage message;
    private String recipient;

    public MessagePrivateEvent(final String recipient, final ChatMessage message) {
        this.recipient = recipient;
        this.message = message;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Recipient: " + recipient + ", message: " + message.getChatText());
    }

    @Override
    public void handle(Player player) {
        final Optional<Punishment> punishment = PunishmentManager.isPunishmentActive(player.getUsername(), player.getIP(), PunishmentType.MUTE);
        if (punishment.isPresent()) {
            player.sendMessage("You cannot talk while the punishment is active: " + punishment.get() + ".");
            return;
        }
        if (recipient.length() > 12) {
            recipient = recipient.substring(0, 12);
        }
        player.getSocialManager().sendMessage(recipient, message);
        if (Constants.SQL_ENABLED) {
            PrivateMessage.list.add(new PrivateMessage(player, recipient, message.getChatText()));
        }
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
