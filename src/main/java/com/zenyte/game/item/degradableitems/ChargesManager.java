package com.zenyte.game.item.degradableitems;

import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ChargeExtension;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.ContainerWrapper;
import com.zenyte.game.world.entity.player.container.impl.ContainerType;
import com.zenyte.game.world.entity.player.container.impl.equipment.EquipmentSlot;
import mgi.types.config.items.ItemDefinitions;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.logging.Logger;

/**
 * @author Kris | 28. dets 2017 : 1:44.05
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>}
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status profile</a>}
 */
public final class ChargesManager {
    public static final DecimalFormat FORMATTER = new DecimalFormat("#0.0");
    private static final Logger log = Logger.getLogger(ChargesManager.class.getName());
    private static final int[] SLOTS = new int[]{EquipmentSlot.HELMET.getSlot(), EquipmentSlot.PLATE.getSlot(), EquipmentSlot.LEGS.getSlot(), EquipmentSlot.WEAPON.getSlot(), EquipmentSlot.SHIELD.getSlot()};
    private final Player player;

    public ChargesManager(final Player player) {
        this.player = player;
    }

    /**
     * Loops over the player's worn equipment and removes charges if the item is a degradable item and the type matches.
     *
     * @param type
     */
    public void removeCharges(final DegradeType type) {
        final Container container = player.getEquipment().getContainer();
        for (int slot = SLOTS.length - 1; slot >= 0; slot--) {
            final Item item = container.get(SLOTS[slot]);
            if (item == null) {
                continue;
            }
            final DegradableItem deg = DegradableItem.ITEMS.get(item.getId());
            if (deg == null) {
                continue;
            }
            if (deg.getType() != type) {
                continue;
            }
            final int charges = item.getCharges();
            if (charges != deg.getMaximumCharges() && charges <= 0) {
                continue;
            }
            final ItemPlugin chargesPlugin = ItemPlugin.getPlugin(item.getId());
            final ContainerType containerType = container.getType();
            if (chargesPlugin instanceof ChargeExtension && (containerType == ContainerType.INVENTORY || containerType == ContainerType.EQUIPMENT)) {
                ContainerWrapper wrapper = containerType == ContainerType.INVENTORY ? player.getInventory() : player.getEquipment();
                ((ChargeExtension) chargesPlugin).removeCharges(player, item, wrapper, SLOTS[slot], 1);
                continue;
            }
            final int currentCharges = item.getCharges();
            item.setCharges(charges - 1);
            if (item.getCharges() <= deg.getMinimumCharges()) {
                final int next = deg.getNextId();
                if (deg.equals(DegradableItem.ABYSSAL_TENTACLE)) {
                    player.getEquipment().set(SLOTS[slot], null);
                    player.getInventory().addOrDrop(new Item(next));
                } else if (next == -1) {
                    player.getEquipment().set(SLOTS[slot], null);
                } else if (next != item.getId()) {
                    item.setId(deg.getNextId());
                    final DegradableItem nextDeg = DegradableItem.ITEMS.get(deg.getNextId());
                    if (nextDeg != null) {
                        item.setCharges(nextDeg.getMaximumCharges() - (currentCharges - item.getCharges()));
                    }
                }
                player.getEquipment().refresh(SLOTS[slot]);
                player.getAppearance().setRenderAnimation(player.getAppearance().generateRenderAnimation());
                final String name = ItemDefinitions.getOrThrow(deg.getItemId()).getName();
                player.sendMessage("Your " + name + " " + (name.contains("legs") ? "have" : "has") + (item.getCharges() == 0 ? " fully" : "") + " degraded" + (deg.getNextId() == -1 ? " and turned to dust." : "."));
            }
        }
    }

