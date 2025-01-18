package com.zenyte.game.packet.out;

import com.zenyte.game.item.Item;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ItemContainer;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.RSBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tommeh | 28 jul. 2018 | 18:59:29
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class UpdateInvPartial implements GamePacketEncoder {
    private int key;
    private int interfaceId;
    private int componentId;
    private int[] slots;
    private ItemContainer items;
    private Container container;

    public UpdateInvPartial(final int key, final int interfaceId, final int componentId, final ItemContainer items, final int... slots) {
        this.interfaceId = interfaceId;
        this.componentId = componentId;
        this.key = key;
        this.slots = slots;
        this.items = items;
    }

    public UpdateInvPartial(final Container container) {
        this.container = container;
    }

    @Override
    public void log(@NotNull final Player player) {
        if (container == null) {
            return;
        }
        log(player, "Type: " + container.getType().getName());
    }

    @Override
    public GamePacketOut encode() {
        final ServerProt prot = ServerProt.UPDATE_INV_PARTIAL;
        final RSBuffer buffer = new RSBuffer(prot);
        if (container != null) {
            final ContainerType type = container.getType();
            buffer.writeInt(type.getInterfaceId() << 16 | type.getComponentId());
            buffer.writeShort(type.getId());
            for (final Integer slot : container.getModifiedSlots()) {
                buffer.writeSmart(slot);
                int id = -1;
                int amount = 0;
                final Item item = container.get(slot);
                if (item != null) {
                    id = item.getId();
                    amount = item.getAmount();
                }
                buffer.writeShort(id + 1);
                if (id != -1) {
                    final int count = Math.min(amount, 255);
                    buffer.writeByte(count);
                    if (amount >= 255) {
                        buffer.writeInt(amount);
                    }
                }
            }
            Container.AWAITING_RESET_CONTAINERS.add(container);
            return new GamePacketOut(prot, buffer);
        }
        buffer.writeInt(interfaceId << 16 | componentId);
        buffer.writeShort(key);
        for (final int slotId : slots) {
            if (slotId >= items.getItems().length) {
                continue;
            }
            buffer.writeSmart(slotId);
            int id = -1;
            int amount = 0;
            final Item item = items.getItems()[slotId];
            if (item != null) {
                id = item.getId();
                amount = item.getAmount();
            }
            buffer.writeShort(id + 1);
            if (id != -1) {
                final int count = Math.min(amount, 255);
                buffer.writeByte(count);
                if (amount >= 255) {
                    buffer.writeInt(amount);
                }
            }
        }
        return new GamePacketOut(prot, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }
}
