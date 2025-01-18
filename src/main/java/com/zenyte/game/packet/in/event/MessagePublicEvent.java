package com.zenyte.game.packet.in.event;

import com.zenyte.game.content.clans.ClanChannel;
import com.zenyte.game.content.clans.ClanManager;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.packet.out.clan_channel.ChatChannelType;
import com.zenyte.game.world.entity.masks.UpdateFlag;
import com.zenyte.game.world.entity.player.GameCommands;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.Privilege;
import com.zenyte.game.world.entity.player.punishments.Punishment;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Tommeh | 25-1-2019 | 21:23
 * @author Jire
 */
public class MessagePublicEvent implements ClientProtEvent {

    private final int type;
    private final int colour;
    private final int effect;
    private final String message;
    private final int clanType;

    @Override
    public void handle(Player player) {
        final int effects = ((colour & 255) << 8) | (effect & 255);
        if (player.getPrivilege().eligibleTo(Privilege.SUPPORT) && message.startsWith(";;")) {
            GameCommands.process(player, message.substring(2));
            return;
        }
        final Optional<Punishment> punishment = PunishmentManager.isPunishmentActive(player.getUsername(), player.getIP(), PunishmentType.MUTE);
        if (punishment.isPresent()) {
            player.sendMessage("You cannot talk while the punishment is active: " + punishment.get() + ".");
            return;
        }
        switch (type) {
            case 2: {
                final ClanChannel channel = player.getSettings().getChannel();
                if (channel != null) {
                    final String clanMessage = message.indexOf('/') == 0
                            ? message.substring(1)
                            : message;
                    ClanManager.message(player, clanMessage);
                    return;
                }
            }
            case 3: {
                if (clanType == ChatChannelType.GIM.getPacketIdentifier()) {
                }
            }
        }
        if (player.getUpdateFlags().get(UpdateFlag.CHAT)) {
            return;
        }
        player.getUpdateFlags().flag(UpdateFlag.CHAT);
        player.getChatMessage().set(message, effects, type == 1);
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Type: " + type + ", colour: " + colour + ", effect: " + effect + ", message: " + message);
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }

    public MessagePublicEvent(int type, int colour, int effect, String message, int clanType) {
        this.type = type;
        this.colour = colour;
        this.effect = effect;
        this.message = message;
        this.clanType = clanType;
    }
}
