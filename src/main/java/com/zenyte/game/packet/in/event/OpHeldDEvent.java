package com.zenyte.game.packet.in.event;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.packet.in.ClientProtEvent;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.InterfacePosition;
import com.zenyte.game.ui.SwitchPlugin;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.plugins.MethodicPluginHandler;
import com.zenyte.plugins.handlers.InterfaceSwitchHandler;
import com.zenyte.plugins.handlers.InterfaceSwitchPlugin;
import com.zenyte.processor.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Tommeh | 25-1-2019 | 22:20
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class OpHeldDEvent implements ClientProtEvent {
    private final int interfaceId;
    private final int componentId;
    private final int fromSlot;
    private final int toSlot;

    public OpHeldDEvent(final int interfaceId, final int componentId, final int fromSlot, final int toSlot) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
        this.fromSlot = fromSlot;
        this.toSlot = toSlot;
    }

    @Override
    public void log(@NotNull final Player player) {
        log(player, "Interface: " + interfaceId + ", component: " + componentId + ", from slot: " + fromSlot + ", to slot: " + toSlot);
    }

    @Override
    public void handle(Player player) {
        /** Close all input dialogues when switching, to prevent potential dupes in vulnerable code. */
        player.getInterfaceHandler().closeInput();
        player.getInterfaceHandler().closeInterface(InterfacePosition.DIALOGUE);
        final Optional<GameInterface> optionalGameInterface = GameInterface.get(interfaceId);
        if (optionalGameInterface.isPresent()) {
            final GameInterface gameInterface = optionalGameInterface.get();
            final Optional<Interface> optionalPlugin = gameInterface.getPlugin();
            if (optionalPlugin.isPresent()) {
                final Interface plugin = optionalPlugin.get();
                if (plugin instanceof SwitchPlugin) {
                    if (plugin.switchItem(player, componentId, componentId, fromSlot, toSlot)) return;
                }
            }
        }
        final InterfaceSwitchPlugin plugin = InterfaceSwitchHandler.INTERFACES.get(interfaceId);
        /** If a full-script plugin exists for the interface, execute it and prevent code from going further. */
        if (plugin != null) {
            plugin.switchItem(player, interfaceId, interfaceId, componentId, componentId, fromSlot, toSlot);
            return;
        }
        MethodicPluginHandler.invokePlugins(Listener.ListenerType.INTERFACE_SWITCH, player, interfaceId, interfaceId, componentId, componentId, fromSlot, toSlot);
    }

    /**
     * Invoke all methodic plugins.
     */
    @Override
    public LogLevel level() {
        return LogLevel.HIGH_PACKET;
    }
}
