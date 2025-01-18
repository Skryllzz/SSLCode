package com.zenyte.game.packet.in.event;

import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.ui.ButtonAction;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.NewInterfaceHandler;
import com.zenyte.game.ui.UserInterface;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Tommeh | 25-1-2019 | 21:29
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpModelEvent implements ClientProtEvent {
    private static final Logger log = LoggerFactory.getLogger(OpModelEvent.class);
    private final int interfaceId;
    private final int componentId;

    public OpModelEvent(final int interfaceId, final int componentId) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId);
    }

    @Override
    public void handle(Player player) {
        if (!player.getInterfaceHandler().getVisible().containsValue(interfaceId)) {
            return;
        }
        final Interface plugin = NewInterfaceHandler.getInterface(interfaceId);
        if (plugin != null) {
            Optional<String> opt = plugin.getComponentName(componentId, -1);
            log.info("[" + plugin.getClass().getSimpleName() + "] IF1: " + opt.orElse("Absent") + "(" + interfaceId + "::" + componentId + ")");
            plugin.click(player, componentId, -1, -1, -1);
            return;
        }
        final UserInterface inter = ButtonAction.INTERFACES.get(interfaceId);
        if (inter != null) {
            inter.handleComponentClick(player, interfaceId, componentId, -1, -1, -1, "");
        }
    }

    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
