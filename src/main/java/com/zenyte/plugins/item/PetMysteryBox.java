package com.zenyte.plugins.item;

import com.zenyte.game.content.follower.Follower;
import com.zenyte.game.content.follower.Pet;
import com.zenyte.game.content.follower.impl.MiscPet;
import com.zenyte.game.item.Item;
import com.zenyte.game.item.pluginextensions.ItemPlugin;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.container.impl.Inventory;
import com.zenyte.plugins.dialogue.ItemChat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 04/02/2020
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server profile</a>
 */
public class PetMysteryBox extends ItemPlugin {
    private final IntSet availablePets = new IntOpenHashSet();
    private final IntSet availableDragons = new IntOpenHashSet();

    public PetMysteryBox() {
        for (final MiscPet pet : MiscPet.values()) {
            if (pet.ordinal() < MiscPet.SPIRIT_KALPHITE.ordinal() || pet.ordinal() > MiscPet.CHAMELEON_10.ordinal()) {
                continue;
            }
            if (pet == MiscPet.BLACK_BABYDRAGON || pet == MiscPet.BLUE_BABYDRAGON || pet == MiscPet.GREEN_BABYDRAGON || pet == MiscPet.RED_BABYDRAGON) {
                availableDragons.add(pet.getItemId());
            } else {
                availablePets.add(pet.getItemId());
            }
        }
    }

    @Override
    public void handle() {
        bind("Open", (player, item, container, slotId) -> {
            final IntOpenHashSet availableDragons = new IntOpenHashSet();
            for (final Integer dragon : this.availableDragons) {
                if (possessesItem(player, dragon)) {
                    continue;
                }
                availableDragons.add(dragon.intValue());
            }
            final IntOpenHashSet availablePets = new IntOpenHashSet();
            for (final Integer pet : this.availablePets) {
                if (possessesItem(player, pet)) {
                    continue;
                }
                availablePets.add(pet.intValue());
            }
            if (availableDragons.isEmpty() && availablePets.isEmpty()) {
                player.sendMessage("You already possess all the pets you can possibly find from the pet mystery box.");
                return;
            }
            final Integer pet = (!availableDragons.isEmpty() && (Utils.random(24) == 0 || availablePets.isEmpty())) ? Utils.getRandomCollectionElement(availableDragons) : Utils.getRandomCollectionElement(availablePets);
            final Inventory inventory = player.getInventory();
            inventory.deleteItem(slotId, item);
            final Item petItem = new Item(pet);
            inventory.addOrDrop(petItem);
            player.getDialogueManager().start(new ItemChat(player, petItem, "You find a " + petItem.getName().toLowerCase() + " from the pet mystery box!"));
        });
    }

    private boolean possessesItem(@NotNull final Player player, final int id) {
        final Follower follower = player.getFollower();
        final Pet pet = follower == null ? null : follower.getPet();
        return (pet != null && pet.itemId() == id) || player.containsItem(id);
    }

    @Override
    public int[] getItems() {
        return new int[]{30031};
    }
}
