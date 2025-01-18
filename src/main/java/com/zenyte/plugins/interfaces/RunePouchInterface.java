package com.zenyte.plugins.interfaces;

import com.zenyte.game.constants.GameInterface;
import com.zenyte.game.content.skills.magic.Rune;
import com.zenyte.game.item.Item;
import com.zenyte.game.packet.PacketDispatcher;
import com.zenyte.game.ui.Interface;
import com.zenyte.game.ui.SwitchPlugin;
import com.zenyte.game.util.AccessMask;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.Container;
import com.zenyte.game.world.entity.player.container.impl.RunePouch;

/**
 * @author Tommeh | 26 mrt. 2018 : 18:30:34
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>
 */
public final class RunePouchInterface extends Interface implements SwitchPlugin {
    @Override
    protected void attach() {
        put(4, "Withdraw");
        put(8, "Deposit");
    }

    @Override
    public void open(final Player player) {
        player.getInterfaceHandler().sendInterface(this);
        final int id = player.getNumericTemporaryAttribute("rune_pouch").intValue();
        final RunePouch pouch = id == 12791 ? player.getRunePouch() : player.getSecondaryRunePouch();
        final Container container = pouch.getContainer();
        final PacketDispatcher dispatcher = player.getPacketDispatcher();
        container.setFullUpdate(true);
        container.refresh(player);
        dispatcher.sendComponentSettings(getInterface(), getComponent("Withdraw"), 0, 2, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2, AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP10, AccessMask.DRAG_DEPTH2, AccessMask.DRAG_TARGETABLE);
        dispatcher.sendComponentSettings(getInterface(), getComponent("Deposit"), 0, 27, AccessMask.CLICK_OP1, AccessMask.CLICK_OP2, AccessMask.CLICK_OP3, AccessMask.CLICK_OP4, AccessMask.CLICK_OP10, AccessMask.DRAG_DEPTH2, AccessMask.DRAG_TARGETABLE);
    }

    @Override
    protected void build() {
        bind("Deposit", "Withdraw", (player, fromSlot, toSlot) -> {
            final int id = player.getNumericTemporaryAttribute("rune_pouch").intValue();
            final RunePouch pouch = id == 12791 ? player.getRunePouch() : player.getSecondaryRunePouch();
            final Rune rune = Rune.getRune(player.getInventory().getItem(fromSlot));
            if (rune == null) {
                player.sendMessage("You can only add runes to the rune pouch.");
                return;
            }
            if (pouch.getRune(toSlot) != null) {
                withdraw(player, toSlot, 3);
            }
            deposit(player, fromSlot, 3);
        });
        bind("Withdraw", "Deposit", (player, fromSlot, toSlot) -> {
            if (player.getInventory().getItem(toSlot) != null) {
                if (player.getInventory().checkSpace()) {
                    withdraw(player, fromSlot, 3);
                    deposit(player, toSlot, 3);
                }
            } else {
                withdraw(player, fromSlot, 3);
            }
        });
        bind("Withdraw", (player, slotId, itemId, option) -> {
            withdraw(player, slotId, option);
        });
        bind("Deposit", (player, slotId, itemId, option) -> {
            deposit(player, slotId, option);
        });
        bind("Withdraw", "Withdraw", (player, fromSlot, toSlot) -> {
            final int id = player.getNumericTemporaryAttribute("rune_pouch").intValue();
            final RunePouch pouch = id == 12791 ? player.getRunePouch() : player.getSecondaryRunePouch();
            pouch.switchItem(fromSlot, toSlot);
        });
        bind("Deposit", "Deposit", (player, fromSlot, toSlot) -> player.getInventory().switchItem(fromSlot, toSlot));
    }

    private void withdraw(final Player player, final int slotId, final int option) {
        final int id = player.getNumericTemporaryAttribute("rune_pouch").intValue();
        final RunePouch pouch = id == 12791 ? player.getRunePouch() : player.getSecondaryRunePouch();
        if (option >= 1 && option <= 3) {
            pouch.getContainer().withdraw(player, player.getInventory().getContainer(), slotId, option == 1 ? 1 : option == 2 ? 5 : pouch.getRune(slotId).getAmount());
            pouch.getContainer().refresh(player);
            player.getInventory().refreshAll();
        } else if (option == 4) {
            player.sendInputInt("Enter amount:", amount -> {
                pouch.getContainer().withdraw(player, player.getInventory().getContainer(), slotId, amount);
                pouch.getContainer().refresh(player);
                player.getInventory().refreshAll();
            });
        }
    }

    private void deposit(final Player player, final int slotId, final int option) {
        final int id = player.getNumericTemporaryAttribute("rune_pouch").intValue();
        final RunePouch pouch = id == 12791 ? player.getRunePouch() : player.getSecondaryRunePouch();
        final Item item = player.getInventory().getItem(slotId);
        if (item == null) {
            return;
        }
        final Rune r = Rune.getRune(item);
        if (r == null) {
            player.sendMessage("You can only add runes to the rune pouch.");
            return;
        }
        if (pouch.getContainer().getSize() == 3) {
            if (pouch.getAmountOf(item.getId()) == 0) {
                player.sendMessage("You can only carry three different types of runes in your rune pouch at a time.");
                return;
            }
        }
        if (option >= 1 && option <= 3) {
            int amount = option == 1 ? 1 : option == 2 ? 5 : player.getInventory().getItem(slotId).getAmount();
            final int inPouch = pouch.getAmountOf(item.getId());
            if ((amount + (long) inPouch) >= 16000) {
                amount = 16000 - inPouch;
            }
            if (amount <= 0) {
                player.sendMessage("You can't put that many runes in your pouch.");
                return;
            }
            pouch.getContainer().deposit(player, player.getInventory().getContainer(), slotId, amount);
            pouch.getContainer().refresh(player);
            player.getInventory().refreshAll();
        } else if (option == 4) {
            player.sendInputInt("Enter amount:", amount -> {
                final int inPouch = pouch.getAmountOf(item.getId());
                if ((amount + (long) inPouch) >= 16000) {
                    amount = 16000 - inPouch;
                }
                if (amount <= 0) {
                    return;
                }
                pouch.getContainer().deposit(player, player.getInventory().getContainer(), slotId, amount);
                pouch.getContainer().refresh(player);
                player.getInventory().refreshAll();
            });
        }
    }

    @Override
    public GameInterface getInterface() {
        return GameInterface.RUNE_POUCH;
    }
}
