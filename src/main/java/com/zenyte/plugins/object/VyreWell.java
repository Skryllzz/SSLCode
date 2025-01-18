package com.zenyte.plugins.object;

import com.zenyte.game.item.Item;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.game.world.entity.player.dialogue.Dialogue;
import com.zenyte.game.world.entity.player.var.VarCollection;
import com.zenyte.game.world.object.NullObjectID;
import com.zenyte.game.world.object.ObjectAction;
import com.zenyte.game.world.object.ObjectId;
import com.zenyte.game.world.object.WorldObject;
import com.zenyte.plugins.dialogue.PlainChat;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 18/01/2019 16:34
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class VyreWell implements ObjectAction {

    @Override
    public void handleObjectAction(Player player, WorldObject object, String name, int optionId, String option) {
        switch(option) {
            case "Fill":
                player.getDialogueManager().start(new Dialogue(player) {

                    @Override
                    public void buildDialogue() {
                        plain("Mixing 1 vial of blood with the power of 300 blood runes into this well will add 100 charges per set.").executeAction(() -> {
                            player.getDialogueManager().finish();
                            int availableCharges = 20;
                            final int vials = player.getInventory().getAmountOf(22446) + player.getInventory().getAmountOf(22447);
                            final int setsOfBlood = player.getInventory().getAmountOf(565) / 300;
                            availableCharges = Math.min(vials, availableCharges);
                            availableCharges = Math.min(setsOfBlood, availableCharges);
                            if (availableCharges <= 0) {
                                player.getDialogueManager().start(new PlainChat(player, "You haven't got enough " + (vials == 0 ? "vials of blood" : "blood runes") + " to fill the well."));
                                return;
                            }
                            chargeWell(player, availableCharges);
                        });
                    }
                });
                break;
            case "Check":
                {
                    final int existingCharges = player.getNumericAttribute("vyre well charges").intValue();
                    player.getDialogueManager().start(new PlainChat(player, "The well contains " + existingCharges + " charge" + (existingCharges == 1 ? "" : "s") + "."));
                    break;
                }
            case "Empty":
                {
                    final int existingCharges = Math.min(player.getNumericAttribute("vyre well charges").intValue(), 20);
                    if (existingCharges <= 0) {
                        //Should never happen, but refresh the varbit just in case if it does.
                        VarCollection.VYRE_WELL.update(player);
                        player.getDialogueManager().start(new PlainChat(player, "The well is already empty."));
                        return;
                    }
                    player.addAttribute("vyre well charges", 0);
                    VarCollection.VYRE_WELL.update(player);
                    player.getInventory().addItem(new Item(565, existingCharges * 300)).onFailure(item -> World.spawnFloorItem(item, player));
                    player.getInventory().addItem(new Item(22447, existingCharges)).onFailure(item -> World.spawnFloorItem(item, player));
                    player.getDialogueManager().start(new PlainChat(player, "You empty the well."));
                    break;
                }
            case "Charge-at":
                final Inventory inventory = player.getInventory();
                for (int i = 0; i < 28; i++) {
                    final Item item = inventory.getItem(i);
                    if (item == null)
                        continue;
                    final int id = item.getId();
                    if (id == 22486 || id == 22325 || id == 22323 || id == 22481) {
                        if (item.getCharges() <= 19900) {
                            appendCharges(player, item);
                            return;
                        }
                    }
                }
                player.sendMessage("You haven't got any weapons needing charging.");
                break;
        }
    }

    private static void chargeWell(@NotNull final Player player, final int availableCharges) {
        player.sendInputInt("How many sets of 100 charges do you wish to add? (Up to " + availableCharges + ")", number -> {
            final int existingCharges = player.getNumericAttribute("vyre well charges").intValue();
            if (existingCharges >= 20) {
                player.getDialogueManager().start(new PlainChat(player, "The well is already full."));
                return;
            }
            int amount = Math.min(20 - existingCharges, number);
            final int vials = player.getInventory().getAmountOf(22446) + player.getInventory().getAmountOf(22447);
            final int setsOfBlood = player.getInventory().getAmountOf(565) / 300;
            amount = Math.min(vials, amount);
            amount = Math.min(setsOfBlood, amount);
            if (amount <= 0) {
                player.getDialogueManager().start(new PlainChat(player, "You haven't got enough " + (vials == 0 ? "vials of blood" : "blood runes") + " to fill the well."));
                return;
            }
            player.getInventory().deleteItem(new Item(22446, amount)).onFailure(remainder -> player.getInventory().deleteItem(new Item(22447, remainder.getAmount())));
            player.getInventory().deleteItem(new Item(565, amount * 300));
            player.addAttribute("vyre well charges", existingCharges + amount);
            VarCollection.VYRE_WELL.update(player);
            player.getDialogueManager().start(new PlainChat(player, "You fill the well with " + amount + " charge" + (amount == 1 ? "" : "s") + "."));
        });
    }

    private static void appendCharges(@NotNull final Player player, @NotNull final Item item) {
        final int id = item.getId();
        player.getDialogueManager().start(new Dialogue(player) {

            @Override
            public void buildDialogue() {
                item(item, "You lower your " + (id == 22486 || id == 22325 ? "scythe" : "staff") + " into the well...").executeAction(() -> {
                    player.getDialogueManager().finish();
                    final int max = Math.min(player.getNumericAttribute("vyre well charges").intValue(), 20);
                    final int charges = (int) Math.min(max, Math.floor((20000 - item.getCharges()) / 100.0F));
                    if (charges == 0) {
                        //Should never happen.
                        VarCollection.VYRE_WELL.update(player);
                        player.getDialogueManager().start(new PlainChat(player, "The well is already empty."));
                        return;
                    }
                    player.sendInputInt("How many sets of 100 charges do you wish to apply? (Up to " + charges + ")", number -> {
                        final int existingCharges = Math.min(player.getNumericAttribute("vyre well charges").intValue(), 20);
                        final int amount = (int) Math.min(Math.min(number, existingCharges), Math.floor((20000 - item.getCharges()) / 100.0F));
                        if (amount == 0) {
                            //Should never happen.
                            return;
                        }
                        if (item.getId() == 22486) {
                            item.setId(22325);
                        } else if (item.getId() == 22481) {
                            item.setId(22323);
                        }
                        item.setCharges(item.getCharges() + (amount * 100));
                        player.addAttribute("vyre well charges", existingCharges - amount);
                        VarCollection.VYRE_WELL.update(player);
                        player.getInventory().refreshAll();
                        player.getDialogueManager().start(new Dialogue(player) {

                            @Override
                            public void buildDialogue() {
                                item(item, "You apply " + Utils.format((amount * 100)) + " charges to your " + item.getName().toLowerCase() + ".");
                            }
                        });
                    });
                });
            }
        });
    }

    @Override
    public Object[] getObjects() {
        return new Object[] { ObjectId.VYRE_WELL_32985, ObjectId.VYRE_WELL, NullObjectID.NULL_33085 };
    }
}