    /**
     * Removes the defined amount of charges from the item in arguments. The item nor its container can be null.
     *
     * @param item      the item to remove charges from, can't be null, nor can its container.
     * @param amount
     * @param container
     * @param slotId
     */
    public void removeCharges(@NotNull final Item item, final int amount, Container container, int slotId) {
        if (item == null) {
            throw new NullPointerException("item is marked non-null but is null");
        }
        final ItemPlugin chargesPlugin = ItemPlugin.getPlugin(item.getId());
        final ContainerType containerType = container.getType();
        if (chargesPlugin instanceof ChargeExtension && (containerType == ContainerType.INVENTORY || containerType == ContainerType.EQUIPMENT)) {
            ContainerWrapper wrapper = containerType == ContainerType.INVENTORY ? player.getInventory() : player.getEquipment();
            ((ChargeExtension) chargesPlugin).removeCharges(player, item, wrapper, slotId, amount);
            return;
        }
        final int charges = item.getCharges();
        final DegradableItem deg = DegradableItem.ITEMS.get(item.getId());
        if (deg == null) {
            log.info("Unable to remove charges from item: " + item);
            return;
        }
        final int currentCharges = item.getCharges();
        item.setCharges(charges - amount);
        final boolean equipment = containerType == ContainerType.EQUIPMENT;
        if (item.getCharges() <= deg.getMinimumCharges()) {
            final int nextId = deg.getNextId();
            final DegradableItem nextDeg = DegradableItem.ITEMS.get(deg.getNextId());
            if (nextDeg != null) {
                item.setCharges(nextDeg.getMaximumCharges() - (currentCharges - item.getCharges()));
            }
            container.set(slotId, nextId == -1 ? null : new Item(nextId, item.getAmount(), item.getAttributesCopy()));
            container.refresh(player);
            if (equipment) {
                player.getEquipment().refresh();
            }
            final String name = ItemDefinitions.getOrThrow(deg.getItemId()).getName();
            player.sendMessage("Your " + name + " " + (name.contains("legs") ? "have" : "has") + (item.getCharges() == 0 ? " fully" : "") + " degraded" + (deg.getNextId() == -1 ? " and turned to dust." : "."));
        }
    }

    public void checkCharges(@NotNull final Item item) {
        if (item == null) {
            throw new NullPointerException("item is marked non-null but is null");
        }
        checkCharges(item, true);
    }

    /**
     * Checks the charges of the item and informs the player of it.
     *
     * @param item the item to check the charges of.
     */
    public void checkCharges(@NotNull final Item item, final boolean filterable) {
        if (item == null) {
            throw new NullPointerException("item is marked non-null but is null");
        }
        final ItemPlugin chargesPlugin = ItemPlugin.getPlugin(item.getId());
        if (chargesPlugin instanceof ChargeExtension) {
            ((ChargeExtension) chargesPlugin).checkCharges(player, item);
            return;
        }
        final String name = item.getName();
        final int fullCharges = DegradableItem.getDefaultCharges(item.getId(), -1);
        if (item.getCharges() == fullCharges) {
            final String payload = item.getName().toLowerCase().endsWith("s") ? " are fully charged." : " is fully charged.";
            player.sendMessage("Your " + item.getName() + payload);
            return;
        }
        if (item.getCharges() <= 0) {
            final String payload = item.getName().toLowerCase().endsWith("s") ? " are completely degraded" : " is completely degraded";
            player.sendMessage("Your " + item.getName() + payload);
            return;
        }
        final DegradableItem deg = DegradableItem.ITEMS.get(item.getId());
        if (deg == null) {
            log.info("Unable to check charges of item: " + item);
            return;
        }
        if (deg.getType() == DegradeType.RECOIL || deg.getType() == DegradeType.USE) {
            player.sendMessage("Your " + name + " has " + item.getCharges() + " charge" + (item.getCharges() == 1 ? "" : "s") + " remaining.");
            return;
        }
        final String percentage = FORMATTER.format(item.getCharges() / (float) DegradableItem.getFullCharges(item.getId()) * 100);
        player.sendMessage("Your " + name + " " + (name.contains("legs") ? "have " : "has ") + percentage.replace(".0", "") + "% charges remaining.");
    }
}
